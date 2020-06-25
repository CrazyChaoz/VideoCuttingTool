package at.jku.videocuttingtool.backend;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * Backend of the Basic Video cutting tool
 */
public class Backend {
    private final List<File> files = new ArrayList<>();
    private File workingDir;

    private static final String sep = File.separator;
    private static final String ffmpeg = "lib" + sep + "ffmpeg" + sep + "ffmpeg.exe";

    /**
     * add sources that are to be used in the project
     *
     * @param sources a list of Files
     */
    public void addSources(List<File> sources) {
        files.addAll(sources);
    }

    /**
     * load all file from the working directory
     */
    public void loadFromWorkingDir() {
        List<File> files = new ArrayList<>();
        listf(workingDir, files);
        addSources(files);
    }

    /**
     * set the working directory
     *
     * @param dir the working directory
     */
    public void setWorkingDir(File dir) {
        this.workingDir = dir;
    }

    /**
     * method to export a given timeline with its export parameters
     *
     * @param export the export container with the given export parameters
     * @throws IOException          if an error occurs while cutting the clips
     * @throws InterruptedException if an error occurs while executing the ffmpeg commands
     */
    public void export(Export export) throws IOException, InterruptedException, EditMediaException {
        File exportDir = export.getExport().getParentFile();

        if(exportDir==null) {
            exportDir = workingDir;
        }

        if (!exportDir.isDirectory()) {
            return;
        }

        //generate temporary directories for the export
        File vDir = new File(exportDir + sep + "outV");
        vDir.mkdir();
        File aDir = new File(exportDir + sep + "outA");
        aDir.mkdir();

        String vF = export.getVideoFormat();
        String aF = export.getAudioFormat();
        File vMerged = null, aMerged = null;
        boolean hasVideo = false, hasAudio = false;

        /*
         * if there are more than 1 files in the video or audio timeline
         * merge them together
         */
        Process merge;
        if (export.getTimeline().getVideo().size() > 0) {
            if (export.getTimeline().getVideo().size() == 1) {
                vMerged = export.getTimeline().getVideo().get(0).getMedia();
            } else {
                vMerged = new File(vDir + sep + "v_export." + vF);
                merge = mergeMedia(export.getTimeline().getVideo(), vMerged);
                if (merge != null && merge.waitFor() != 0) {
                    throw new EditMediaException(merge.getErrorStream());
                }
            }
            hasVideo = true;
        }

        if (export.getTimeline().getAudio().size() > 0) {
            if (export.getTimeline().getAudio().size() == 1) {
                aMerged = export.getTimeline().getAudio().get(0).getMedia();
            } else {
                aMerged = new File(aDir + sep + "a_export." + aF);
                merge = mergeMedia(export.getTimeline().getAudio(), aMerged);
                if (merge != null && merge.waitFor() != 0) {
                    throw new EditMediaException(merge.getErrorStream());
                }
            }
            hasAudio = true;
        }

        /*
         * check if there is audio overlapping the video
         * if there is merge the extra audio with the video and its audio
         */
        if (hasVideo && hasAudio){
            String extractAudio = new Exec(ffmpeg)
                    .addInput(vMerged.getAbsolutePath())
                    .addArg("-y")
                    .addOutput(aDir + sep + "v_audio." + aF)
                    .done();
            String mergeAudio = new Exec(ffmpeg)
                    .addInput(aMerged.getAbsolutePath())
                    .addInput(aDir + sep + "v_audio." + aF)
                    .addArg("-filter_complex amerge -c:a libmp3lame -q:a 4 -y")
                    .addOutput(aDir + sep + "final_audio." + aF)
                    .done();
            String mergeAudioAndVideo = new Exec(ffmpeg)
                    .addInput(vMerged.getAbsolutePath())
                    .addInput(aDir + sep + "final_audio." + aF)
                    .addArg("-map 0:v -map 1:a -c copy -y")
                    .addOutput(export.getExport().getAbsolutePath())
                    .done();

            Process p;
            if ((p = Runtime.getRuntime().exec(extractAudio)).waitFor() != 0) {
                throw new EditMediaException(p.getErrorStream());
            }
            if ((p = Runtime.getRuntime().exec(mergeAudio)).waitFor() != 0) {
                throw new EditMediaException(p.getErrorStream());
            }
            if ((p = Runtime.getRuntime().exec(mergeAudioAndVideo)).waitFor() != 0) {
                throw new EditMediaException(p.getErrorStream());
            }
        } else if (hasVideo) {
            Files.copy(vMerged.toPath(),export.getExport().toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else if (hasAudio) {
            Files.copy(aMerged.toPath(),export.getExport().toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        delDir(vDir);
        delDir(aDir);
    }

    /**
     * merge a set amount of media files
     * can either be audio or video
     *
     * @param media a List of @see{@link Clip} Objects
     * @param merge output directory+file for exported merged file
     * @return a Process to be executed be the caller
     * @throws IOException          if an error occurs while cutting the clips
     * @throws InterruptedException if an error occurs while executing the ffmpeg commands
     */
    private Process mergeMedia(List<Clip> media, File merge) throws IOException, InterruptedException, EditMediaException {
        File dir = merge.getParentFile();
        String format = merge.getName().substring(merge.getName().lastIndexOf('.'));
        if (!dir.isDirectory() || format.isEmpty()) {
            return null;
        }

        /*
         * apply the start and end time if available to the clip
         * -> cut the clip to size
         */
        File tmp = new File(dir + sep + "ffmpeg_merge.txt");
        StringBuilder files = new StringBuilder();
        for (Clip c : media) {
            String partName = dir.getAbsolutePath() + sep + "part_" + c.getPos() + format;
            Optional<Process> p = cutClip(c, partName);
            if (p.isPresent()) {
                if (p.get().waitFor() != 0) {
                    throw new EditMediaException(p.get().getErrorStream());
                }
                files.append("file '").append(partName).append("'\n");
            } else {
                files.append("file '").append(c.getMedia().getAbsolutePath()).append("'\n");
            }
        }
        Files.write(tmp.toPath(), files.toString().getBytes());

        String concat = new Exec(ffmpeg)
                .addArg("-f concat -safe 0")
                .addInput(tmp.getAbsolutePath())
                .addArg("-c copy -y")
                .addOutput(merge.getAbsolutePath())
                .done();

        return Runtime.getRuntime().exec(concat);
    }

    /**
     * @param clip   a single @see{@link Clip} object that should be cut
     * @param export the complete filepath (incl. file location/filename) of the exported clip
     * @return an Optional Process to be executed be the caller
     * this is empty if no cut parameters for the clip are set
     * @throws IOException if an error occurs while cutting the clips
     */
    private Optional<Process> cutClip(Clip clip, String export) throws IOException {
        if (clip.getStart().isEmpty() && clip.getEnd().isEmpty()) {
            return Optional.empty();
        }

        Exec cut = new Exec(ffmpeg);

        if (!clip.getStart().isEmpty()) {
            cut.addArg("-ss " + clip.getStart());
        }

        cut.addInput(clip.getMedia().getAbsolutePath())
                .addArg("-c copy");

        if (!clip.getEnd().isEmpty()) {
            cut.addArg("-to " + clip.getEnd());
        }

        cut.addArg(" -y ");
        cut.addOutput(export);

        return Optional.of(Runtime.getRuntime().exec(cut.done()));
    }

    /**
     * method to save a project
     *
     * @param tm the @see{@link Timeline} object to save
     * @param to the file to save to
     * @throws IOException if an error while writing to the file occurs
     */
    public void saveProject(Timeline tm, File to) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(to))) {
            out.write(tm.getVideo().stream()
                    .map(Clip::toString)
                    .collect(joining("\n")));
            out.write("\n->\n");
            out.write(tm.getAudio().stream()
                    .map(Clip::toString)
                    .collect(joining("\n")));
        }
    }

    /**
     * method to load from a saved project file
     *
     * @param from the project file to load from
     * @return the loaded @see{@link Timeline} instance
     * @throws IOException if an error occurs while reading from the given file
     */
    public Timeline loadProject(File from) throws IOException {
        Timeline tm = new Timeline();
        try (BufferedReader in = new BufferedReader(new FileReader(from))) {
            Iterator<String> lines = in.lines().iterator();
            while (lines.hasNext()) {
                String next = lines.next();
                if (next.equals("->")) break;
                tm.addVideo(Clip.parse(next));
            }
            while (lines.hasNext()) {
                tm.addAudio(Clip.parse(lines.next()));
            }
        }
        return tm;
    }

    /**
     * list all files in a give directory recursively
     *
     * @param dir   the directory to return the files from
     * @param files a reference to a List object to save the files into
     */
    private static void listf(File dir, List<File> files) {
        File[] fList = dir.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listf(file, files);
                }
            }
        }
    }

    /**
     * delete a complete directory with all its contents
     *
     * @param dir the directory to be deleted
     * @throws IOException thrown if an error occurs while deleting the directory
     */
    private static void delDir(File dir) throws IOException {
        Files.walk(dir.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * getter for the source files
     * @return list of files
     */
    public List<File> getSources() {
        return files;
    }
}
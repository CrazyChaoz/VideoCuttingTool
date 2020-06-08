package at.jku.videocuttingtool.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * Backend of the Basic Video cutting tool
 */
public class Backend {
    private final List<File> files = new ArrayList<>();
    private File workingDir;

    private static final String ffmpeg = "lib/ffmpeg/ffmpeg.exe";

    public void addSources(List<File> sources) {
        files.addAll(sources);
    }

    public void loadFromWorkingDir() {
        List<File> files = new ArrayList<>();
        listf(workingDir, files);
        addSources(files);
    }

    public void setWorkingDir(File dir) {
        this.workingDir = dir;
    }

    /**
     * method to export a given timeline with its export parameters
     * @param export the export container with the given export parameters
     * @throws IOException thrown if an error occurs while cutting the clips
     * @throws InterruptedException thrown if an error occurs while executing the ffmpeg commands
     */
    public void export(Export export) throws IOException, InterruptedException {
        if (!export.getExportDir().isDirectory())
            return;

        File vDir = new File(export.getExportDir() + "/outV/");
        vDir.mkdir();
        File aDir = new File(export.getExportDir() + "/outA/");
        aDir.mkdir();

        String vF = export.getVideoFormat();
        String aF = export.getAudioFormat();
        Process merge = mergeMedia(export.getTimeline().getVideo(), vDir, "v_export."+vF, vF);
        if (merge != null) merge.waitFor();
        merge = mergeMedia(export.getTimeline().getAudio(), aDir, "a_export."+aF, aF);
        if (merge != null) merge.waitFor();

        String extractAudio = new Exec(ffmpeg)
                .addInput(vDir + "/v_export."+vF)
                .addOutput(aDir + "/v_audio."+aF)
                .done();
        String mergeAudio = new Exec(ffmpeg)
                .addInput(aDir + "/a_export."+aF)
                .addInput(aDir + "/v_audio."+aF)
                .addArg("-filter_complex amerge -c:a libmp3lame -q:a 4")
                .addOutput(aDir + "/final_audio."+aF)
                .done();
        String mergeAudioAndVideo = new Exec(ffmpeg)
                .addInput(vDir + "/v_export."+vF)
                .addInput(aDir + "/final_audio."+aF)
                .addArg("-map 0:v -map 1:a -c copy -y")
                .addOutput(export.getExportName())
                .done();

        /*new ProcessBuilder(extractAudio).inheritIO().start().waitFor();
        new ProcessBuilder(mergeAudio).inheritIO().start().waitFor();
        new ProcessBuilder(mergeAudioAndVideo).inheritIO().start().waitFor();*/

        Runtime.getRuntime().exec(extractAudio)
                .waitFor();
        Runtime.getRuntime().exec(mergeAudio)
                .waitFor();
        Runtime.getRuntime().exec(mergeAudioAndVideo)
                .waitFor();

        delDir(vDir);
        delDir(aDir);
    }

    /**
     * merge a set amount of media files
     * can either be audio or video
     * @param media a List of @see{@link Clip} Objects
     * @param dir output directory for the temp files
     * @param name name of the merged file
     * @param format media format of the merged file
     * @return a Process to be executed be the caller
     * @throws IOException thrown if an error occurs while cutting the clips
     * @throws InterruptedException thrown if an error occurs while executing the ffmpeg commands
     */
    public Process mergeMedia(Set<Clip> media, File dir, String name, String format) throws IOException, InterruptedException {
        for (Clip c : media) {
            Optional<Process> p = cutClip(c, dir.getAbsolutePath() + "/part_" + c.getPos() + "." + format);
            if (p.isPresent()) p.get().waitFor();
        }

        Path files = createFileList(dir);
        if (files == null)
            return null;

        String concat = new Exec(ffmpeg)
                .addArg("-f concat -safe 0")
                .addInput(files.toAbsolutePath().toString())
                .addArg("-c copy")
                .addOutput(dir + "/" + name)
                .done();

        return Runtime.getRuntime().exec(concat);
    }

    /**
     *
     * @param clip a single @see{@link Clip} object that should be cut
     * @param export the complete filepath (incl. file location/filename) of the exported clip
     * @return an Optional Process to be executed be the caller
     *          this is empty if no cut parameters for the clip are set
     * @throws IOException thrown if an error occurs while cutting the clips
     */
    public Optional<Process> cutClip(Clip clip, String export) throws IOException {
        if (clip.getStart().isEmpty() && clip.getEnd().isEmpty()) {
            Files.copy(clip.getMedia().toPath(), new File(export).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return Optional.empty();
        }

        Exec cut = new Exec(ffmpeg);

        if (!clip.getStart().isEmpty())
            cut.addArg("-ss " + clip.getStart());

        cut.addInput(clip.getMedia().getAbsolutePath())
            .addArg("-c copy");

        if (!clip.getEnd().isEmpty())
            cut.addArg("-to " + clip.getEnd());

        cut.addOutput(export);

        return Optional.of(Runtime.getRuntime().exec(cut.done()));
    }

    public static void main(String[] args) {
        try {
            Backend b = new Backend();

            File test = new File("D:/ShadowPlay/Desktop/f1_1.mp4");
            File test2 = new File("D:/ShadowPlay/Desktop/f1_2.mp4");
            File test3 = new File("D:/ShadowPlay/Desktop/f1_3.mp4");

            Timeline tm = new Timeline();
            tm.addVideo(new Clip(test, 1, "", ""));
            //tm.addVideo(new Clip(test2, 2,"",""));
            //tm.addVideo(new Clip(test3, 3,"",""));

            File audio = new File("D:/OneDrive/JKU/Multimediasysteme/audio/Inception Soundtrack HD - #12 Time (Hans Zimmer).mp3");
            tm.addAudio(new Clip(audio, 0, "00:00:25", "00:00:35"));
            tm.addAudio(new Clip(audio, 1, "00:00:35", "00:00:45"));

            Export ex = new Export(tm, new File("D:/ShadowPlay/Desktop"),  "export.mp4","mp4", "mp3");
            b.export(ex);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a list of files in a given directory
     * @param dir the directory to create the list of
     * @return a Path to the created file
     * @throws IOException if an error occurs while creating the file or accessing the files in the given directory
     */
    private static Path createFileList(File dir) throws IOException {
        if (!dir.isDirectory())
            return null;

        ArrayList<File> outFiles = new ArrayList<>();
        listf(dir, outFiles);
        String filesStrings = outFiles
                .stream()
                .map(f -> f.getAbsolutePath().toString())
                .map(p -> "file '" + p + "'")
                .collect(joining(System.getProperty("line.separator")));
        Path listOfFiles = Files.createTempFile(Paths.get(dir.toURI()), "ffmpeg-list-", ".txt");
        Files.write(listOfFiles, filesStrings.getBytes());

        return listOfFiles;
    }

    /**
     * list all files in a give directory recursively
     * @param dir the directory to return the files from
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
     * @param dir the directory to be deleted
     * @throws IOException thrown if an error occurs while deleting the directory
     */
    private static void delDir(File dir) throws IOException {
        Files.walk(dir.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public List<File> getSources() {
        return files;
    }
}
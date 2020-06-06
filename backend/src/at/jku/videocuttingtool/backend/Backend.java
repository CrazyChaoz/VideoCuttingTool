package at.jku.videocuttingtool.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static java.util.stream.Collectors.joining;

public class Backend {
	private final List<File> files = new ArrayList<>();
	private File workingDir;

	private static final String ffmpeg = "lib/ffmpeg/ffmpeg.exe";

	public void addSources(List<File> sources){
		files.addAll(sources);
	}

	public void loadFromWorkingDir(){
		List<File> files = new ArrayList<>();
		listf(workingDir,files);
		addSources(files);
	}

	public void setWorkingDir(File dir) {
		this.workingDir = dir;
	}

	public void export(Timeline timeline, File exportDir, String name) throws IOException, InterruptedException {
		if (!exportDir.isDirectory())
			return;

		File tempDirV = new File(exportDir+"/outV/");
		tempDirV.mkdir();
		File tempDirA = new File(exportDir+"/outA/");
		tempDirA.mkdir();

		Process merge = mergeMedia(timeline.getVideo(),tempDirV,"v_export.mp4", "mp4");
		if (merge != null) merge.waitFor();
		merge = mergeMedia(timeline.getAudio(),tempDirA,"a_export.mp3", "mp3");
		if (merge != null) merge.waitFor();

		String extractAudio = ffmpeg +" -i "+tempDirV+"/v_export.mp4 "+tempDirA+"/v_audio.mp3";
		String mergeAudio = ffmpeg +" -i "+tempDirA+"/a_export.mp3 -i "+tempDirA+"/v_audio.mp3 -filter_complex amerge -c:a libmp3lame -q:a 4 "+tempDirA+"/final_audio.mp3";
		String mergeAudioAndVideo = ffmpeg +" -i "+tempDirV+"/v_export.mp4 -i "+tempDirA+"/final_audio.mp3 -map 0:v -map 1:a -c copy -y "+exportDir+"/"+name;

		Process extract = Runtime.getRuntime().exec(extractAudio);
		extract.waitFor();
		Process mergeA = Runtime.getRuntime().exec(mergeAudio);
		mergeA.waitFor();
		merge = Runtime.getRuntime().exec(mergeAudioAndVideo);
		merge.waitFor();

		delDir(tempDirV);
		delDir(tempDirA);
	}

	public Process mergeMedia(Set<Clip> media, File dir, String name, String codec) throws IOException, InterruptedException{
		for (Clip c: media) {
			Optional<Process> p = cutClip(c, dir.getAbsolutePath() + "\\part_" + c.getPos() + "." + codec);
			if (p.isPresent()) p.get().waitFor();
		}

		Path files = createFileList(dir);
		if (files == null)
			return null;

		String concat = ffmpeg+" -f concat -safe 0 -i "+files.toAbsolutePath().toString()+" -c copy "+dir+"/"+name;
		return Runtime.getRuntime().exec(concat);
	}

	public Optional<Process> cutClip(Clip clip, String export) throws IOException{
		if (clip.getStart().isEmpty() && clip.getEnd().isEmpty()) {
			Files.copy(clip.getMedia().toPath(), new File(export).toPath(), StandardCopyOption.REPLACE_EXISTING);
			return Optional.empty();
		}

		String cut = ffmpeg;

		if (!clip.getStart().isEmpty())
			cut += " -ss "+clip.getStart();

		cut += " -i \""+clip.getMedia().getAbsolutePath()+"\" -c copy";

		if (!clip.getEnd().isEmpty())
			cut += " -to "+clip.getEnd();

		cut += " "+export;

		return Optional.of(Runtime.getRuntime().exec(cut));
	}

	public static void main(String[] args) {
		try {
			Backend b = new Backend();

			File test = new File("D:\\ShadowPlay\\Desktop\\f1_1.mp4");
			File test2 = new File("D:\\ShadowPlay\\Desktop\\f1_2.mp4");
			File test3 = new File("D:\\ShadowPlay\\Desktop\\f1_3.mp4");

			Timeline tm = new Timeline();
			tm.addVideo(new Clip(test, 1,"",""));
			//tm.addVideo(new Clip(test2, 2,"",""));
			//tm.addVideo(new Clip(test3, 3,"",""));

			File audio = new File("D:\\OneDrive\\JKU\\Multimediasysteme\\audio\\Inception Soundtrack HD - #12 Time (Hans Zimmer).mp3");
			tm.addAudio(new Clip(audio,0,"00:00:25","00:00:35"));
			tm.addAudio(new Clip(audio, 1, "00:00:35", "00:00:45"));

			b.export(tm,new File("D:\\ShadowPlay\\Desktop\\"),"export.mp4");
		} catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}

	private static Path createFileList(File dir) throws IOException{
		if (!dir.isDirectory())
			return null;

		ArrayList<File> outFiles = new ArrayList<>();
		listf(dir,outFiles);
		String filesStrings = outFiles
				.stream()
				.map(f -> f.getAbsolutePath().toString())
				.map(p -> "file '" + p + "'")
				.collect(joining(System.getProperty("line.separator")));
		Path listOfFiles = Files.createTempFile(Paths.get(dir.toURI()), "ffmpeg-list-", ".txt");
		Files.write(listOfFiles, filesStrings.getBytes());

		return listOfFiles;
	}

	private static void listf(File dir, List<File> files) {
		File[] fList = dir.listFiles();
		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					listf(file, files);
				}
			}
		}
	}

	public static void delDir(File dir) throws IOException{
		Files.walk(dir.toPath())
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
	}

	public List<File> getSources(){
		return files;
	}
}
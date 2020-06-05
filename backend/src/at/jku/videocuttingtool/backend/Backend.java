package at.jku.videocuttingtool.backend;

import com.google.common.collect.Lists;
import javafx.scene.media.Media;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * des is a temporäre impl, das i vom frontend auf daten zugreifn kau
 * */
public class Backend {
	private List<Media> files = new ArrayList<>();
	private File workingDir;

	private void addSource(File source) {
		files.add(new Media(source.getAbsolutePath()));
	}

	public void addSources(List<File> sources){
		for (File f : sources) {
			addSource(f);
		}
	}

	public void loadFromWorkingDir(){
		List<File> files = new ArrayList<>();
		listf(workingDir,files);
		addSources(files);
	}

	public void setWorkingDir(File dir) {
		this.workingDir = dir;
	}

	public void export(Timeline timeline, File exportDir, String name) throws IOException {
		FFmpeg ffmpeg = new FFmpeg(workingDir.getAbsolutePath());
		FFmpegBuilder builder = new FFmpegBuilder();
		for (Clip c : timeline.getVideo()){
			builder.addInput(c.getMedia().getSource());
		}
		builder.overrideOutputFiles(true)
				.addOutput(exportDir+"\"+n"+name)
				.done();

		FFmpegExecutor exe = new FFmpegExecutor(ffmpeg);
		exe.createJob(builder).run();
	}

	public static void holyShitThisActuallyWorks() throws IOException{
		FFmpeg ffmpeg = new FFmpeg("lib/ffmpeg/ffmpeg.exe");
		FFprobe ffprobe = new FFprobe("lib/ffmpeg/ffprobe.exe");
		FFmpegBuilder builder = new FFmpegBuilder();

		String path = "D:\\ShadowPlay\\Desktop";
		String filesStrings = Lists.newArrayList(new File(path+"\\f1_1.mp4"), new File(path+"\\f1_2.mp4"), new File(path+"\\f1_3.mp4"))
				.stream()
				.map(f -> f.getAbsolutePath().toString())
				.map(p -> "file '" + p + "'")
				.collect(joining(System.getProperty("line.separator")));
		Path listOfFiles = Files.createTempFile(Paths.get(path), "ffmpeg-list-", ".txt");
		Files.write(listOfFiles, filesStrings.getBytes());

		builder.setInput(listOfFiles.toAbsolutePath().toString())
				.setFormat("concat").addExtraArgs("-safe","0")
				.addOutput(path+"\\out.mp4")
				.setAudioCodec("copy")
				.setVideoCodec("copy")
				.done();

		FFmpegExecutor exe = new FFmpegExecutor(ffmpeg,ffprobe);
		exe.createJob(builder).run();
	}

	public static void main(String[] args) {
		try {
			holyShitThisActuallyWorks();
		}catch (IOException e){
			e.printStackTrace();
		}
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

	public List<Media> getSources(){
		return files;
	}

	public boolean isVideo(Media s){
		return true;
	}

	public boolean isAudio(Media s){
		return false;
	}
}
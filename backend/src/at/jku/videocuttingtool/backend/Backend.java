package at.jku.videocuttingtool.backend;

import javafx.scene.media.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

	public void export(Timeline timeline, File exportDir, String name){

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
package at.jku.videocuttingtool.backend;

import javafx.scene.media.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * des is a temporäre impl, das i vom frontend auf daten zugreifn kau
 * */
public class Backend {
	private List<Media> files=new ArrayList<>();


	private void addSource(File sourcefile){
		files.add(new Media(sourcefile.getPath()));
	}

	public void addSources(List<File> sourcefiles){
		for (File sourcefile : sourcefiles) {
			addSource(sourcefile);
		}
	}

	public void setWorkingDir(File file) {
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

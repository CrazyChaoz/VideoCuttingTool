package at.jku.videocuttingtool.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * des is a temporäre impl, das i vom frontend auf daten zugreifn kau
 * */
public class Backend {
	private List<Source> files=new ArrayList<>();


	private void addSource(File sourcefile){
		files.add(new Source(sourcefile));
	}

	public void addSources(List<File> sourcefiles){
		for (File sourcefile : sourcefiles) {
			addSource(sourcefile);
		}
	}

	public void setWorkingDir(File file) {
	}

	public List<Source> getSources(){
		return files;
	}


	public boolean isVideo(Source s){
		return true;
	}

	public boolean isAudio(Source s){
		return false;
	}


}

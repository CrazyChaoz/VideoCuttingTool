package at.jku.videocuttingtool.backend;

import java.io.File;
import java.util.List;

public class Backend {

	private void addSource(File sourcefile){

	}

	public void addSources(List<File> sourcefiles){
		for (File sourcefile : sourcefiles) {
			addSource(sourcefile);
		}
	}

	public void setWorkingDir(File file) {
	}
}

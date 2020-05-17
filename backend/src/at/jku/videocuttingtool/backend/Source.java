package at.jku.videocuttingtool.backend;

import java.io.File;

public class Source {
	private File file;

	public Source(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}
}

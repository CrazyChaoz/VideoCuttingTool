package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Source;

public abstract class CommonController {

	protected Source source;

	public void setSource(Source s){
		source = s;
	}

	public abstract void initialize();

}

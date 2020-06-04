package at.jku.videocuttingtool.frontend;

import javafx.scene.media.Media;

public abstract class CommonController {

	protected Media source;

	public void setSource(Media s){
		source = s;
	}

	public abstract void initialize();

}

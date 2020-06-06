package at.jku.videocuttingtool.frontend.video;


import at.jku.videocuttingtool.frontend.CommonController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class VisualsController extends CommonController {

	public MediaView mediaView;

	public void initialize() {
	}

	@Override
	public void setSource(File s) {
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(s.toURI().toString()));
		mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setMute(true);
		mediaPlayer.setOnError(() -> System.out.println(mediaPlayer.errorProperty().get().getMessage()));
	}
}

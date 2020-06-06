package at.jku.videocuttingtool.frontend.video;


import at.jku.videocuttingtool.frontend.CommonController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class VisualsController extends CommonController {

	public MediaView mediaView;
	public BorderPane papaContainer;

	public void initialize() {
	}

	@Override
	public void setSource(File s) {
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(s.toURI().toString()));
		mediaView.setMediaPlayer(mediaPlayer);

		MediaBar bar = new MediaBar(mediaPlayer); // Passing the player to MediaBar
		papaContainer.setBottom(bar); // Setting the MediaBar at bottom
		papaContainer.setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar
		mediaPlayer.play();
	}
}

class MediaBar extends HBox { // MediaBar extends Horizontal Box

	// introducing Sliders
	Slider time = new Slider(); // Slider for time
	Slider vol = new Slider(); // Slider for volume
	Button PlayButton = new Button("||"); // For pausing the player
	Label volume = new Label("Volume: ");
	MediaPlayer player;

	public MediaBar(MediaPlayer play) { // Default constructor taking
		// the MediaPlayer object
		player = play;

		setAlignment(Pos.CENTER);
		setPadding(new Insets(5, 10, 5, 10));
		vol.setPrefWidth(70);
		vol.setMinWidth(30);
		vol.setValue(100);
		HBox.setHgrow(time, Priority.ALWAYS);
		PlayButton.setPrefWidth(30);

		getChildren().add(PlayButton);
		getChildren().add(time);
		getChildren().add(volume);
		getChildren().add(vol);

		PlayButton.setOnAction(e -> {
			MediaPlayer.Status status = player.getStatus();
			if (status == MediaPlayer.Status.PLAYING) {
				if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
					player.seek(player.getStartTime());
					player.play();
				} else {
					player.pause();
					PlayButton.setText(">");
				}
			}
			if (status == MediaPlayer.Status.HALTED || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.PAUSED) {
				player.play();
				PlayButton.setText("||");
			}
		});

		player.currentTimeProperty().addListener(ov -> updatesValues());

		time.valueProperty().addListener(ov -> {
			if (time.isPressed()) {
				player.seek(player.getMedia().getDuration().multiply(time.getValue() / 100));
			}
		});

		vol.valueProperty().addListener(ov -> {
			if (vol.isPressed()) {
				player.setVolume(vol.getValue() / 100);
			}
		});
	}

	// Outside the constructor
	protected void updatesValues() {
		Platform.runLater(() -> {
			// Updating to the new time value
			// This will move the slider while running your video
			time.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);
		});
	}
}
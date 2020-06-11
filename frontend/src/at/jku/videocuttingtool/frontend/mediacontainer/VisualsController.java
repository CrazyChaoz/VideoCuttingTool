package at.jku.videocuttingtool.frontend.mediacontainer;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class VisualsController{

	@FXML
	public MediaView mediaView;

	@FXML
	public BorderPane papaContainer;

	@FXML
	private Button beginButton;

	@FXML
	private Button backButton;

	@FXML
	private Button pauseButton;

	@FXML
	private Button forwardButton;

	@FXML
	private Button endButton;

	@FXML
	private Button setBeginButton;

	@FXML
	private Button setEndButton;

	@FXML
	private Slider contentPositionSlider;

	@FXML
	private Slider startPositionSlider;

	@FXML
	private Slider endPositionSlider;


	public void setSource(File s) {
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(s.toURI().toString()));
		mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.play();
	}

	@FXML
	private void repositionContentPositionSlider() {
		//TODO: set content to contentPositionSlider.getValue()*contentLength
	}

	@FXML
	private void skipToBeginning() {
		//TODO: set content to begin of selected, if already at begin of selected set startPositionSlider to absolute begin
	}

	@FXML
	private void skipToEnd() {
		//TODO: set content to end of selected, if already at end of selected set endPositionSlider to absolute end
	}

	@FXML
	private void skipToPreviousFrameUnit() {
		//TODO: set content to curr-FrameUnit, if already at begin of selected set startPositionSlider to curr-FrameUnit
	}

	@FXML
	private void skipToNextFrameUnit() {
		//TODO: set content to curr+FrameUnit, if already at end of selected set endPositionSlider to curr+FrameUnit begin
	}

	@FXML
	private void pause() {
		//TODO: pause/play content
		System.out.println("PAUSE");
	}

	@FXML
	private void setBeginPosition() {
		//TODO: set beginPositionSlider to curr
	}

	@FXML
	private void setEndPosition() {
		//TODO: set endPositionSlider to curr
	}
}

//class MediaBar extends HBox { // MediaBar extends Horizontal Box
//
//	// introducing Sliders
//	Slider time = new Slider(); // Slider for time
//	Slider vol = new Slider(); // Slider for volume
//	Button PlayButton = new Button("||"); // For pausing the player
//	Label volume = new Label("Volume: ");
//	MediaPlayer player;
//
//	public MediaBar(MediaPlayer play) { // Default constructor taking
//		// the MediaPlayer object
//		player = play;
//
//		setAlignment(Pos.CENTER);
//		setPadding(new Insets(5, 10, 5, 10));
//		vol.setPrefWidth(70);
//		vol.setMinWidth(30);
//		vol.setValue(100);
//		HBox.setHgrow(time, Priority.ALWAYS);
//		PlayButton.setPrefWidth(30);
//
//		getChildren().add(PlayButton);
//		getChildren().add(time);
//		getChildren().add(volume);
//		getChildren().add(vol);
//
//		PlayButton.setOnAction(e -> {
//			MediaPlayer.Status status = player.getStatus();
//			if (status == MediaPlayer.Status.PLAYING) {
//				if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
//					player.seek(player.getStartTime());
//					player.play();
//				} else {
//					player.pause();
//					PlayButton.setText(">");
//				}
//			}
//			if (status == MediaPlayer.Status.HALTED || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.PAUSED) {
//				player.play();
//				PlayButton.setText("||");
//			}
//		});
//
//		player.currentTimeProperty().addListener(ov -> updatesValues());
//
//		time.valueProperty().addListener(ov -> {
//			if (time.isPressed()) {
//				player.seek(player.getMedia().getDuration().multiply(time.getValue() / 100));
//			}
//		});
//
//		vol.valueProperty().addListener(ov -> {
//			if (vol.isPressed()) {
//				player.setVolume(vol.getValue() / 100);
//			}
//		});
//	}
//
//	// Outside the constructor
//	protected void updatesValues() {
//		Platform.runLater(() -> {
//			// Updating to the new time value
//			// This will move the slider while running your video
//			time.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);
//		});
//	}
//}
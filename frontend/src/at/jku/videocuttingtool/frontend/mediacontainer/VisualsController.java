package at.jku.videocuttingtool.frontend.mediacontainer;


import at.jku.videocuttingtool.backend.Clip;
import at.jku.videocuttingtool.frontend.GUI;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class VisualsController {

	@FXML
	public BorderPane papaContainer;
	@FXML
	private MediaView mediaView;
	@FXML
	private AreaChart<String, Number> audioView;
	@FXML
	private AreaChart<Integer, Number> totalView;
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

	@FXML
	private TextField positionInTimelineField;

	private Clip clip;


	public void setSource(Clip clip, boolean isVideo) {
		this.clip = clip;


		MediaPlayer mediaPlayer = new MediaPlayer(new Media(clip.getMedia().toURI().toString()));
		mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.currentTimeProperty().addListener(observable -> {
			contentPositionSlider.setValue(mediaView.getMediaPlayer().getCurrentTime().toMillis() / mediaView.getMediaPlayer().getTotalDuration().toMillis() * 100);
			if (contentPositionSlider.getValue() > endPositionSlider.getValue()) {
				mediaView.getMediaPlayer().pause();
			}
		});

		mediaPlayer.setAutoPlay(false);

		if (isVideo) {
			if (GUI.SPLIT_VIDEO)
				mediaPlayer.setMute(true);
			audioView.setVisible(false);
			audioView.setManaged(false);
			totalView.setVisible(false);
			totalView.setManaged(false);
		} else {
			mediaView.setVisible(false);
			mediaView.setManaged(false);
			mediaPlayer.setAudioSpectrumListener(new SpectrumListener(mediaPlayer, audioView, totalView));
		}

		positionInTimelineField.setText(clip.getPos() + "");
	}


	@FXML
	private void repositionContentPositionSlider() {
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getMedia().getDuration().multiply(contentPositionSlider.getValue() / 100));
	}

	@FXML
	private void skipToBeginning() {
		//set content to begin of selected, if already at begin of selected set startPositionSlider to absolute begin

		if (contentPositionSlider.getValue() == startPositionSlider.getValue()) {
			mediaView.getMediaPlayer().seek(Duration.millis(0));
			contentPositionSlider.setValue(0);
		} else {
			mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getMedia().getDuration().multiply(startPositionSlider.getValue() / 100));
			contentPositionSlider.setValue(startPositionSlider.getValue());
		}
	}

	@FXML
	private void skipToEnd() {
		//set content to end of selected, if already at end of selected set endPositionSlider to absolute end
		if (contentPositionSlider.getValue() == endPositionSlider.getValue()) {
			mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getMedia().getDuration());
			contentPositionSlider.setValue(100);
		} else {
			mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getMedia().getDuration().multiply(endPositionSlider.getValue() / 100));
			contentPositionSlider.setValue(endPositionSlider.getValue());
		}
	}

	@FXML
	private void skipToPreviousFrameUnit() {
		//set content to curr-FrameUnit, if already at begin of selected set startPositionSlider to curr-FrameUnit
		mediaView
				.getMediaPlayer()
				.seek(mediaView
						.getMediaPlayer()
						.getCurrentTime()
						.subtract(mediaView
								.getMediaPlayer()
								.getCurrentTime()
								.toMillis() > GUI.MINIMAL_SKIPPABLE_TIME ?
								Duration.millis(GUI.MINIMAL_SKIPPABLE_TIME) :
								mediaView.getMediaPlayer().getCurrentTime()));
	}

	@FXML
	private void skipToNextFrameUnit() {
		//set content to curr+FrameUnit, if already at end of selected set endPositionSlider to curr+FrameUnit begin
		mediaView
				.getMediaPlayer()
				.seek(mediaView
						.getMediaPlayer()
						.getCurrentTime()
						.add(mediaView
								.getMediaPlayer()
								.getCurrentTime()
								.toMillis() + GUI.MINIMAL_SKIPPABLE_TIME < mediaView
								.getMediaPlayer().getTotalDuration().toMillis() ?
								Duration.millis(GUI.MINIMAL_SKIPPABLE_TIME) :
								mediaView.getMediaPlayer().getTotalDuration().subtract(mediaView.getMediaPlayer().getCurrentTime())));
	}

	@FXML
	private void pause() {
		MediaPlayer.Status status = mediaView.getMediaPlayer().getStatus();
		if (status == MediaPlayer.Status.PLAYING) {
			if (mediaView.getMediaPlayer().getCurrentTime().greaterThanOrEqualTo(mediaView.getMediaPlayer().getTotalDuration())) {
				mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStartTime());
				mediaView.getMediaPlayer().play();
			} else {
				mediaView.getMediaPlayer().pause();
			}
		} else {
			mediaView.getMediaPlayer().play();
		}
	}

	@FXML
	private void setBeginPosition() {
		//set beginPositionSlider to curr
		startPositionSlider.setValue(contentPositionSlider.getValue());
		clip.setStart(getBauerFormatting());
	}

	@FXML
	private void setEndPosition() {
		//set endPositionSlider to curr
		endPositionSlider.setValue(contentPositionSlider.getValue());
		clip.setEnd(getBauerFormatting());
	}

	@FXML
	private void setPositionInTimeline() {
		try {
			clip.setPos(Integer.parseInt(positionInTimelineField.getText()));
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		}
	}

	private String getBauerFormatting() {
		return (int) mediaView.getMediaPlayer().getCurrentTime().toHours() +
				":" +
				(int) mediaView.getMediaPlayer().getCurrentTime().toMinutes() % 60 +
				":" +
				(int) mediaView.getMediaPlayer().getCurrentTime().toSeconds() % 60 +
				"." +
				(int) mediaView.getMediaPlayer().getCurrentTime().toMillis() % 1000;
	}

	public void onClose() {
		mediaView.getMediaPlayer().pause();
	}
}
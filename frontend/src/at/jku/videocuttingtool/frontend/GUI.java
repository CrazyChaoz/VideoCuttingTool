package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import at.jku.videocuttingtool.frontend.mediacontainer.VisualsController;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class GUI extends Application {
	public static final int MINIMAL_SKIPPABLE_TIME = 200; //milliseconds

	private Backend backend = new Backend();
	private IntegerProperty filesToConvertProperty;
	private Stage convertingFilesProgressIndicator;

	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(showMenu(primaryStage));
		primaryStage.setTitle("VideoCuttingTool");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> {
			if (convertingFilesProgressIndicator != null) convertingFilesProgressIndicator.close();
		});
	}

	private Parent showMenu(Stage stage) {
		Label label = new Label("Was möchten Sie tun?");
		Button src = new Button("Sourcefiles adden");
		Button dir = new Button("Working Dir setzen");
		Button displayAll = new Button("Alle Elemente Displayen");

		src.setOnMouseClicked((event) -> {
			FileChooser fileChooser = new FileChooser();
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			if (files != null) {
				if (convertingFilesProgressIndicator == null)
					convertingProgressBar(files.size());
				else
					updateProgressBar(files.size());
				backend.addSources(files);
			}
		});

		dir.setOnMouseClicked((event) -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File file = directoryChooser.showDialog(stage);
			if (file != null)
				backend.setWorkingDir(file);
		});

		displayAll.setOnMouseClicked((event) -> {
			backend.getSources().forEach((source -> {
				try {
					System.out.println(getContentType(source));

					VisualsController controller = createVisualContainer(source);

//					if (isVideo(source)) {
//					} else if (isAudio(source)) {
//						//TODO: Implement Audio handling
//					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}));
		});

		return new VBox(label, new HBox(src, dir), displayAll);
	}

	private String getContentType(File source) throws IOException {
		return Files.probeContentType(source.toPath());
	}


	private boolean isAudio(File source) throws IOException {
		return getContentType(source).split("/")[0].equals("audio");
	}

	private boolean isVideo(File source) throws IOException {
		return getContentType(source).split("/")[0].equals("video");
	}

	private VisualsController createVisualContainer(File source) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("mediacontainer/Visuals.fxml"));

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage popup = new Stage();
		popup.setTitle(source.getName());
		popup.setScene(new Scene(loader.getRoot()));
		popup.show();
		VisualsController controller = loader.getController();
		controller.setSource(source);
		popup.setOnCloseRequest(uwu -> controller.onClose());
		return controller;
	}

	private void convertingProgressBar(int files) {
		convertingFilesProgressIndicator = new Stage();
		filesToConvertProperty = new SimpleIntegerProperty(files);
		convertingFilesProgressIndicator.initStyle(StageStyle.UTILITY);
		convertingFilesProgressIndicator.setTitle("Converting Files");
		convertingFilesProgressIndicator.setScene(new Scene(progressBarHandling(files)));
		convertingFilesProgressIndicator.setOnCloseRequest(unused -> convertingFilesProgressIndicator = null);
		convertingFilesProgressIndicator.sizeToScene();
		convertingFilesProgressIndicator.show();
	}

	private void updateProgressBar(int files) {
		int files2 = files + filesToConvertProperty.get();
		filesToConvertProperty.set(files2);
		progressBarHandling(files2);
		convertingFilesProgressIndicator.getScene().setRoot(progressBarHandling(files2));
	}

	private Parent progressBarHandling(int files) {
		Label progress = new Label(0 + "/" + files);
		ProgressBar pb = new ProgressBar(0);

		filesToConvertProperty.addListener((ov, old_val, new_val) -> {
			pb.setProgress(new_val.doubleValue() / files);
			progress.setText(new_val + "/" + files);
		});


		final HBox box = new HBox();
		box.setStyle("-fx-font-size: 15px;");
		box.setSpacing(5);
		box.setPadding(new Insets(10, 10, 10, 10));
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(pb, progress);
		return box;
	}

	public void fileConverted() {
		filesToConvertProperty.subtract(1);
	}
}

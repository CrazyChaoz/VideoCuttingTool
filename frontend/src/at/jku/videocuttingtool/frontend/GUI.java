package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GUI extends Application {

	private Backend backend = new Backend();
	private IntegerProperty convertingFiles;
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
				if (convertingFilesProgressIndicator==null)
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
				if (backend.isVideo(source)) {
					createVisualContainer(source, getClass().getResource("video/Visuals.fxml"));
				}
				if (backend.isAudio(source)) {
					createVisualContainer(source, getClass().getResource("audio/Visuals.fxml"));
				}
			}));
		});

		return new VBox(label, new HBox(src, dir), displayAll);
	}

	private void createVisualContainer(Media source, URL fxmlUrl) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(fxmlUrl);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage popup = new Stage();
		popup.setTitle(source.getSource());
		((CommonController) loader.getController()).setSource(source);
		popup.setScene(new Scene(loader.getRoot()));
		popup.show();
	}

	private void convertingProgressBar(int files) {
		convertingFilesProgressIndicator = new Stage();
		convertingFiles=new SimpleIntegerProperty(files);
		convertingFilesProgressIndicator.setScene(new Scene(progressBarHandling(files)));
		convertingFilesProgressIndicator.setTitle("Converting Files");
		convertingFilesProgressIndicator.show();
	}

	private void updateProgressBar(int files) {
		int files2=files+convertingFiles.get();
		convertingFiles.set(files2);
		progressBarHandling(files2);
		convertingFilesProgressIndicator.getScene().setRoot(progressBarHandling(files2));
	}

	private Parent progressBarHandling(int files) {
		Label progress=new Label("");
		ProgressBar pb = new ProgressBar(0);

		convertingFiles.addListener((ov, old_val, new_val) -> {
			pb.setProgress(new_val.doubleValue() / files);
			progress.setText(new_val+"/"+files);
		});

		final HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(new Label("Converting Files"), new VBox(pb,progress));
		return hb;
	}
}

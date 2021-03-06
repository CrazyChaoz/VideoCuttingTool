package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.*;
import at.jku.videocuttingtool.frontend.mediacontainer.VisualsController;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
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
	public static final boolean SPLIT_VIDEO=false;

	private Backend backend = new Backend();
	private IntegerProperty filesToConvertProperty;
	private Stage convertingFilesProgressIndicator;
	private Timeline timeline= new Timeline();
	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws IOException {

		this.primaryStage = primaryStage;

		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("NewAndUpdatedMainGui.fxml")));
		primaryStage.setTitle("VideoCuttingTool");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> {
			if (convertingFilesProgressIndicator != null) convertingFilesProgressIndicator.close();
		});
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

	private VisualsController createVisualContainer(Clip source, boolean isVideo) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("mediacontainer/Visuals.fxml"));

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage popup = new Stage();
		popup.setTitle(source.getMedia().getName());
		popup.setScene(new Scene(loader.getRoot()));
		popup.show();
		VisualsController controller = loader.getController();
		controller.setSource(source, isVideo);
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

	@FXML
	private void addSourceFiles() {
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
		if (files != null) {
//			if (convertingFilesProgressIndicator == null)
//				convertingProgressBar(files.size());
//			else
//				updateProgressBar(files.size());
			backend.addSources(files);
			files.forEach(source->{
				try {
					Clip clip = new Clip(source,0);
					if (isVideo(source)) {
						timeline.addVideo(clip);
					} else if (isAudio(source)) {
						timeline.addAudio(clip);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}


	@FXML
	private void saveProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(primaryStage);
		if (file != null){
			try {
				backend.saveProject(timeline,file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void loadProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(primaryStage);
		if (file != null){
			try {
				timeline=backend.loadProject(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@FXML
	private void setWorkingDir() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(primaryStage);
		if (file != null)
			backend.setWorkingDir(file);
	}

	@FXML
	private void displayElements() {
		timeline.getVideo().forEach(source->{createVisualContainer(source, true);if(SPLIT_VIDEO)createVisualContainer(source, false);});
		timeline.getAudio().forEach(source->createVisualContainer(source, false));
	}

	@FXML
	private void onExportButtonPressed(){
		try {
			File file=new DirectoryChooser().showDialog(primaryStage);
			backend.export(new Export(timeline,new File(file.getAbsolutePath()+"/out.mp4"),"mp4","mp3"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (EditMediaException e) {
			e.printStackTrace();
		}
	}
}

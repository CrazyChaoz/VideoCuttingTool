package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
			if (files != null)
				backend.addSources(files);
		});

		dir.setOnMouseClicked((event) -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File file = directoryChooser.showDialog(stage);
			if (file != null)
				backend.setWorkingDir(file);
		});

		displayAll.setOnMouseClicked((event)->{
			backend.getSources().forEach((source -> {
				if(backend.isVideo(source)){
					createVisualContainer(source,getClass().getResource("video/Visuals.fxml"));
				}
				if (backend.isAudio(source)){
					createVisualContainer(source,getClass().getResource("audio/Visuals.fxml"));
				}
			}));
		});

		return new VBox(label, new HBox(src, dir),displayAll);
	}

	private void createVisualContainer(Media source, URL fxmlUrl){
		FXMLLoader loader=new FXMLLoader();
		loader.setLocation(fxmlUrl);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage popup=new Stage();
		popup.setTitle(source.getSource());
		((CommonController)loader.getController()).setSource(source);
		popup.setScene(new Scene(loader.getRoot()));
		popup.show();
	}

	private void convertingProgressBar(int files){
		Stage popup=new Stage();
		Group root = new Group();
		Scene scene = new Scene(root);
		popup.setScene(scene);
		popup.setTitle("Progress Controls");



		final ProgressBar pb = new ProgressBar(0);

//		slider.valueProperty().addListener((ov, old_val, new_val) -> {
//			pb.setProgress(new_val.doubleValue()/files);
//		});

		final HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(pb);
		scene.setRoot(hb);

		popup.show();
	}

}

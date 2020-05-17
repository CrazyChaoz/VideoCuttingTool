package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import at.jku.videocuttingtool.backend.Source;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GUI extends Application {

	Backend backend = new Backend();

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

	private void createVisualContainer(Source source,URL fxmlUrl){
		FXMLLoader loader=new FXMLLoader();
		loader.setLocation(fxmlUrl);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage popup=new Stage();
		popup.setTitle(source.getFile().getName());
		((CommonController)loader.getController()).setSource(source);
		popup.setScene(new Scene(loader.getRoot()));
		popup.show();
	}
}

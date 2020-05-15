package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import javafx.application.Application;
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

		return new VBox(label, new HBox(src, dir));
	}
}

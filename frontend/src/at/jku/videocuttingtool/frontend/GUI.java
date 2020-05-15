package at.jku.videocuttingtool.frontend;

import at.jku.videocuttingtool.backend.Backend;
import javafx.application.Application;
import javafx.geometry.Pos;
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

	Backend backend=new Backend();

	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primaryStage){
		Scene scene = new Scene(showMenu(primaryStage));
		primaryStage.setTitle("VideoCuttingTool");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Parent showMenu(Stage stage){
		Label label=new Label("Was möchten Sie tun?");
		Button src=new Button("Sourcefiles adden");
		Button dir=new Button("Working Dir setzen");

		src.setOnMouseClicked((event)->{
			stage.getScene().setRoot(showSourceSelection(stage));
		});

		dir.setOnMouseClicked((event)->{
			stage.getScene().setRoot(showWorkingDirSelection(stage));
		});

		return new VBox(label,new HBox(src,dir));
	}


	private Parent showSourceSelection(Stage stage){
		Label label = new Label("Bitte Sourcefiles auswählen");
		FileChooser fileChooser = new FileChooser();

		Button selectFiles = new Button("Select Files");
		selectFiles.setOnAction(event -> {
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			backend.addSources(files);
			stage.getScene().setRoot(showMenu(stage));
		});

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(label, selectFiles);

		return root;
	}

	private Parent showWorkingDirSelection(Stage stage){
		Label label = new Label("Bitte Working Directory auswählen");
		DirectoryChooser directoryChooser = new DirectoryChooser();

		Button selectFiles = new Button("Select Files");
		selectFiles.setOnAction(event -> {
			File file = directoryChooser.showDialog(stage);
			backend.setWorkingDir(file);
			stage.getScene().setRoot(showMenu(stage));
		});

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(label, selectFiles);

		return root;
	}
}

package at.jku.videocuttingtool.frontend.audio;


import at.jku.videocuttingtool.frontend.CommonController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VisualsController extends CommonController {

	@FXML
	public Label sourceLabel;

	public void initialize(){
		sourceLabel.setOnMouseClicked((event)->{
			System.out.println("uwu");
		});
	}

}

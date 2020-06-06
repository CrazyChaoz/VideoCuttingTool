package at.jku.videocuttingtool.frontend.audio;


import at.jku.videocuttingtool.frontend.CommonController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.File;

public class VisualsController extends CommonController {

	@FXML
	public Label sourceLabel;

	@Override
	public void setSource(File s) {}

	public void initialize(){}
}

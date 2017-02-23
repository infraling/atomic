package org.corpus_tools.search.parts;

import org.eclipse.e4.core.di.annotations.Creatable;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Creatable
public class ANNISSearchController {

	@FXML
	private Label myLabelInView;
	
	public void focus() {
		myLabelInView.requestFocus();
	}
	
	public void showInfo(String msg) {
		myLabelInView.setText(msg);
	}
	
}

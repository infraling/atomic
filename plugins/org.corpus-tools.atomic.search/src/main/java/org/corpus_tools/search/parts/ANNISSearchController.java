package org.corpus_tools.search.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.osgi.service.log.LogService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Creatable
public class ANNISSearchController {

	@FXML
	private Button reIndexButton;
	
	@Inject
	private UISynchronize uiSync;
	
	@Inject 
	private LogService log;
	
	public void reindex() {
		log.log(LogService.LOG_INFO, "Attempting re-index");
		uiSync.asyncExec(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		} );
	}
	
	
}

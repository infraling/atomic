package org.corpus_tools.search.parts;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.ui.di.FXMLBuilder.Data;
import org.eclipse.fx.ui.di.InjectingFXMLLoader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ANNISSearch {

	private ANNISSearchController controller;

	private FXCanvas fxCanvas;
	
	
	private void createFXMLScene(IEclipseContext context) {
		InjectingFXMLLoader<Parent> loader = InjectingFXMLLoader.create(context, getClass(), "ANNISSearch.fxml");
		Data<Parent, ANNISSearchController> fxmlData;
		try {
			fxmlData = loader.loadWithController();
			this.controller = fxmlData.getController();
			final Scene scene = new Scene(fxmlData.getNode());
			fxCanvas.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createFXMLScene(context));
	}
	


}

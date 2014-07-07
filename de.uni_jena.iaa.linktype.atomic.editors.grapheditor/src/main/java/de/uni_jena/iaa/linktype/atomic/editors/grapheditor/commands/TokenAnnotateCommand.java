/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.exceptions.GraphInsertException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class TokenAnnotateCommand extends Command {
	
	private String annotationInput;
	private SToken model;
	private ArrayList<SAnnotation> oldAnnotations = new ArrayList<SAnnotation>();
	
	@Override 
	public void execute() {
		oldAnnotations.addAll(model.getSAnnotations());
		for (Iterator<SAnnotation> iterator = oldAnnotations.iterator(); iterator.hasNext();) {
			SAnnotation a = iterator.next();
			model.removeLabel(a.getSName());
		}
		
		// Parse String input, 
		// separate into single key-value pairs (split at line break),
		// delete all SAnnotations,
		// create new SAnnotation for each key-value pair
		String lineSeparator = System.getProperty("line.separator");
		String[] keyValuePairs = annotationInput.split(lineSeparator);
		
		for (int i = 0; i < keyValuePairs.length; i++) {
			String[] singleKeyValuePair = keyValuePairs[i].split(":"); // TEST
			SAnnotation newSAnnotation = SaltFactory.eINSTANCE.createSAnnotation();
			newSAnnotation.setSName(singleKeyValuePair[0]);
			newSAnnotation.setSValue(singleKeyValuePair[1]);
			for (SAnnotation anno : oldAnnotations) {
				if (anno.getSName().equals(newSAnnotation.getSName())) {
					System.err.println("EXISTS!");
				}
			}
			try {
				model.addSAnnotation(newSAnnotation);
			} catch (GraphInsertException e) {
				e.printStackTrace();
				MessageDialog.open(MessageDialog.ERROR, Display.getCurrent().getActiveShell(), 
						"Duplicate annotation!", 
						"The annotation " + 
						singleKeyValuePair[0] + ":" + singleKeyValuePair[1] + 
						" cannot be added.\n" +
						"An annotation with this key already exists.", 
						SWT.NONE);
			}
		}
			
	}
			 
	//@Override 
	//public void undo() {
	//	if (model.getSAnnotations() == null)
	//		//model.createSAnnotation(null, "ATOMIC__PRIMARY_ANNOTATION", oldAnnotation);
	//	model.getSAnnotation("ATOMIC__PRIMARY_ANNOTATION").setSValue(oldAnnotation);
	//}
			   
	public void setNewAnnotation(String newAnnotation) {
		this.annotationInput = newAnnotation;
	}
			   
	public void setModel(SToken model) {
		this.model = model;
	}

}

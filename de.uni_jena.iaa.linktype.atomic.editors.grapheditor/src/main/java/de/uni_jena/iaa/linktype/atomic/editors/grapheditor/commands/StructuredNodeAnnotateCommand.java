/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class StructuredNodeAnnotateCommand extends Command {
	
	private String annotationInput;
	private SStructuredNode model;
	private TreeMap<String, String> annotations = new TreeMap<String, String>();
	
	@Override 
	public void execute() {
		for (Iterator<SAnnotation> iterator = model.getSAnnotations().iterator(); iterator.hasNext();) {
			SAnnotation anno = (SAnnotation) iterator.next();
			String name = anno.getName();
			model.removeLabel(name);
			Assert.isTrue(!(model.getSAnnotations().contains(name))); // FIXME: Refactor to unit test method
		}
		
		// TODO Refactor to consider optional namespace
		String lineSeparator = System.getProperty("line.separator");
		String[] keyValuePairs = annotationInput.split(lineSeparator);
		for (int i = 0; i < keyValuePairs.length; i++) {
			String[] keyValuePair = keyValuePairs[i].split(":");
			String annoKey = keyValuePair[0];
			String annoValue = keyValuePair[1];
			String previousValue = annotations.put(annoKey, annoValue);
			if (previousValue != null) {
				boolean shouldAnnoChange = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Duplicate annotation found!", "Duplicate annotation!\n"
						+ "Should the previous value of " + annoKey + " (" + previousValue + ") be overwritten with "
						+ "the value " + annoValue + "?");
				if (shouldAnnoChange) {
					// Do nothing, key-value pair is already in TreeMap
				}
				else {
					annotations.put(annoKey, previousValue);
				}
			}
		}
		for (Entry<String, String> anno : annotations.entrySet()) {
			model.createSAnnotation(null, anno.getKey(), anno.getValue());
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
			   
	public void setModel(SStructuredNode model) {
		this.model = model;
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.Arrays;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationAnnotateCommand extends Command {
	
	private String annotationInput;
	private SAnnotation model;
	private LabelableElement modelParent;
	private String[] annotationKeyValue;
	
	public void setModel(SAnnotation model) {
		this.model = model;
	}

	public void setAnnotationInput(String input) {
		this.annotationInput = input;
	}
	
	public String getAnnotationInput() {
		return this.annotationInput;
	}
	
	@Override 
	public void execute() {
		// FIXME: Take into account optional namespace!
		/* FIXME: Write Unit tests for different input possibilities:
		 * a:
		 * :a
		 * a::a::a
		 * a::a::a:a
		 * a::a
		 * a
		 * :
		 * ::
		 * :::
		 * etc.
		 */
		// Parse annotation String
		String key = null;
		String value = null;
		String namespace = null;
		setAnnotationKeyValue(constructAnnotationKeyValue(getAnnotationInput()));

		key = getAnnotationKeyValue()[0];
		value = getAnnotationKeyValue()[1];
		if (getAnnotationKeyValue()[2] != null) {
			namespace = getAnnotationKeyValue()[2];
		}
		boolean isAnnotationArrayValid = checkAnnotationArray(getAnnotationKeyValue());
		if (!isAnnotationArrayValid) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Annotation error", "The annotation format is not correct.\nPlease use [namespace]::[key]:[value], where neither key nor value may be empty, and namespace is optional.");
			return;
		}
		
		// Determine type of model parent & get its List of annotations
		EList<SAnnotation> existingAnnotations = getExistingAnnotationsFromModelParent(modelParent);
		if (existingAnnotations != null) {
			boolean keyExistsInPreexistingAnnotations = false;
			for (SAnnotation preExistingAnnotation : existingAnnotations) {
				if (preExistingAnnotation.getSName().equalsIgnoreCase(key) && preExistingAnnotation != model) {
					if (preExistingAnnotation.getValueString().equalsIgnoreCase(value)) { // Exact duplicate annotation exists
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Duplicate annotation!", "This exact annotation exists already for this annotatable element.\nThe process will be aborted.");
						return;
					}
					else {
						MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), 
								"Duplicate annotation key!", 
								null, 
								"An annotation with the key " + 
									key + 
									" already exists!\n" +
									"Its current value is " +
									preExistingAnnotation.getValueString() + ".\n" +
									"Do you want to overwrite the value of the existing annotation,\n" +
									"and delete the annotation you are currently editing?", 
								MessageDialog.QUESTION, 
								new String[] {"Yes", "No"}, 
								0);
								int result = dialog.open();
								switch (result) {
								case 0: // "Yes"
									preExistingAnnotation.setSValue(value);
									LabelableElement parent = model.getLabelableElement();
									parent.removeLabel(model.getSName());
									parent.eNotify(new NotificationImpl(Notification.REMOVE, model, null));
									break;
								case 1: // "No"
									// Do nothing
									break;
								default:
									break;
								}
					}
					keyExistsInPreexistingAnnotations = true;
				}
				else
					keyExistsInPreexistingAnnotations = false;
			}
			if (!keyExistsInPreexistingAnnotations) { // Key doesn't exist in preexisting annotations.
				model.setSName(key);
				model.setSValue(value);
				if (namespace != null) {
					model.setNamespace(namespace);
				}
			}
		}
		else { // existingAnnotations == null -> model parent has no annotations. Should never be called...
			model.setSName(key);
			model.setSValue(value);
			if (namespace != null) {
				model.setNamespace(namespace);
			}
		}
	}

	private boolean checkAnnotationArray(String[] annotationKeyValue2) {
		// TODO Auto-generated method stub
		return false;
	}

	private String[] constructAnnotationKeyValue(String input) {
		String[] annotationArray = new String[3];
		if (input.contains("::")) { // I.e., has namespace part
			String[] nsKV = input.split(":{2}");
			System.err.println(Arrays.toString(nsKV));
			annotationArray[2] = nsKV[0];
			String[] kV = nsKV[1].split(":");
			System.err.println(Arrays.toString(kV));
			annotationArray[0] = kV[0];
			annotationArray[1] = kV[1];
			System.err.println(Arrays.toString(annotationArray) + "\n");
		}
		else {
			String[] kV = input.split(":");
			annotationArray[0] = kV[0];
			annotationArray[1] = kV[1];
			if (model.getNamespace() != null) {
				annotationArray[2] = model.getNamespace();
			}
		}
		return annotationArray;
	}

	private EList<SAnnotation> getExistingAnnotationsFromModelParent(LabelableElement modelParent) {
		EList<SAnnotation> existingAnnotationsFromModelParent = null;
		if (modelParent instanceof SStructure) {
			SStructure parent = (SStructure) modelParent;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent instanceof SToken) {
			SToken parent = (SToken) modelParent;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent instanceof SSpan) {
			SSpan parent = (SSpan) modelParent;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent instanceof SDominanceRelation) {
			SDominanceRelation parent = (SDominanceRelation) modelParent;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent instanceof SPointingRelation) {
			SPointingRelation parent = (SPointingRelation) modelParent;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		return existingAnnotationsFromModelParent;
	}

	public void setModelParent(LabelableElement labelableElement) {
		this.modelParent = labelableElement;
	}

	/**
	 * @return the annotationKeyValue
	 */
	public String[] getAnnotationKeyValue() {
		return annotationKeyValue;
	}

	/**
	 * @param annotationKeyValue the annotationKeyValue to set
	 */
	public void setAnnotationKeyValue(String[] annotationKeyValue) {
		this.annotationKeyValue = annotationKeyValue;
	}

}

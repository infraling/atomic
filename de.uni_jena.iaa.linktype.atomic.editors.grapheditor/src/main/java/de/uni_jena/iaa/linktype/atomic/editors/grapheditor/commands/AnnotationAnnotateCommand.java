/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
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
	private static final String ANNOTATION_WITH_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:{2}(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ANNOTATION_WITHOUT_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ONE_OR_TWO_COLONS = "(?<!\\\\):{1,2}";
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
		boolean isInputValid = checkInputValidity(getAnnotationInput());
		if (isInputValid) {
			String[] annotationFields = segmentInput(getAnnotationInput());
			int l = annotationFields.length;
			Assert.isLegal(l == 2 || l == 3, "The length of the annotation fields array should be 2 or 3 to hold key, value, and optionally namespace, but it is not.");
			if (l == 2) {
				key = annotationFields[0];
				value = annotationFields[1];
			}
			else if (l == 3) {
				namespace = annotationFields[0];
				key = annotationFields[1];
				value = annotationFields[2];
			}
			else { // Theoretically, this should never be the case, cf. Assert above
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "The length of the annotation fields array should be 2 or 3 to hold key, value, and optionally namespace, but it is not. Please report this error!");
				return;
			}
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Annotation error", "The annotation format is not correct.\nPlease use the following format\n\n[namespace]::[key]:[value]\n\nwhere neither key nor value may be empty, and namespace is optional.\n[namespace], [key] and [value] may contain any characters except the newline character. Additionally, colons used within either field must be escaped with a backslash: \"\\:\".");
			return;
		}

		// FIXME: Clean this up!
		// Determine type of model parent & get its List of annotations
		EList<SAnnotation> existingAnnotations = getExistingAnnotationsFromModelParent(modelParent);
		if (existingAnnotations != null) {
			boolean keyExistsInPreexistingAnnotations = false;
			for (SAnnotation preExistingAnnotation : existingAnnotations) {
				if (preExistingAnnotation.getName().equalsIgnoreCase(key) && preExistingAnnotation != model) {
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
				setAnnotationValues(key, value, namespace);
			}
		}
		else { // existingAnnotations == null -> model parent has no annotations. Should never be called...
			setAnnotationValues(key, value, namespace);
		}
	}

	/**
	 * @param key
	 * @param value
	 * @param namespace
	 */
	private void setAnnotationValues(String key, String value, String namespace) {
		String unescapedKey = key.replaceAll("(?:\\\\:)", ":");
		model.setSName(unescapedKey);
		String unescapedValue = value.replaceAll("(?:\\\\:)", ":");
		model.setSValue(unescapedValue);
		if (namespace != null) {
			String unescapedNamespace = namespace.replaceAll("(?:\\\\:)", ":");
			model.setNamespace(unescapedNamespace);
		}
		else {
			model.setNamespace(null);
		}
	}

	private String[] segmentInput(String annotationInput) {
		Pattern oneOrTwoColons = Pattern.compile(ONE_OR_TWO_COLONS);
		return oneOrTwoColons.split(annotationInput);
	}

	private boolean checkInputValidity(String input) {
		if (input.matches(ANNOTATION_WITHOUT_NAMESPACE_REGEX) || input.matches(ANNOTATION_WITH_NAMESPACE_REGEX)) {
			return true;
		}
		return false;
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

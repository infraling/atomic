/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AnnotationUtils;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationAnnotateCommand extends Command {
	
	private static final int ABORT = -1;
	private static final int SET_ALL = 0;
	private static final int SET_NAMESPACE_AND_VALUE = 1;
	private static final int SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION = 2;
	private static final int ADD_ANNOTATION_IN_NEW_NAMESPACE = 3;
	private static final int CHANGE_OLD_ANNOTATION_VALUE = 4;
	private static final int ADD_ANNOTATION_IN_NEW_NAMESPACE_WITH_NEW_VALUE = 5;
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
		String key = null;
		String value = null;
		String namespace = null;
		boolean isInputValid = AnnotationUtils.checkInputValidity(getAnnotationInput());
		if (isInputValid) {
			String[] annotationFields = AnnotationUtils.segmentInput(getAnnotationInput());
			int numberOfAnnotationFields = annotationFields.length;
			Assert.isLegal(numberOfAnnotationFields == 2 || numberOfAnnotationFields == 3, "The length of the annotation fields array should be 2 or 3 to hold key, value, and optionally namespace, but it is not.");
			if (numberOfAnnotationFields == 2) {
				key = annotationFields[0];
				value = annotationFields[1];
			}
			else if (numberOfAnnotationFields == 3) {
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

		// Determine type of model parent & get its List of annotations
		EList<SAnnotation> existingAnnotations = ((SAnnotatableElement) modelParent).getSAnnotations();
		if (existingAnnotations != null) {
			for (SAnnotation preExistingAnnotation : existingAnnotations) {
				if (preExistingAnnotation.getName().equalsIgnoreCase(key) && preExistingAnnotation != model) { // KEY exists
					int executionType = getExecutionTypeAndObject(preExistingAnnotation, namespace, value, key);
					switch (executionType) {
					case AnnotationAnnotateCommand.ABORT:
						return;
					case AnnotationAnnotateCommand.SET_ALL:
						setAnnotationValues(key, value, namespace);
						return;
					case AnnotationAnnotateCommand.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION:
						preExistingAnnotation.setNamespace(namespace);
						return;
					case AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE:
						setAnnotationValues(key, value, namespace);
						return;
					case AnnotationAnnotateCommand.CHANGE_OLD_ANNOTATION_VALUE:
						preExistingAnnotation.setValue(value);
						modelParent.getLabels().remove(model);
						return;
					case AnnotationAnnotateCommand.SET_NAMESPACE_AND_VALUE:
						preExistingAnnotation.setNamespace(namespace);
						preExistingAnnotation.setValue(value);
						return;
					case AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE_WITH_NEW_VALUE:
						setAnnotationValues(key, value, namespace);
						return;
					default:
						// FIXME: Log an error here!
						break;
					} 
				} 
			} 
			// When having gone through all pre-existing annotations but haven't found `key` in getName() for any of them
			// (i.e., key doesn't exists yet in pre-existing annotations):
			setAnnotationValues(key, value, namespace);
		}
		else { // existingAnnotations == null
			setAnnotationValues(key, value, namespace);
		}
	}

	private int getExecutionTypeAndObject(SAnnotation preExistingAnnotation, String namespace, String value, String key) {
		if (preExistingAnnotation != model) {
			if (!preExistingAnnotation.getName().equalsIgnoreCase(key)) {
				return AnnotationAnnotateCommand.SET_ALL;
			}
			else { // KEY exists
				// Values first cos namespaces may be null
				String oldNamespace = preExistingAnnotation.getNamespace();
				if (preExistingAnnotation.getValue().toString().equalsIgnoreCase(value)) { // VALUE exists
					if (namespace != null && oldNamespace != null) { // Both NS NOT null
						if (!(namespace.equalsIgnoreCase(oldNamespace))) { // NSs differ
							MessageDialog namespacesDifferDialog = createFeedbackDialog("There is already an annotation with the key " + key + " and the value " + value + " for this element, albeit in a different namespace (" + oldNamespace + ")!\nWould you like to\n- Set the existing annotation's namespace to " + namespace + "\n- Add a duplicate of the existing annotation in the namespace " + namespace, new String[]{"Set namespace", "Create duplicate", "Abort"});
							int result = namespacesDifferDialog.open();
							switch (result) {
								case 0: return AnnotationAnnotateCommand.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION;
								case 1: return AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE;
								case 2: return AnnotationAnnotateCommand.ABORT;
								default: break;
							}
						}
						else { // NSs are the same
							MessageDialog abortDialog = createFeedbackDialog("An exact same annotation with the values\n\nNamespace: " + namespace + "\nKey: " + key + "\nValue: " + value + "\n\nalready exists. The operation will be aborted.", new String[]{"OK"});
							int result = abortDialog.open();
							if (result == 0) { // "OK
								return AnnotationAnnotateCommand.ABORT;								
							}	
						}
					}
					else if (namespace == null ^ oldNamespace == null) { // Either NS null
						String setNamespaceText = null;
						String addDuplicateText = null;
						String setButtonText = null;
						if (namespace == null) {
							setNamespaceText = "- Remove the existing annotation's namespace tag";
							addDuplicateText = "- Add a duplicate of the existing annotation without a namespace tag";
							setButtonText = "Remove namespace tag";
						}
						else {
							setNamespaceText = "- Set the existing annotation's namespace to " + namespace;
							addDuplicateText = "- Add a duplicate of the existing annotation in the namespace " + namespace;
							setButtonText = "Set namespace";
						}
						MessageDialog namespacesDifferDialogWithNull = createFeedbackDialog("There is already an annotation with the key " + key + " and the value " + value + " for this element, albeit in a different namespace (" + oldNamespace + ")!\nWould you like to\n" + setNamespaceText + "\n" + addDuplicateText, new String[]{setButtonText, "Add duplicate", "Abort"});
						int result = namespacesDifferDialogWithNull.open();
						switch (result) {
							case 0: return AnnotationAnnotateCommand.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION;
							case 1: return AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE;
							case 2: return AnnotationAnnotateCommand.ABORT;
						default: break;
						}
					}
					else { // Both NS null
						MessageDialog abortDialog = createFeedbackDialog("An exact same annotation with the values\n\nNamespace: " + namespace + "\nKey: " + key + "\nValue: " + value + "\n\nalready exists. The operation will be aborted.", new String[]{"OK"});
						int result = abortDialog.open();
						if (result == 0) { // "OK
							return AnnotationAnnotateCommand.ABORT;								
						}
					}
				}
				else  { // VALUE is NEW
					if (namespace != null && oldNamespace != null) { // Both NS NOT null
						if (!(namespace.equalsIgnoreCase(oldNamespace))) { // NSs differ
							MessageDialog newNamespaceNewValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists, albeit in a different namespace (" + oldNamespace + ") and with a different value (" + preExistingAnnotation.getValue().toString() + ").\nDo you want to change the fields (namespace, value) for the existing annotation, or overwrite the currently edited annotation with a new annotation with the key " + key + " in the namespace " + namespace + ", and assign it the value " + value + "?", new String[]{"Change fields", "Overwrite annotation", "Abort"});
							int result = newNamespaceNewValueDialog.open();
							switch (result) {
							case 0: return AnnotationAnnotateCommand.SET_NAMESPACE_AND_VALUE;
							case 1: return AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE_WITH_NEW_VALUE;
							case 2: return AnnotationAnnotateCommand.ABORT;
							default: break;
							}
						}
						else { // NSs are the same
							MessageDialog changingOldValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists in the namespace " + namespace + "!\nDo you want to change the existing annotation's value (currently " + preExistingAnnotation.getValue().toString() + ") to " + value + " and delete the currently edited anotation (" + model.getName() + ":" + model.getValue().toString() + ")?", new String[]{"Yes", "No"});
							int result = changingOldValueDialog.open();
							if (result == 0) {
								return AnnotationAnnotateCommand.CHANGE_OLD_ANNOTATION_VALUE;
							}
							else {
								return AnnotationAnnotateCommand.ABORT;
							}
						}
					}
					else if (namespace == null ^ oldNamespace == null) { // Either NS null = NSs differ
						MessageDialog newNamespaceNewValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists, albeit in a different namespace (" + oldNamespace + ") and with a different value (" + preExistingAnnotation.getValue().toString() + ").\nDo you want to change the fields (namespace, value) for the existing annotation, or overwrite the currently edited annotation with a new annotation with the key " + key + " in the namespace " + namespace + ", and assign it the value " + value + "?", new String[]{"Change fields", "Overwrite annotation", "Abort"});
						int result = newNamespaceNewValueDialog.open();
						switch (result) {
						case 0: return AnnotationAnnotateCommand.SET_NAMESPACE_AND_VALUE;
						case 1: return AnnotationAnnotateCommand.ADD_ANNOTATION_IN_NEW_NAMESPACE_WITH_NEW_VALUE;
						case 2: return AnnotationAnnotateCommand.ABORT; //
						default: break;
						}
					}
					else { // Both NS null
						MessageDialog changingOldValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists in the namespace " + namespace + "!\nDo you want to change the existing annotation's value (currently " + preExistingAnnotation.getValue().toString() + ") to " + value + " and delete the currently edited anotation (" + model.getName() + ":" + model.getValue().toString() + ")?", new String[]{"Yes", "No"});
						int result = changingOldValueDialog.open();
						if (result == 0) {
							return AnnotationAnnotateCommand.CHANGE_OLD_ANNOTATION_VALUE;
						}
						else {
							return AnnotationAnnotateCommand.ABORT;
						}
					}
				}
			}
		}
		return 666;
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

	private static MessageDialog createFeedbackDialog(String dialogMessage, String[] dialogButtonLabels) {
		return new MessageDialog(Display.getCurrent().getActiveShell(), "Duplicate annotation!", null, dialogMessage, MessageDialog.QUESTION, dialogButtonLabels, 0);
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

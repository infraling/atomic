/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationAnnotateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart.AnnotationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AnnotationUtils;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationDirectEditPolicy extends DirectEditPolicy {
	private static final int ABORT = -1;
	private static final int SET_ALL = 0;
	private static final int SET_NAMESPACE_AND_VALUE = 1;
	private static final int SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION = 2;
	private static final int CHANGE_OLD_ANNOTATION_VALUE = 3;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		SAnnotation model = (SAnnotation) getHost().getModel();
		AnnotationAnnotateCommand command = new AnnotationAnnotateCommand();
	    command.setModel(model);
	    command.setModelParent(model.getSAnnotatableElement());
		String annotationInput = (String) request.getCellEditor().getValue();
		HashMap<SAnnotation,Integer> duplicateAnnotationsToModify = null;

		boolean isInputValid = AnnotationUtils.checkInputValidity(annotationInput);
		if (isInputValid) {
			String namespace = null, key, value;
			String[] annotationFields = AnnotationUtils.segmentInput(annotationInput);
			int numberOfAnnotationFields = annotationFields.length;
			if (!(numberOfAnnotationFields == 2 || numberOfAnnotationFields == 3)) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "The length of the annotation fields array should be 2 or 3 to hold key, value, and optionally namespace, but it is not. Please report this error!");
				return null;
			}
			else {
				if (numberOfAnnotationFields == 2) {
					key = annotationFields[0];
					value = annotationFields[1];
				}
				else {
					namespace = annotationFields[0];
					key = annotationFields[1];
					value = annotationFields[2];
				}
			}
			key = key.replaceAll("(?:\\\\:)", ":");
			value = value.replaceAll("(?:\\\\:)", ":");
			if (namespace != null) {
				namespace = namespace.replaceAll("(?:\\\\:)", ":");
			}
			command.setInputValues(key, value, namespace);
			EList<SAnnotation> existingAnnotations = (((SAnnotation) getHost().getModel()).getSAnnotatableElement()).getSAnnotations();
			if (existingAnnotations != null) {
				for (SAnnotation preExistingAnnotation : existingAnnotations) {
					if (preExistingAnnotation.getName().equalsIgnoreCase(key) && preExistingAnnotation != model) { // KEY exists
						duplicateAnnotationsToModify = new HashMap<SAnnotation, Integer>();
						int executionType = getExecutionTypeAndObject(preExistingAnnotation, namespace, value, key);
						switch (executionType) {
						case AnnotationDirectEditPolicy.ABORT:
							return null;
						case AnnotationDirectEditPolicy.SET_ALL:
							duplicateAnnotationsToModify.put(model, AnnotationAnnotateCommand.SET_ALL);
						case AnnotationDirectEditPolicy.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION:
							duplicateAnnotationsToModify.put(preExistingAnnotation, AnnotationAnnotateCommand.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION);
						case AnnotationDirectEditPolicy.CHANGE_OLD_ANNOTATION_VALUE:
							duplicateAnnotationsToModify.put(preExistingAnnotation, AnnotationAnnotateCommand.CHANGE_OLD_ANNOTATION_VALUE);
						case AnnotationDirectEditPolicy.SET_NAMESPACE_AND_VALUE:
							duplicateAnnotationsToModify.put(preExistingAnnotation, AnnotationAnnotateCommand.SET_NAMESPACE_AND_VALUE);
						default:
							// FIXME: Log an error here!
							break;
						} 
					} 
				} 
			}
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Annotation error", "The annotation format is not correct.\nPlease use the following format\n\n[namespace]::[key]:[value]\n\nwhere neither key nor value may be empty, and namespace is optional.\n[namespace], [key] and [value] may contain any characters except the newline character. Additionally, colons used within either field must be escaped with a backslash: \"\\:\".");
			return null;
		}
		command.setDuplicateAnnotationsToModify(duplicateAnnotationsToModify);
	    return command;
	}

	private int getExecutionTypeAndObject(SAnnotation preExistingAnnotation, String namespace, String value, String key) {
		SAnnotation model = (SAnnotation) getHost().getModel(); 
		// KEY exists
		// Values first cos namespaces may be null
		String oldNamespace = preExistingAnnotation.getNamespace();
		if (preExistingAnnotation.getValue().toString().equalsIgnoreCase(value)) { // VALUE exists
			if (namespace != null && oldNamespace != null) { // Both NS NOT null
				if (!(namespace.equalsIgnoreCase(oldNamespace))) { // NSs differ
					MessageDialog namespacesDifferDialog = createFeedbackDialog("There is already an annotation with the key " + key + " and the value " + value + " for this element, albeit in a different namespace (" + oldNamespace + ")!\nWould you like to\n- Set the existing annotation's namespace to " + namespace + "\n- Add a duplicate of the existing annotation in the namespace " + namespace, new String[]{"Set namespace", "Create duplicate", "Abort"});
					int result = namespacesDifferDialog.open();
					switch (result) {
						case 0: return AnnotationDirectEditPolicy.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION;
						case 1: return AnnotationDirectEditPolicy.SET_ALL;
						case 2: return AnnotationDirectEditPolicy.ABORT;
					default: break;
					}
				}
				else { // NSs are the same
					MessageDialog abortDialog = createFeedbackDialog("An exact same annotation with the values\n\nNamespace: " + namespace + "\nKey: " + key + "\nValue: " + value + "\n\nalready exists. The operation will be aborted.", new String[]{"OK"});
					int result = abortDialog.open();
					if (result == 0) { // "OK
						return AnnotationDirectEditPolicy.ABORT;								
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
					case 0: return AnnotationDirectEditPolicy.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION;
					case 1: return AnnotationDirectEditPolicy.SET_ALL;
					case 2: return AnnotationDirectEditPolicy.ABORT;
				default: break;
				}
			}
			else { // Both NS null
				MessageDialog abortDialog = createFeedbackDialog("An exact same annotation with the values\n\nNamespace: " + namespace + "\nKey: " + key + "\nValue: " + value + "\n\nalready exists. The operation will be aborted.", new String[]{"OK"});
				int result = abortDialog.open();
				if (result == 0) { // "OK
					return AnnotationDirectEditPolicy.ABORT;								
				}
			}
		}
		else  { // VALUE is NEW
			if (namespace != null && oldNamespace != null) { // Both NS NOT null
				if (!(namespace.equalsIgnoreCase(oldNamespace))) { // NSs differ
					MessageDialog newNamespaceNewValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists, albeit in a different namespace (" + oldNamespace + ") and with a different value (" + preExistingAnnotation.getValue().toString() + ").\nDo you want to change the fields (namespace, value) for the existing annotation, or overwrite the currently edited annotation with a new annotation with the key " + key + " in the namespace " + namespace + ", and assign it the value " + value + "?", new String[]{"Change fields", "Overwrite annotation", "Abort"});
					int result = newNamespaceNewValueDialog.open();
					switch (result) {
						case 0: return AnnotationDirectEditPolicy.SET_NAMESPACE_AND_VALUE;
						case 1: return AnnotationDirectEditPolicy.SET_ALL;
						case 2: return AnnotationDirectEditPolicy.ABORT;
					default: break;
					}
				}
				else { // NSs are the same
					MessageDialog changingOldValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists in the namespace " + namespace + "!\nDo you want to change the existing annotation's value (currently " + preExistingAnnotation.getValue().toString() + ") to " + value + " and delete the currently edited anotation (" + model.getName() + ":" + model.getValue().toString() + ")?", new String[]{"Yes", "No"});
					int result = changingOldValueDialog.open();
					if (result == 0) {
						return AnnotationDirectEditPolicy.CHANGE_OLD_ANNOTATION_VALUE;
					}
					else {
						return AnnotationDirectEditPolicy.ABORT;
					}
				}
			}
			else if (namespace == null ^ oldNamespace == null) { // Either NS null = NSs differ
				MessageDialog newNamespaceNewValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists, albeit in a different namespace (" + oldNamespace + ") and with a different value (" + preExistingAnnotation.getValue().toString() + ").\nDo you want to change the fields (namespace, value) for the existing annotation, or overwrite the currently edited annotation with a new annotation with the key " + key + " in the namespace " + namespace + ", and assign it the value " + value + "?", new String[]{"Change fields", "Overwrite annotation", "Abort"});
				int result = newNamespaceNewValueDialog.open();
				switch (result) {
					case 0: return AnnotationDirectEditPolicy.SET_NAMESPACE_AND_VALUE;
					case 1: return AnnotationDirectEditPolicy.SET_ALL;
					case 2: return AnnotationDirectEditPolicy.ABORT; //
				default: break;
				}
			}
			else { // Both NS null
				MessageDialog changingOldValueDialog = createFeedbackDialog("An annotation with the key " + key + " already exists in the namespace " + namespace + "!\nDo you want to change the existing annotation's value (currently " + preExistingAnnotation.getValue().toString() + ") to " + value + " and delete the currently edited anotation (" + model.getName() + ":" + model.getValue().toString() + ")?", new String[]{"Yes", "No"});
				int result = changingOldValueDialog.open();
				if (result == 0) {
					return AnnotationDirectEditPolicy.CHANGE_OLD_ANNOTATION_VALUE;
				}
				else {
					return AnnotationDirectEditPolicy.ABORT;
				}
			}
		}
		return 666;
	}
	
	private static MessageDialog createFeedbackDialog(String dialogMessage, String[] dialogButtonLabels) {
		return new MessageDialog(Display.getCurrent().getActiveShell(), "Duplicate annotation!", null, dialogMessage, MessageDialog.QUESTION, dialogButtonLabels, 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#showCurrentEditValue(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
	    ((AnnotationFigure) getHostFigure()).setText(value);
	}

}

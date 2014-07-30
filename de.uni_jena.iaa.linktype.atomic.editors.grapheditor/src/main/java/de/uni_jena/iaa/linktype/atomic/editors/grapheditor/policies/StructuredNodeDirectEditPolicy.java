/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.StructuredNodeAnnotateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AnnotationUtils;

/**
 * @author Stephan Druskat
 *
 */
public class StructuredNodeDirectEditPolicy extends DirectEditPolicy {

	private TreeMap<String, Pair<String, String>> annotations = new TreeMap<String, Pair<String,String>>();

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		SStructuredNode model = (SStructuredNode) getHost().getModel();
		StructuredNodeAnnotateCommand command = new StructuredNodeAnnotateCommand();
	    command.setModel(model);
		String annotationString = (String) request.getCellEditor().getValue();
		
		String lineSeparator = System.getProperty("line.separator");
		String[] annotationEntries = annotationString.split(lineSeparator);
		for (int i = 0; i < annotationEntries.length; i++) {
			String annotationInput = annotationEntries[i];
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
						key = annotationFields[0].replaceAll("(?:\\\\:)", ":");
						value = annotationFields[1].replaceAll("(?:\\\\:)", ":");
					}
					else {
						namespace = annotationFields[0].replaceAll("(?:\\\\:)", ":");
						key = annotationFields[1].replaceAll("(?:\\\\:)", ":");
						value = annotationFields[2].replaceAll("(?:\\\\:)", ":");
					}
				}
				Pair<String, String> previousNamespaceAndValue = annotations.put(key, Pair.of(namespace, value));
				if (previousNamespaceAndValue != null) {
					boolean shouldAnnoChange = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Duplicate annotation found!", "Duplicate annotation!\n" + "Should the previous value of " + key + " (" + previousNamespaceAndValue.getValue() + ") in the namespace " + previousNamespaceAndValue.getKey() + " be overwritten with " + "the value " + value + " and moved to the namespace " + namespace + "?");
					if (shouldAnnoChange) {
						// Do nothing, key-value pair is already in TreeMap
					}
					else {
						annotations.put(key, previousNamespaceAndValue);
					}
				}
				command.setAnnotations(annotations);
			}
			else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Annotation error", "The annotation format is not correct.\nPlease use the following format\n\n[namespace]::[key]:[value]\n\nwhere neither key nor value may be empty, and namespace is optional.\n[namespace], [key] and [value] may contain any characters except the newline character. Additionally, colons used within either field must be escaped with a backslash: \"\\:\".");
				return null;
			}
		}
		return command;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#showCurrentEditValue(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		// TODO Auto-generated method stub

	}

}

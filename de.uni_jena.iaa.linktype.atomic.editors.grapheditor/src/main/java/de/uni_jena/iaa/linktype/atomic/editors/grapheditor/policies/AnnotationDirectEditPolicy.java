/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationAnnotateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart.AnnotationFigure;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationDirectEditPolicy extends DirectEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		AnnotationAnnotateCommand command = new AnnotationAnnotateCommand();
	    command.setModel((SAnnotation) getHost().getModel());
	    command.setModelParent(((SAnnotation) getHost().getModel()).getLabelableElement());
	    command.setAnnotationInput((String) request.getCellEditor().getValue());
	    return command;
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

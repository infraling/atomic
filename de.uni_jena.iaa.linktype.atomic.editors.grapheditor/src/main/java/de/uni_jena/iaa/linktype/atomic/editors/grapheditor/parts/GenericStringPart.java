/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class GenericStringPart extends AbstractGraphicalEditPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new Label((String) getModel());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void refreshVisuals() {
		Label figure = (Label) getFigure();
		// FIXME: Use FontRegistry!!!
		Font font = new Font(Display.getCurrent(), "sansserif", 11, SWT.BOLD);  // FIXME: Parameterize with Preferences
		figure.setFont(font);
		figure.setForegroundColor(ColorConstants.darkGray); // FIXME: Parameterize with Preferences
		figure.setText((String) getModel());
		getParent().refresh();
	 }

}

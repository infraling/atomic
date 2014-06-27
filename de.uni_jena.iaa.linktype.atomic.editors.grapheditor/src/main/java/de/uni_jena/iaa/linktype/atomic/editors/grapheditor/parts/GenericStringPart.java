/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Stephan Druskat
 *
 */
public class GenericStringPart extends AbstractGraphicalEditPart {
	
	private static final String TOKEN_TEXT_FONT = "tokenTextFont";

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
		// FIXME: Move fonts elsewhere (central definition? GraphPart?)
		if (!JFaceResources.getFontRegistry().hasValueFor(TOKEN_TEXT_FONT)) {
			FontData[] fontDataArray = new FontData[1];
			fontDataArray[0] = new FontData("sansserif", 10, SWT.BOLD); // FIXME: Parameterize with Preferences
			JFaceResources.getFontRegistry().put(TOKEN_TEXT_FONT, fontDataArray);
		}
		Font font = JFaceResources.getFontRegistry().getBold(TOKEN_TEXT_FONT);
		figure.setFont(font);
		figure.setForegroundColor(ColorConstants.darkGray); // FIXME: Parameterize with Preferences
		figure.setText((String) getModel());
		getParent().refresh();
	 }

}

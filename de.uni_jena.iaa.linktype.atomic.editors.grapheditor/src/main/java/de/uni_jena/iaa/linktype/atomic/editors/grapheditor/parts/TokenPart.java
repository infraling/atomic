/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.TokenFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class TokenPart extends AbstractGraphicalEditPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new TokenFigure(PartUtils.getVisualID(getModel()));
	}
	
	@Override
	protected void refreshVisuals() {
		((GraphPart) getParent()).setLayoutConstraint(this, getFigure(), new Rectangle(getModel().getSDocumentGraph().getSTokens().indexOf(getModel()) * 100, 10, getFigure().getPreferredSize().width, getFigure().getPreferredSize().height));
		super.refreshVisuals();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}
	
	public SToken getModel() {
		return (SToken) super.getModel();
	}

}

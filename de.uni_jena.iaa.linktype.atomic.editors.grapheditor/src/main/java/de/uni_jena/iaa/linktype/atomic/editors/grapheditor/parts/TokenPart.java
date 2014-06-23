/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

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
		return new Label();
	}
	
	protected void refreshVisuals() {
		((Label) getFigure()).setText(getModel().getSName());
		((GraphPart) getParent()).setLayoutConstraint(getParent(), getFigure(), new Rectangle(((SDocumentGraph) getParent().getModel()).getSTokens().indexOf(getModel()) * 100, 10, getFigure().getPreferredSize().width, getFigure().getPreferredSize().height));
		getParent().refresh();
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

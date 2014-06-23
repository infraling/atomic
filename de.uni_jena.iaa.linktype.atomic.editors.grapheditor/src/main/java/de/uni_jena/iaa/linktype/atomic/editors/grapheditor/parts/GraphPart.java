/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * @author Stephan Druskat
 *
 */
public class GraphPart extends AbstractGraphicalEditPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3)); // FIXME: Remove
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}
	
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}
	
	protected List<SNode> getModelChildren() {
		List<SNode> modelChildren = new ArrayList<SNode>();
		modelChildren.addAll(getModel().getSTokens());
		return modelChildren;
	}
	
	public SDocumentGraph getModel() {
		return (SDocumentGraph) super.getModel();
	}

}

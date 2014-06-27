/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationPart extends AbstractGraphicalEditPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new AnnotationFigure();
	}
	
	@Override
	protected void refreshVisuals() {
		((Label) getFigure()).setText(getModel().getSName() + ":" + getModel().getValueString());
		getParent().refresh();
	}
	
	public SAnnotation getModel() {
		return (SAnnotation) super.getModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}

	/**
	 * @author Stephan Druskat
	 *
	 */
	public class AnnotationFigure extends Label {
		
		public AnnotationFigure() {
			setBorder(new LineBorder(new Color(Display.getCurrent(), 222, 222, 222), 1, Graphics.LINE_DOT));
			setOpaque(true);
		}
	}

}

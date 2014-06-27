/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
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
		private static final String VERY_LIGHT_GREY = "veryLightGrey";
		
		public AnnotationFigure() {
			Color color = null;
			if (!JFaceResources.getColorRegistry().hasValueFor(VERY_LIGHT_GREY)) {
			    JFaceResources.getColorRegistry().put(VERY_LIGHT_GREY, new RGB(222, 222, 222));
			} else {
			    color = JFaceResources.getColorRegistry().get(VERY_LIGHT_GREY);
			}
			setBorder(new LineBorder(JFaceResources.getColorRegistry().get(VERY_LIGHT_GREY), 1, Graphics.LINE_DOT));
			setOpaque(true);
		}
		
		
	}

}

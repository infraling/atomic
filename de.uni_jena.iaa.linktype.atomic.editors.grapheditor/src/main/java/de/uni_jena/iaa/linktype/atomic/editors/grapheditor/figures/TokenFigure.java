/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class TokenFigure extends Figure {

	private ConnectionAnchor connectionAnchor;

	public TokenFigure(String visualID) {
		setOpaque(false);
		final ToolbarLayout annotationLayout = new ToolbarLayout();
		annotationLayout.setSpacing(1);
		setLayoutManager(annotationLayout);
		setBorder(new TokenFigureBorder(visualID));
	}
	
	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this);
		}
		return connectionAnchor;
	}
	
	public class TokenFigureBorder extends GroupBoxBorder {
		
		public TokenFigureBorder(String visualID) {
			super(visualID);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
		 */
		@Override
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.pushState();
	        Rectangle rect = figure.getBounds();
	        graphics.clipRect(rect);
	        Rectangle r = rect.getResized(-1, -9).translate(0, 8);
	        PartUtils.setColor(graphics, PartUtils.VERYLIGHTGREY, false);
	        graphics.drawRoundRectangle(r, 10, 10);
	        graphics.setForegroundColor(ColorConstants.red);
	        PartUtils.setColor(graphics, PartUtils.MEDIUMLIGHTGREY, true);
	        graphics.fillText(getLabel(), r.x + 8, r.y - 8);
	        graphics.popState();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.AbstractLabeledBorder#calculateInsets(org.eclipse.draw2d.IFigure)
		 */
		@Override
		protected Insets calculateInsets(IFigure figure) {
			return new Insets(15, 2, 2, 2); // TODO calculate Insets possible?
		}
	}

}

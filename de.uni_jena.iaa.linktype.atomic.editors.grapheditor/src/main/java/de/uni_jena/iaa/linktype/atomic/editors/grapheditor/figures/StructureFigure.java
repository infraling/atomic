/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * @author Stephan Druskat
 *
 */
public class StructureFigure extends Figure {

	private ConnectionAnchor connectionAnchor;

	public StructureFigure(String visualID) {
		setOpaque(false);
		final ToolbarLayout annotationLayout = new ToolbarLayout();
		annotationLayout.setSpacing(1);
		setLayoutManager(annotationLayout);
		setBorder(new NodeFigureBorder(visualID));
	}
	
	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this);
		}
		return connectionAnchor;
	}

}

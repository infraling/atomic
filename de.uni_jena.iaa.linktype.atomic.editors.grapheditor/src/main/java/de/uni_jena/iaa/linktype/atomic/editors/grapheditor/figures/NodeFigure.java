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
public class NodeFigure extends Figure {

	public static final int TOKEN_MODEL = 1;
	public static final int STRUCTURE_MODEL = 2;
	public static final int SPAN_MODEL = 3;
	private ConnectionAnchor connectionAnchor;

	public NodeFigure(String visualID, int modelType) {
		setOpaque(false);
		final ToolbarLayout annotationLayout = new ToolbarLayout();
		annotationLayout.setSpacing(1);
		setLayoutManager(annotationLayout);
		setBorder(new NodeFigureBorder(visualID, modelType));
	}
	
	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this);
		}
		return connectionAnchor;
	}

}

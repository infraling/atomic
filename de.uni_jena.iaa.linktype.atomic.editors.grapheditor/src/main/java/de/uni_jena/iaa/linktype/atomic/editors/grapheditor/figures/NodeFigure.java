/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
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
	private LevelTooltipFigure tooltipFigure;

	public NodeFigure(String visualID, int modelType) {
		setOpaque(false);
		final ToolbarLayout annotationLayout = new ToolbarLayout();
		annotationLayout.setSpacing(1);
		setLayoutManager(annotationLayout);
		setBorder(new NodeFigureBorder(visualID, modelType));
		tooltipFigure = new LevelTooltipFigure();
		setToolTip(tooltipFigure);
		addLayoutListener(new LayoutListener() {
			
			@Override
			public void setConstraint(IFigure child, Object constraint) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void remove(IFigure child) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void postLayout(IFigure container) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean layout(IFigure container) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void invalidate(IFigure container) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this);
		}
		return connectionAnchor;
	}
	
	public void setTooltipText(String tooltipText) {
        tooltipFigure.setMessage(tooltipText);
    }

}

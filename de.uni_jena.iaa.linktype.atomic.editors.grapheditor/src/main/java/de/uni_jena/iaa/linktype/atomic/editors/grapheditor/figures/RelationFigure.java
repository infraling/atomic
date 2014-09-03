/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

/**
 * @author Stephan Druskat
 *
 */
public class RelationFigure extends PolylineConnection {

	public static final int DOMINANCERELATION_MODEL = 4;
	public static final int SPANNINGRELATION_MODEL = 5;
	public static final int POINTINGRELATION_MODEL = 6;
	public static final int ORDERRELATION_MODEL = 7;
	private IDLabel label;

	public RelationFigure(String visualID, int modelType) {
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		setTargetDecoration(decoration);
		switch (modelType) {
		case DOMINANCERELATION_MODEL:
			setForegroundColor(ColorConstants.blue);
			break;
		case SPANNINGRELATION_MODEL:
			setForegroundColor(ColorConstants.yellow);
			break;
		case POINTINGRELATION_MODEL:
			setForegroundColor(ColorConstants.red);
			break;
		case ORDERRELATION_MODEL:
			setForegroundColor(ColorConstants.green);
			break;

		default:
			break;
		}
		label = new IDLabel(visualID);
		add(getLabel(), new ConnectionLocator(this, ConnectionLocator.MIDDLE));
	}

	/**
	 * @return the label
	 */
	public IDLabel getLabel() {
		return label;
	}

}

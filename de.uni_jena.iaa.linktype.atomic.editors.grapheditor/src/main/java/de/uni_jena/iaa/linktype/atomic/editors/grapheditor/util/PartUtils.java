/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigureBorder;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.StructurePart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.TokenPart;

/**
 * @author Stephan Druskat
 *
 */
public class PartUtils {

	public static final String SANS10BOLD = "sansserif 10pt bold";
	public static final String VERYLIGHTGREY = "very light grey colour";
	public static final String MEDIUMLIGHTGREY = "medium light grey colour";
	private static final int margin = 5;  // FIXME Hard-coded margin (5), make settable in Prefs
	
	public static String getVisualID(SNode model) {
		LinkedList<String> visualID = new LinkedList<String>();

		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(model.getSName()); 
		while (m.find()) {
		   visualID.add(m.group());
		}
		if (model instanceof SToken) {
			return "T" + visualID.getFirst();
		}
		else if (model instanceof SStructure) {
			return "N" + visualID.getFirst();
		}
		else if (model instanceof SSpan) {
			return "S" + visualID.getFirst();
		}
		return null;
	}

	public static int getTokenX(GraphPart graphPart, SToken model, IFigure iFigure) {
		int tokenX = -1;
		SDocumentGraph graph = graphPart.getModel();
		int currentTokenIndex = graph.getSTokens().indexOf(model);
		if (currentTokenIndex == 0) {
			return margin;
		}
		EList<SToken> tokenList = graph.getSTokens();
		SToken lastToken = tokenList.get(currentTokenIndex - 1);
		for (Object part : graphPart.getChildren()) {
			if (part instanceof TokenPart && ((TokenPart) part).getModel() == lastToken) {
					IFigure lastFigure = ((TokenPart) part).getFigure();
					int lastX = ((Rectangle) graphPart.getFigure().getLayoutManager().getConstraint(lastFigure)).x;
					tokenX = lastX + lastFigure.getPreferredSize().width + margin;
			}
		}
		return tokenX;	}

	public static void setFont(IFigure figure, String fontStyle) {
		FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		if (!fontRegistry.hasValueFor(fontStyle)) {
			addFontToFontRegistry(fontStyle, fontRegistry);
		}
		figure.setFont(fontRegistry.get(fontStyle));
	}

	/**
	 * @param fontStyle
	 * @param fontRegistry
	 */
	private static void addFontToFontRegistry(String fontStyle, FontRegistry fontRegistry) {
		FontData[] fontDataArray = new FontData[1];
		if (fontStyle.equals(SANS10BOLD))
			fontDataArray[0] = new FontData("sansserif", 10, SWT.BOLD); // FIXME: Parameterize with Preferences
		fontRegistry.put(fontStyle, fontDataArray);
	}
	
	public static void setColor(Graphics graphics, String color, boolean isBackgroundColor) {
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (!colorRegistry.hasValueFor(color)) {
			addColorToColorRegistry(color, colorRegistry);
		}
		if (isBackgroundColor)
			graphics.setBackgroundColor(colorRegistry.get(color));
		else
			graphics.setForegroundColor(colorRegistry.get(color));
	}
	
	public static Color getColor(String color) {
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (!colorRegistry.hasValueFor(color)) {
			addColorToColorRegistry(color, colorRegistry);
		}
		return colorRegistry.get(color);
	}

	private static void addColorToColorRegistry(String color, ColorRegistry colorRegistry) {
		if (color.equals(VERYLIGHTGREY))
			colorRegistry.put(VERYLIGHTGREY, new RGB(237, 237, 237));
		else if (color.equals(MEDIUMLIGHTGREY))
			colorRegistry.put(MEDIUMLIGHTGREY, new RGB(222, 222, 222));
	}

	public static void performDirectEditing(AbstractGraphicalEditPart editPart) {
		SingleLineDirectEditManager manager = new SingleLineDirectEditManager(editPart, TextCellEditor.class, new AtomicCellEditorLocator(editPart.getFigure()), editPart.getFigure());
		manager.show();
	}
	
	public static Rectangle calculateStructuredNodeLayout(AbstractGraphicalEditPart part, SStructuredNode model, Figure figure) {
		int[] xY = calculateXY(model, part);
		int width = figure.getPreferredSize().width;
		int height = figure.getPreferredSize().height;
		int x = xY[0] - (width / 2);
		int y = xY[1] - height;
		// Do hit testing - with findFigureAt
		return new Rectangle(x, y, width, height);
	}

	private static int[] calculateXY(SStructuredNode model, AbstractGraphicalEditPart part) {
		int[] xY = new int[2];
		SDocumentGraph graph;
		if (model instanceof SSpan) {
			graph = ((SSpan) model).getSDocumentGraph();
		}
		else if (model instanceof SStructure) {
			graph = ((SStructure) model).getSDocumentGraph();
		}
		else {
			throw new UnsupportedOperationException("An error has occurred. Model is neither SStructure nor SSpan! Please report this error!");
		}
		Node target = null;
		List<Integer> xList = new ArrayList<Integer>();
		List<Integer> yList = new ArrayList<Integer>();
		for(Edge edge : graph.getOutEdges(model.getSId())) {
			target = edge.getTarget();
			AbstractGraphicalEditPart targetEP = (AbstractGraphicalEditPart) part.getViewer().getEditPartRegistry().get(target);
			Rectangle targetConstraints = (Rectangle) part.getFigure().getParent().getLayoutManager().getConstraint(targetEP.getFigure());
			xList.add(targetConstraints.x + (targetConstraints.width / 2));
			yList.add(targetConstraints.y);
		}
		Collections.sort(xList);
		Collections.sort(yList);
		// Calculate x
		int x = 0;
		for (Iterator<Integer> iterator = xList.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			x = x + integer.intValue();
		}
		x = x / xList.size();
		xY[0] = x;
		// Calculate y
		int y = yList.get(0) - 100; // FIXME -100 is hardcoded
		xY[1] = y;
		return xY;
	}

	public static void doOneTimeReLayout(Map<?, ?> editPartRegistry, SDocumentGraph graph, SStructuredNode sStructuredNode) {
		ArrayList<SStructuredNode> nodes = new ArrayList<SStructuredNode>();
		nodes.addAll(graph.getSStructures());
		nodes.addAll(graph.getSSpans());
		nodes.remove(sStructuredNode);
		ArrayList<AbstractGraphicalEditPart> editPartsDefiniteList = new ArrayList<AbstractGraphicalEditPart>(); // FIXME: After refactoring, set arg to other type
		for (SStructuredNode model : nodes) {
			editPartsDefiniteList.add((AbstractGraphicalEditPart) editPartRegistry.get(model));
		}
		boolean mustRepeat;
		int i = 0;
		do {
			mustRepeat = false;
			for (Iterator<SStructuredNode> iterator = nodes.iterator(); iterator.hasNext();) {
				AbstractGraphicalEditPart activePart = (AbstractGraphicalEditPart) editPartRegistry.get(iterator.next());
				IFigure figure = activePart.getFigure();
				for (Object part : editPartRegistry.values()) {
					IFigure partFigure = ((AbstractGraphicalEditPart) part).getFigure();
					IFigure hit = hitTest(figure, partFigure);
					if (hit != null && hit instanceof NodeFigure && hit != figure) {
						moveFigure(activePart, figure, hit, i);
						mustRepeat = true;
					}
				}
			}
			i++;
		} while (mustRepeat);
	}

	private static void moveFigure(AbstractGraphicalEditPart editPart, IFigure figure, IFigure hit, int i) {
		Rectangle layout = figure.getBounds();
		Rectangle bounds = hit.getBounds();
		System.err.println("________ " + ((NodeFigureBorder) figure.getBorder()).getLabel() + " > " + ((NodeFigureBorder) hit.getBorder()).getLabel());
		int x1 = layout.x;
		int x2 = x1 + layout.width;
		int y1 = layout.y;
		int y2 = y1 + layout.height;
		int hx1 = bounds.x;
		int hx2 = hx1 + bounds.width;
		int hy1 = bounds.y;
		int hy2 = hy1 + bounds.height;
		boolean move = true;
		boolean moveLeft = (x1 < hx1 && x2 > hx1 && x2 < hx2);
		boolean moveRight = (x1 > hx1 && x1 < hx2 && x2 > hx2);
		boolean moveUp = (y1 < hy1 && y2 > hy1 && y2 < hy2);
		boolean moveDown = (y1 > hy1 && y1 < hy2 && y2 > hy2);
		boolean xContained = (x1 > hx1 && x2 < hx2);
		boolean yContained = (y1 > hy1 && y2 < hy2);
		boolean completeContained = (xContained && yContained);
		if (move) {
			layout.x = (hx1 - layout.width - 5); // FIXME: Hardcoded 5
			System.err.println((i+1) + " move! " + ((NodeFigureBorder) editPart.getFigure().getBorder()).getLabel());
		}
		if (moveLeft) System.err.println("LEFT");
		if (moveRight) System.err.println("RIGHT");
		if (moveUp) System.err.println("UP");
		if (moveDown) System.err.println("DOWN");
		if (xContained) System.err.println("XCONTAINED");
		if (yContained) System.err.println("YCONTAINED");
		if (completeContained) System.err.println("CONTAINED");
		((GraphPart) editPart.getParent()).setLayoutConstraint(editPart, editPart.getFigure(), layout); // FIXME: Fixed y coord (10). Make settable in Prefs?super.refreshVisuals();
		editPart.getFigure().setBounds(layout);
		SStructuredNode model = (SStructuredNode) editPart.getModel();
		if (model.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS") != null) {
			int versionInt = ((int[]) model.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS").getValue())[2];
			model.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS").setValue(new int[]{layout.x, layout.y, versionInt++});
		}
		else {
			model.createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[]{layout.x, layout.y, 1}, SDATATYPE.SOBJECT);
		}
		
	}

	private static IFigure hitTest(IFigure activeFigure, IFigure figureAgainstWhichToHitTest) {
		Rectangle partFigureBounds = figureAgainstWhichToHitTest.getBounds();
		int partFigureX1 = partFigureBounds.x;
		int partFigureY1 = partFigureBounds.y;
		int partFigureX2 = partFigureX1 + partFigureBounds.width;
		int partFigureY2 = partFigureY1 + partFigureBounds.height;
		Rectangle bounds = activeFigure.getBounds();
		int figureX1 = bounds.x;
		int figureY1 = bounds.y;
		int figureX2 = figureX1 + bounds.width;
		int figureY2 = figureY1 + bounds.height;
				
		// The actual hit test
		if (figureX1 < partFigureX2 && figureX2 > partFigureX1 && figureY1 < partFigureY2 && figureY2 > partFigureY1) {
			return figureAgainstWhichToHitTest;
		}
		return null;
	}

}

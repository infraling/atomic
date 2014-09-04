/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNamedElement;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.SpanPart;
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
	private static final int margin = 50;  // FIXME Hard-coded margin (5), make settable in Prefs
	
	public static String getVisualID(SNamedElement model) {
		LinkedList<String> visualID = new LinkedList<String>();
		
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(((SNamedElement) model).getSName()); 
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
		else if (model instanceof SDominanceRelation) {
			return "D" + visualID.getFirst();
		}
		else if (model instanceof SSpanningRelation) {
			return "R" + visualID.getFirst();
		}
		else if (model instanceof SPointingRelation) {
			return "P" + visualID.getFirst();
		}
		else if (model instanceof SOrderRelation) {
			return "O" + visualID.getFirst();
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
					if (part instanceof StructurePart || part instanceof SpanPart) {
						IFigure partFigure = ((AbstractGraphicalEditPart) part).getFigure();
						IFigure hit = null;
						if (figure.getBounds().intersects(partFigure.getBounds())) {
							hit = partFigure;
						}
						if (hit != null && hit instanceof NodeFigure && hit != figure) {
							moveFigure(activePart, figure, hit, i);
							mustRepeat = true;
						}
					}
				}
			}
			i++;
		} while (mustRepeat);
		if (graph.getSProcessingAnnotation("ATOMIC::IS_LAYOUTED") != null) {
			System.err.println("Graph has already been layouted.");
			// Should never be called!
		}
		else {
			graph.createSProcessingAnnotation("ATOMIC", "IS_LAYOUTED", true, SDATATYPE.SBOOLEAN);
		}
	}

	private static void moveFigure(AbstractGraphicalEditPart editPart, IFigure figure, IFigure hit, int i) {
		Rectangle layout = figure.getBounds();
		Rectangle bounds = hit.getBounds();
		layout = calculateNewLayout(layout, bounds);
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

	private static Rectangle calculateNewLayout(Rectangle oldLayout, Rectangle hitBounds) {
		Rectangle newLayout = null;
		Rectangle intersection = oldLayout.getIntersection(hitBounds);
		Rectangle top = new Rectangle(hitBounds.x, hitBounds.y, hitBounds.width, hitBounds.height / 3);
		Rectangle bottom = new Rectangle(hitBounds.x, hitBounds.y + ((hitBounds.height / 3) * 2), hitBounds.width, hitBounds.height / 3 + 1);
		Rectangle left = new Rectangle(hitBounds.x, hitBounds.y, hitBounds.width / 3, hitBounds.height);
		Rectangle right = new Rectangle(hitBounds.x + ((hitBounds.width / 3) * 2), hitBounds.y, hitBounds.width / 3 + 1, hitBounds.height);

		int areaTopIntersectionWithIntersection = intersection.getIntersection(top).getSize().getArea();
		int areaBottomIntersectionWithIntersection = intersection.getIntersection(bottom).getSize().getArea();
		int areaLeftIntersectionWithIntersection = intersection.getIntersection(left).getSize().getArea();
		int areaRightIntersectionWithIntersection = intersection.getIntersection(right).getSize().getArea();

		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(areaTopIntersectionWithIntersection, "top");
		map.put(areaBottomIntersectionWithIntersection, "top"); // was "bottom", but some nodes will overlap tokens if moved down
		map.put(areaLeftIntersectionWithIntersection, "left");
		map.put(areaRightIntersectionWithIntersection, "right");
		int[] areas = { areaTopIntersectionWithIntersection, areaBottomIntersectionWithIntersection, areaLeftIntersectionWithIntersection, areaRightIntersectionWithIntersection };
		Arrays.sort(areas);
		String largestIntersectionAreaRectangleName = map.get(areas[3]);
		if (areaTopIntersectionWithIntersection == 0 && areaBottomIntersectionWithIntersection == 0 && areaLeftIntersectionWithIntersection == 0 && areaRightIntersectionWithIntersection == 0) {
			// Intersection is completely in uncovered center section -> move right
			largestIntersectionAreaRectangleName = "right";
		}

		if (largestIntersectionAreaRectangleName.equals("top")) {
			newLayout = new Rectangle(oldLayout.x, hitBounds.y 	- oldLayout.height - margin, oldLayout.width, oldLayout.height);
		} else if (largestIntersectionAreaRectangleName.equals("bottom")) {
			newLayout = new Rectangle(oldLayout.x, hitBounds.y + hitBounds.height + margin, oldLayout.width, oldLayout.height);
		} else if (largestIntersectionAreaRectangleName.equals("left")) {
			newLayout = new Rectangle(hitBounds.x - oldLayout.width - margin, oldLayout.y, oldLayout.width, oldLayout.height);
		} else { // MOVE RIGHT
			newLayout = new Rectangle(hitBounds.x + hitBounds.width + margin, oldLayout.y, oldLayout.width, oldLayout.height);
		}
		return newLayout;
	}

}

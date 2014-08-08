/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
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

}

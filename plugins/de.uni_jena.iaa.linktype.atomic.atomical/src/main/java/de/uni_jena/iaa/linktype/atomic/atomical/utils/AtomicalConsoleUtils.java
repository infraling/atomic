/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.utils;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicalConsoleUtils {
	
	public static final String ERR_RED = "red colour for error OutputStream";
	public static final String OUT_GREEN = "green colour for out OutputStream";
	
	/*
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
	*/
	
	private static void addColorToColorRegistry(String color, ColorRegistry colorRegistry) {
		if (color.equals(ERR_RED))
			colorRegistry.put(ERR_RED, new RGB(255, 59, 59));
		else if (color.equals(OUT_GREEN))
			colorRegistry.put(OUT_GREEN, new RGB(0, 153, 51));
	}
	
	public static Color getColor(String color) {
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (!colorRegistry.hasValueFor(color)) {
			addColorToColorRegistry(color, colorRegistry);
		}
		return colorRegistry.get(color);
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.registries;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Bundle;

/**
 * @author Stephan Druskat
 *
 */
public class Registries {
	
	protected static ColorRegistry colorReg;  
    protected static FontRegistry fontReg;  
    private static Registries instance;
//	private static Bundle bundle;  
    
	private Registries() {  
		colorReg = new ColorRegistry();  
		fontReg = new FontRegistry();  
	}  
    
	public static Registries getInstance(Bundle bundle) {  
//		Registries.bundle = bundle;  
		return getInstance();  
    }  

	public static Registries getInstance() {  
		if (instance == null) {  
			instance = new Registries();  
		}  
		return instance;  
	}  
    
	public Registries init(IRegistriesConfiguration configurator) {  
		configurator.configure(this);  
		return this;  
	}  

	public void putColor(String symbolicName, int red, int green, int blue) {
		colorReg.put(symbolicName, new RGB(red, green, blue));
	}
    
	public void putColor(String symbolicName, RGB colorData) {  
		colorReg.put(symbolicName, colorData);  
	}  

	public void putColor(String symbolicName, String hexColorStr) {  
		Integer colorValue = Integer.parseInt(hexColorStr, 16);  
		int red = (colorValue & 0xFF0000) >> 16;  
		int green = (colorValue & 0x00FF00) >> 8;  
		int blue = (colorValue & 0x0000FF);  
		putColor(symbolicName, new RGB(red, green, blue));  
	}  
    
	public Color getColor(String key) {  
		return colorReg.get(key);  
	}  

	public Color getColor(String key, String hexColorStr) {  
		Color color = colorReg.get(key);  
		if (color == null) {  
			putColor(key, hexColorStr);  
			color = colorReg.get(key);  
		}  
		return color;  
	}  
    
	public String getColorAsHex(String key) {  
		String hexColorStr = null;  
		Color color = getColor(key);  
		if (color != null) {  
			hexColorStr = toHex(color.getRGB());  
		}  
		return hexColorStr;  
	}  

	public String toHex(RGB rgb) {  
		String hexColorStr = Integer.toHexString((rgb.red << 16)  
				+ (rgb.green << 8) + rgb.blue);  
		if (hexColorStr.length() == 4) {  
			hexColorStr = "00" + hexColorStr;  
		} 
		else if (hexColorStr.length() == 2) {  
			hexColorStr = "0000" + hexColorStr;  
		}  
		return hexColorStr;  
	}  

	public void putFont(String symbolicName, FontData fontData) {
		fontReg.put(symbolicName, new FontData[] {fontData});
	}
    
	public void putFont(String symbolicName, FontData fontData[]) {  
		fontReg.put(symbolicName, fontData);  
	}  
    
	public Font getFont(String key) {  
         return fontReg.get(key);
	}

}

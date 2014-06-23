/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * @author Stephan Druskat
 *
 */
public class PartUtils {

	public static String getVisualID(SNode model) {
		LinkedList<String> visualID = new LinkedList<String>();

		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(model.getSName()); 
		while (m.find()) {
		   visualID.add(m.group());
		}
		if (model instanceof SToken)
			return "T" + visualID.getFirst();
		return null;
	}

}

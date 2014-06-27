/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPartViewer;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.TokenPart;

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

	public static int getTokenX(EditPartViewer editPartViewer, SToken model, IFigure iFigure) {
		int tokenX = -1;
		SDocumentGraph graph = model.getSDocumentGraph();
		Map<?,?> registry = editPartViewer.getEditPartRegistry();
		GraphPart graphPart = (GraphPart) registry.get(graph);
		int currentTokenIndex = graph.getSTokens().indexOf(model);
		EList<SToken> tokenList = graph.getSTokens();
		Collection<?> registryValues = registry.values();
		for (Object part : registryValues) {
			if (part instanceof TokenPart) {
				if (tokenList.indexOf(((TokenPart) part).getModel()) == (currentTokenIndex - 1)) {
					IFigure lastFigure = ((TokenPart) part).getFigure();
					int lastX = ((Rectangle) graphPart.getFigure().getLayoutManager().getConstraint(lastFigure)).x;
					tokenX = lastX + lastFigure.getPreferredSize().width + 10; // FIXME Hard-coded margin (10), make settable in Prefs
				}
			}
		}
		return tokenX;
	}

	public static String getTokenText(SToken model) {
		// TODO Auto-generated method stub
		return null;
	}

}

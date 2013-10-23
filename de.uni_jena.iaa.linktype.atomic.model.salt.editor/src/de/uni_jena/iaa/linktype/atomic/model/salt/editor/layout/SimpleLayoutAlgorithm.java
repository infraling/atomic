/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SDocumentGraphEditPart;

/**
 * @author Stephan Druskat
 *
 */
public class SimpleLayoutAlgorithm {

	private static final int _OFFSET_NEWNODE_PARAMETERNODES = 70;

	public static Point calculateLocation(ArrayList<String> elements, SDocumentGraph graph, SDocumentGraphEditPart graphEditPart, boolean newNodeIsParent) {
		Point location = new Point(100, 100); // Default

		// Get model elements from elements
		// ### FIXME: This is duplicate code (used in, e.g., AtomicALConsole as well): Refactor to Utils class
		ArrayList<SNode> children = new ArrayList<SNode>();
		for (int i = 0; i < elements.size(); i++) {
			String iD = elements.get(i).substring(1);
			switch (elements.get(i).charAt(0)) {
			case 'N': // SStructure
				for (SStructure structure : graph.getSStructures()) {
					String valueString = structure.getSName().substring(9);
					if (iD.equals(valueString))
						children.add(structure);
				}
				break;
			
			case 'T': // SToken
				for (SToken token : graph.getSTokens()) {
					String valueString = token.getSName().substring(4);
					if (iD.equals(valueString))
						children.add(token);
				}
				break;
			
			default:
				break;
			}
		}
		// eo ### FIXME: This is duplicate code (used in, e.g., AtomicALConsole as well): Refactor to Utils class
		
		int x = calculateX(graphEditPart, children);
		int y = calculateY(graphEditPart, children, newNodeIsParent);

		// hit test for x
//		for (Object child : graphEditPart.getFigure().getChildren()) {
//			if (child instanceof IFigure && ((IFigure) child).containsPoint(new Point(x, y))) {
//				IFigure hit = (IFigure) child;
//				Rectangle bounds = hit.getBounds();
//				int width = hit.getSize().width;
//				x = bounds.x + width + 10; // FIXME?: Hardcoded (but should be okay since hardcoding has no negative effects...)
//			}
//		}
		
		location.setX(x);
		location.setY(y);
		return location;
	}

	private static int calculateY(SDocumentGraphEditPart graphEditPart, ArrayList<SNode> children, boolean newNodeIsParent) {
		ArrayList<Integer> childrenY = new ArrayList<Integer>();
		for (SNode node : children) {
			Integer y = null;
			for (Object ep : graphEditPart.getChildren()) {
				if (ep instanceof AbstractGraphicalEditPart) {
					AbstractGraphicalEditPart gep = (AbstractGraphicalEditPart) ep;
					if (gep.getModel() == node) {
						y = ((Rectangle) graphEditPart.getFigure().getLayoutManager().getConstraint(gep.getFigure())).y;
					}
				}
			}
			childrenY.add(y);
		}
		Collections.sort(childrenY);
		int lowestY = childrenY.get(0);
		int highestY = childrenY.get(childrenY.size() - 1);
		if (newNodeIsParent)
			return lowestY - _OFFSET_NEWNODE_PARAMETERNODES; // FIXME Hardcoded
		else
			return highestY + _OFFSET_NEWNODE_PARAMETERNODES; // FIXME Hardcoded
	}

	/**
	 * @param graphEditPart
	 * @param children
	 * @return
	 */
	public static int calculateX(SDocumentGraphEditPart graphEditPart, ArrayList<SNode> children) {
		ArrayList<Integer> childrenX = new ArrayList<Integer>();
		for (SNode node : children) {
			Integer x = null;
			for (Object ep : graphEditPart.getChildren()) {
				if (ep instanceof AbstractGraphicalEditPart) {
					AbstractGraphicalEditPart gep = (AbstractGraphicalEditPart) ep;
					if (gep.getModel() == node) {
						x = ((Rectangle) graphEditPart.getFigure().getLayoutManager().getConstraint(gep.getFigure())).x;
					}
				}
			}
			childrenX.add(x);
		}
		int returnX = calculateAverageX(childrenX);
		return returnX;
	}

	private static int calculateAverageX(ArrayList<Integer> childrenX) {
		try {
			int sumX = 0;
			for (Integer mark : childrenX) {
				sumX += mark;
			}
			return sumX / childrenX.size();
		} 
		catch (ArithmeticException e) { // / 0!
			e.printStackTrace();
		}
		return 0;
	}

}

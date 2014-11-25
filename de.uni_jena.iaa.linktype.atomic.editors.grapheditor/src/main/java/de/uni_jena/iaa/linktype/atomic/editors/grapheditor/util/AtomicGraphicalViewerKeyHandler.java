/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeCreateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler {
	
	private static final Logger log = LoggerFactory.getLogger(AtomicGraphicalViewerKeyHandler.class);

	/**
	 * @param viewer
	 */
	public AtomicGraphicalViewerKeyHandler(GraphicalViewer viewer) {
		super(viewer);
	}

	/**
	 * @return <code>true</code> if key pressed indicates a direct edit Carriage
	 *         return key = Enter/Return
	 */
	boolean acceptDirectEdit(KeyEvent event) {
		return event.character == SWT.CR;
	}

	public boolean keyPressed(KeyEvent event) {
		if (acceptDirectEdit(event)) {
			directEditContainer(event);
			return true;
		}
		if ((event.stateMask & SWT.CTRL) != 0) {
			if (event.character == '1') {
				log.info("Creating SSpan with keyboard shortcut CTRL+1");
				createSpan();
			} else if (event.character == '0') {
				log.info("Creating SStructure with keyboard shortcut CTRL+0");
				createStructure();
			}
		}
		return super.keyPressed(event);
	}

	private void createStructure() {
		final GraphicalViewer viewer = getViewer();
		EditPart graphPart = viewer.getRootEditPart().getContents();
		if (graphPart instanceof GraphPart) {
			final NodeCreateCommand createNodeCommand = new NodeCreateCommand();
			createNodeCommand.setGraph((SDocumentGraph) graphPart.getModel());
			List<EditPart> selectedEditParts = viewer.getSelectedEditParts();
			if (selectedEditParts.isEmpty()) {
				createNodeCommand.setLocation(new Point(100, 100)); // FIXME: Or
			}
			else {
				int y = -1; // FIXME: Hardcoded, tie to prefs
				int x = -1;
				List<Integer> xList = new ArrayList<Integer>();
				List<Integer> yList = new ArrayList<Integer>();
				for (Object part : selectedEditParts) {
					if (part instanceof AbstractGraphicalEditPart) {
						Rectangle bounds = ((AbstractGraphicalEditPart) part).getFigure().getBounds();
						yList.add(bounds.y);
						xList.add(bounds.x);
						xList.add(bounds.x + bounds.width);
					}
				}
				Collections.sort(yList);
				Collections.sort(xList);
				y = yList.get(0) - 100;
				x = (xList.get(xList.size() - 1) + xList.get(0)) / 2;
				createNodeCommand.setLocation(new Point(x, y));
			}
			SStructure sStructure = SaltFactory.eINSTANCE.createSStructure();
			createNodeCommand.setModel(sStructure);
			createNodeCommand.setSelectedEditParts(selectedEditParts);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						viewer.getEditDomain().getCommandStack().execute(createNodeCommand);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private void createSpan() {
		final GraphicalViewer viewer = getViewer();
		EditPart graphPart = viewer.getRootEditPart().getContents();
		if (graphPart instanceof GraphPart) {
			final NodeCreateCommand createNodeCommand = new NodeCreateCommand();
			createNodeCommand.setGraph((SDocumentGraph) graphPart.getModel());
			List<EditPart> selectedEditParts = viewer.getSelectedEditParts();
			if (selectedEditParts.isEmpty()) {
				createNodeCommand.setLocation(new Point(100, 100)); // FIXME: Or
			}
			else {
				int y = 500; // FIXME: Hardcoded, tie to prefs
				int x = -1;
				List<Integer> xList = new ArrayList<Integer>();
				for (Object part : selectedEditParts) {
					if (part instanceof AbstractGraphicalEditPart) {
						Rectangle bounds = ((AbstractGraphicalEditPart) part).getFigure().getBounds();
						xList.add(bounds.x);
						xList.add(bounds.x + bounds.width);
					}
				}
				Collections.sort(xList);
				x = (xList.get(xList.size() - 1) + xList.get(0)) / 2;
				createNodeCommand.setLocation(new Point(x, y));
			}
			SSpan sSpan = SaltFactory.eINSTANCE.createSSpan();
			createNodeCommand.setModel(sSpan);
			createNodeCommand.setSelectedEditParts(selectedEditParts);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						viewer.getEditDomain().getCommandStack().execute(createNodeCommand);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private void directEditContainer(KeyEvent event) {
		GraphicalEditPart focus = getFocusEditPart();
		focus.performRequest(new Request(RequestConstants.REQ_DIRECT_EDIT));
	}

}

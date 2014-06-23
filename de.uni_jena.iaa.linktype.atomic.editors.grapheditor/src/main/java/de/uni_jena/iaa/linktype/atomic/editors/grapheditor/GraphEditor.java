/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditor extends GraphicalEditorWithFlyoutPalette {

	private boolean isModelGraph = false; // Used for instance-testing for setContents()
	private HashSet<SToken> tokenMap; // For use as model for sub-graph editing
	private SDocumentGraph graph; // For use as model for graph editing

	/**
	 * 
	 */
	public GraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
	
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new AtomicEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
	}
	
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		if (isModelGraph) {
			getGraphicalViewer().setContents(graph);
		}
		else {
			getGraphicalViewer().setContents(tokenMap);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		System.err.println(input.getClass());
		
//		if (input.g)
	}

}

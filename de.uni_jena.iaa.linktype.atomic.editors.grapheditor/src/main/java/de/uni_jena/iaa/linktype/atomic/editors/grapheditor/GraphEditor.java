/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditor extends GraphicalEditorWithFlyoutPalette {

	/**
	 * 
	 */
	public GraphEditor() {
		// TODO Auto-generated constructor stub
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

}

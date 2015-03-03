/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditingPerspective implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		// Don't use this, instead, declare perspectiveExtensions!
		// Cf. http://eclipse.dzone.com/articles/perspective-layouts-programmat
	}

}

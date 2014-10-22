/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.GraphEditor;

/**
 * @author Stephan Druskat
 *
 */
public class HasActiveEditorTester extends PropertyTester {

	/**
	 * 
	 */
	public HasActiveEditorTester() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean isTestPassed = false;
		if ("hasActiveEditor".equals(property)) {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editor instanceof GraphEditor) {
				isTestPassed = true;
			}
		}
		return isTestPassed;
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;

/**
 * @author Stephan Druskat
 *
 */
public class SelectionPropertyTester extends PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean isTestPassed = false;
		if ("hasNonEmptyTextSelection".equals(property)) {
			TextSelection textSelection = null;
			try {
				IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
				if (activePart instanceof CoreferenceEditor) {
					CoreferenceEditor editor = (CoreferenceEditor) activePart;
					textSelection = testSelectionOnPart(editor);
				}
				isTestPassed = !textSelection.getText().isEmpty();
			} catch (NullPointerException e) {
				// Do nothing, will throw an NPE at opening of editor,
				// probably because the part hasn't been activeated yet.
			}
		}
		return isTestPassed;
	}

	/**
	 * @param view
	 * @return 
	 */
	private TextSelection testSelectionOnPart(IWorkbenchPart view) {
		ISelection viewSiteSelection = view.getSite().getSelectionProvider().getSelection();
		if (viewSiteSelection instanceof TextSelection) {
			return (TextSelection) viewSiteSelection;
		}
		return null;
	}

}

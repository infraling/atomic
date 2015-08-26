/**
 * Handler class providing execute method for exiting Atomic. 
 */
package de.uni_jena.iaa.linktype.atomic.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

public class ExitHandler {
	/**
	 * Handles exiting the Atomic workbench.
	 * @param workbench The current workbench
	 */
	@Execute
	public void execute(IWorkbench workbench) {
		workbench.close();
	}
		
}
/**
 * Handler class providing execute method for exiting Atomic. 
 */
package de.uni_jena.iaa.linktype.atomic.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

public class ExitHandler {
	
	private static Logger log = LogManager.getLogger(ExitHandler.class);

	/**
	 * Handles exiting the Atomic workbench.
	 * @param workbench The current workbench
	 */
	@Execute
	public void execute(IWorkbench workbench) {
		log.trace("Close Atomic workbench.");
		boolean shutdownSuccess = workbench.close();
		if (!shutdownSuccess) {
			log.error("Shutdown of Atomic workbench did not complete successfully.");
		}
		else {
			log.trace("Atomic shut down successfully.");
		}
	}
		
}
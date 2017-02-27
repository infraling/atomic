/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;

/**
 * @author Stephan Druskat
 *
 */
public final class OpenAtomicalConsoleHandler {
	
	@Execute
	public Object execute() throws ExecutionException {
		AtomicalConsoleFactory acf = new AtomicalConsoleFactory();
		acf.openConsole();
		return null;
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author Stephan Druskat
 *
 */
public final class OpenAtomicalConsoleHandler extends AbstractHandler {
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AtomicalConsoleFactory acf = new AtomicalConsoleFactory();
		acf.openConsole();
		return null;
	}

}

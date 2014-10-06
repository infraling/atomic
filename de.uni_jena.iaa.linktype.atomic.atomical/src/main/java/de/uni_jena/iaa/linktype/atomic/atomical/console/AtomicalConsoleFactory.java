/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

/**
 * @author Stephan Druskat
 * Creates and opens an instance of AtomicalConsole in the ConsoleView
 * 
 */
public class AtomicalConsoleFactory implements IConsoleFactory {

	private static AtomicalConsole _console;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
	 */
	@Override
	public void openConsole() {
		AtomicalConsole console = getConsole();
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			IConsole[] existing = manager.getConsoles();
			boolean exists = false;
			// Check if instances of this console already exist
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists)
				manager.addConsoles(new IConsole[] { console });
			manager.showConsoleView(console);
			console.activate();
		}
	}

	private static AtomicalConsole getConsole() {
		if (_console == null) {
			_console = new AtomicalConsole("AtomicAL Console", null);
		}
		return _console;
	}

}

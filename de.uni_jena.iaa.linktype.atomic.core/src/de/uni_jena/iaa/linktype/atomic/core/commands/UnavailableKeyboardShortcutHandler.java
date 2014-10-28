/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Stephan Druskat
 *
 */
public class UnavailableKeyboardShortcutHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Keyboard shortcut not available!", "This Eclipse keyboard shortcut is not available in Atomic.");
		return null;
	}

}

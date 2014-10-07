/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Stephan Druskat
 *
 */
public class CreateMarkableHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Key pressed!", "You pressed CTRL + M. Creating a markable now ...");
		return null;
	}

}

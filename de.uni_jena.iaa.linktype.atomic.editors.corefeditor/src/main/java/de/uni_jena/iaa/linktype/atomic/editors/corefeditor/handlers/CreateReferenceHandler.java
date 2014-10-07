/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author Stephan Druskat
 *
 */
public class CreateReferenceHandler extends AbstractCreateMarkablesHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);
		// Open reference editor, add span, give reference a name
		return null;
	}

}

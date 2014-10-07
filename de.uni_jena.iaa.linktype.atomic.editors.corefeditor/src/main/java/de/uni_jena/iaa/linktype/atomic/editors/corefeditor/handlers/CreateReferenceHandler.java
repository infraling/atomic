/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class CreateReferenceHandler extends CreateMarkableHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);
		// Open reference editor, add span, give reference a name
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		SDocumentGraph graph = getSpan().getSDocumentGraph();
		if (page != null) {
			System.err.println(graph);
			try {
				page.showView("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}

/**
 * 
 */
package org.corpus_tools.atomic.api.commands;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class DocumentGraphAwareHandler extends AbstractHandler {
	
	private final SDocumentGraph graph;
	
	/**
	 * 
	 */
	public DocumentGraphAwareHandler() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor instanceof DocumentGraphEditor) {
			graph = ((DocumentGraphEditor) editor).getGraph();
		}
		else {
			graph = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the graph
	 */
	public final SDocumentGraph getGraph() {
		return graph;
	}

}

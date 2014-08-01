/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditorActionBarContributor extends ActionBarContributor {

	@Override
	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
//	    toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
//	    toolBarManager.add(getAction(ActionFactory.REDO.getId()));
	    toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
//	    toolBarManager.add(new ZoomComboContributionItem(getPage()));
//	    toolBarManager.add(new AtomicCommandLineTextFieldContributionItem(getPage()));
	}	

	@Override
	protected void declareGlobalActionKeys() {
		// TODO Auto-generated method stub
		
	}

	

}

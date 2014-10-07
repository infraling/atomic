/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd.ReferenceViewDropListener;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceView extends ViewPart {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(parent);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
	    treeViewer.setContentProvider(new ReferenceTreeContentProvider());
	    treeViewer.setLabelProvider(new ReferenceTreeLabelProvider());
	    treeViewer.setInput(new MyTreeNode(0));
	    treeViewer.addDropSupport(operations, transferTypes, new ReferenceViewDropListener(treeViewer));
	    treeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					treeViewer.setSubtreeChecked(event.getElement(), true);
				}
				else {
					treeViewer.setSubtreeChecked(event.getElement(), false);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	// FIXME: DELETE ##############################################################
	
	// FIXME: DELETE ##############################################################

}

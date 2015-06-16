/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.linkedsentences;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;

/**
 * @author Stephan Druskat
 *
 */
public class LinkedSentencesView extends ViewPart implements ISelectionListener {
	
	ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection incomingSelection) {
			if (part instanceof EditorPart) {
				// Ignore selection, since the GraphEditor, e.g., can select single Spans as well
			}
			if (incomingSelection instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) incomingSelection;
				if (selection.getFirstElement() instanceof IStructuredSelection) { // SentenceView wraps twice
					if (((IStructuredSelection) selection.getFirstElement()).getFirstElement() instanceof SSpan) {
						SSpan sentenceSpan = (SSpan) ((IStructuredSelection) selection.getFirstElement()).getFirstElement();
						System.err.println(":> " + sentenceSpan.getSName());
					}
				}
			}
		}
	};

	/**
	 * 
	 */
	public LinkedSentencesView() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		getSite().getPage().addSelectionListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		System.err.println("In implentation class, THIS WORKS WELL!");
	}

}

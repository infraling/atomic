/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.sentenceview;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Stephan Druskat
 *
 */
public class SentenceView extends ViewPart implements ISelectionProvider {

	private ListenerList listeners = new ListenerList();

	private MyModel[] input;
	private int qgzcount = 5;
	private CheckboxTableViewer myTableViewer;

	public SentenceView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		myTableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		// getSite().setSelectionProvider(myTableViewer);
		getSite().setSelectionProvider(this);
		myTableViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				for (int i = 0; i < listeners.getListeners().length; i++) {
					((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(SentenceView.this, new StructuredSelection(myTableViewer.getCheckedElements())));
				}
			}
		});

		Table myTable = (Table) myTableViewer.getControl();

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnPixelData(50));
		tableLayout.addColumnData(new ColumnPixelData(50));
		tableLayout.addColumnData(new ColumnPixelData(50));
		myTable.setLayout(tableLayout);

		myTableViewer.setContentProvider(new MyContentProvider());

		TableViewerColumn vNameColumn0 = new TableViewerColumn(myTableViewer, SWT.LEFT);
		TableColumn nameColumn0 = vNameColumn0.getColumn();
		nameColumn0.setText("Vorname");

		vNameColumn0.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MyModel) element).getVorname();
			}
		});

		TableViewerColumn vNameColumn1 = new TableViewerColumn(myTableViewer, SWT.LEFT);
		TableColumn nameColumn1 = vNameColumn1.getColumn();
		nameColumn1.setText("Nachname");

		vNameColumn1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MyModel) element).getNachname();
			}
		});

		TableViewerColumn vNameColumn2 = new TableViewerColumn(myTableViewer, SWT.LEFT);
		TableColumn nameColumn2 = vNameColumn2.getColumn();
		nameColumn2.setText("Counter");

		vNameColumn2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MyModel) element).getCounter();
			}
		});

		input = new MyModel[qgzcount];
		DecimalFormat df = new DecimalFormat("000");
		for (int i = 0; i < qgzcount; i++)
			input[i] = new MyModel("Ein", "Test", "" + df.format(i));
		myTableViewer.setInput(input);
		myTable.setHeaderVisible(true);
		myTable.setLinesVisible(true);
	}

	@Override
	public void setFocus() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(myTableViewer.getCheckedElements());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; i++) {
			((ISelectionChangedListener) list[i]).selectionChanged(new SelectionChangedEvent(this, new StructuredSelection(myTableViewer.getCheckedElements())));
		}
	}

}

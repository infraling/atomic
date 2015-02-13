/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.sentenceview;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * @author Stephan Druskat
 * 
 */
public class SentenceView extends ViewPart implements ISelectionProvider {

	private ListenerList listeners = new ListenerList();

	private CheckboxTableViewer sentenceTableViewer;

	@Override
	public void createPartControl(Composite parent) {
		getSite().setSelectionProvider(this);
		parent.setLayout(new GridLayout(2, false));

		addSelectionButtons(parent);

		sentenceTableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		sentenceTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		sentenceTableViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				notifySelectionListeners();
			}
		});

		Table sentenceTable = (Table) sentenceTableViewer.getControl();
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(100, 50, true));
		sentenceTable.setLayout(tableLayout);

		sentenceTableViewer.setContentProvider(new SentenceContentProvider());

		TableViewerColumn viewerCol = new TableViewerColumn(sentenceTableViewer, SWT.LEFT);
		TableColumn col = viewerCol.getColumn();
		col.setText("Sentence");
		viewerCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return retrieveSentenceFromSpan((SSpan) element);
			}
		});

		sentenceTableViewer.setInput(getInput());
		sentenceTableViewer.getTable().setHeaderVisible(true);
		sentenceTableViewer.getTable().setLinesVisible(true);
	}

	/**
	 * @param element
	 * @return
	 */
	protected String retrieveSentenceFromSpan(SSpan span) {
		EList<SToken> overlappedTokens = span.getSDocumentGraph().getOverlappedSTokens(span, new BasicEList<STYPE_NAME>(Arrays.asList(STYPE_NAME.SSPANNING_RELATION)));
		EList<SToken> sortedTokens = span.getSDocumentGraph().getSortedSTokenByText(overlappedTokens);
		String sentence = "";
		for (int i = 0; i < sortedTokens.size(); i++) {
			String tokenText = getTokenText(sortedTokens.get(i));
			if (i == 0) {
				sentence = sentence + tokenText;
			}
			else {
				sentence = sentence + " " + tokenText;
			}
		}
		if (!sentence.isEmpty()) {
			return sentence;
		}
		return null;
	}

	/**
	 * @param sToken
	 * @return
	 */
	private String getTokenText(SToken token) {
		for (Edge edge : token.getSDocumentGraph().getOutEdges(token.getSId())) {
			if (edge instanceof STextualRelation) {
				STextualRelation textualRelation = (STextualRelation) edge;
				return token.getSDocumentGraph().getSTextualDSs().get(0).getSText().substring(textualRelation.getSStart(), textualRelation.getSEnd());
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private SDocumentGraph getInput() {
		IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) editorInput).getFile();
			return ModelRegistry.getModel(file);
		}
		return null;
	}

	/**
	 * 
	 */
	private void notifySelectionListeners() {
		for (int i = 0; i < listeners.getListeners().length; i++) {
			((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(SentenceView.this, new StructuredSelection(sentenceTableViewer.getCheckedElements())));
		}
	}

	/**
	 * @param parent
	 */
	private void addSelectionButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.RIGHT);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		Button selectButton = createButton(buttonComposite, "Select all", GridData.HORIZONTAL_ALIGN_FILL);
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sentenceTableViewer.setAllChecked(true);
				notifySelectionListeners();
			}
		};
		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite, "Deselect all", GridData.HORIZONTAL_ALIGN_FILL);
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sentenceTableViewer.setAllChecked(false);
				notifySelectionListeners();
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	/**
	 * @param buttonComposite
	 * @param string
	 * @param horizontalAlignFill
	 * @return
	 */
	private Button createButton(Composite parent, String label, int style) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		GridData data = new GridData(style);
		button.setLayoutData(data);
		return button;
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
		return new StructuredSelection(sentenceTableViewer.getCheckedElements());
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
			((ISelectionChangedListener) list[i]).selectionChanged(new SelectionChangedEvent(this, new StructuredSelection(sentenceTableViewer.getCheckedElements())));
		}
	}

}

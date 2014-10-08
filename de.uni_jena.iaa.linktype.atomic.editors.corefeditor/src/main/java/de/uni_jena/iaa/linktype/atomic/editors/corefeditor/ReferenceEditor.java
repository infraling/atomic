/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd.ReferenceViewDropListener;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceCellEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceTreeContentProvider;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceTreeLabelProvider;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceEditor extends EditorPart {

	public static final String ID = "de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceeditor";
	private ReferenceModel model;
	private SDocumentGraph graph;
	private CheckboxTreeViewer treeViewer;
	private SourceViewer textViewer;
	private String text;
	private StyledText styledText;

	/**
	 * 
	 */
	public ReferenceEditor() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		Composite viewComposite = new Composite(sashForm, SWT.NONE);
//		viewComposite.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		createViewArea(viewComposite);
		Composite treeComposite = new Composite(sashForm, SWT.NONE);
		try {
			model = resolveModel(getEditorInput());	
		} catch (NullPointerException e) {
			System.err.println("Couldn't load model!");
			e.printStackTrace();
			return;
		}
		createContentTree(treeComposite);
		sashForm.setWeights(new int[] {1, 1});
		treeViewer.setInput(model);
		IDocument document = new Document();
		System.err.println(text);
		document.set(text);
		textViewer.setDocument(document);
//		textViewer.refresh();
		}

	private ReferenceModel resolveModel(IEditorInput editorInput) {
		ReferenceModel model = null;
		IFileEditorInput fileInput = (IFileEditorInput) editorInput;
		IFile file = fileInput.getFile();
		String fileName = file.getName();
		// FIXME Check if input is a SaltProject file, and if it is, display info about SaltProject
		if (fileName.equals("saltProject." + SaltFactory.FILE_ENDING_SALT)) {
			StringBuilder contents = buildSaltProjectInfo(file);
			text = contents.toString();
		}
		// Check if input is a .salt file, and if it is not a SaltProject,
		// i.e., if it is a persisted SDocument.
		else if (fileName.endsWith(SaltFactory.FILE_ENDING_SALT) && !fileName.equals("saltProject." + SaltFactory.FILE_ENDING_SALT)) {
			SDocument sDoc = SaltFactory.eINSTANCE.createSDocument();
			URI graphURI = URI.createFileURI(file.getLocation().toOSString());
			sDoc.loadSDocumentGraph(graphURI);
			SDocumentGraph sDocGraph = sDoc.getSDocumentGraph();
			setGraph(sDocGraph);
			model = new ReferenceModel(sDocGraph);
			text = sDocGraph.getSTextualDSs().get(0).getSText();
		}
		return model;
	}

	private void createContentTree(Composite treeComposite) {
		treeComposite.setLayout(new FillLayout());
		treeViewer = new CheckboxTreeViewer(treeComposite, SWT.BORDER);
		treeViewer.setContentProvider(new ReferenceTreeContentProvider(model));
		treeViewer.setLabelProvider(new ReferenceTreeLabelProvider());
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addDropSupport(operations, transferTypes, new ReferenceViewDropListener(treeViewer));
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					treeViewer.setSubtreeChecked(event.getElement(), true);
				} else {
					treeViewer.setSubtreeChecked(event.getElement(), false);
				}
			}
		});
		addEditingSupport(treeViewer);
	}

	private void createViewArea(Composite parent) {
		parent.setLayout(new FillLayout());
		Composite viewPanel = new Composite(parent, SWT.NONE);
		viewPanel.setLayout(new FillLayout());
		textViewer = new SourceViewer(viewPanel, null /* TODO: introduce IVerticalRuler later*/, null /* TODO check if OverviewRuler is helpful*/, false /* TODO check if needed later */, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		styledText = textViewer.getTextWidget();
		// Add menus
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(textViewer.getControl ());
		textViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, textViewer);
		// Makes the selection available to the workbench
	    getSite().setSelectionProvider(textViewer);
	}


		private void addEditingSupport(final CheckboxTreeViewer treeViewer) {
		final TreeViewerColumn column = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		column.setLabelProvider(new ReferenceTreeLabelProvider());
		final ReferenceCellEditor cellEditor = new ReferenceCellEditor(
				treeViewer.getTree());

		column.setEditingSupport(new EditingSupport(treeViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Reference) {
					Reference data = (Reference) element;
					data.setName(value.toString());
				}
				treeViewer.update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof Reference) {
					return ((Reference) element).getName();
				} 
				else if (element instanceof SSpan) {
					EList<STYPE_NAME> reList = new BasicEList<STYPE_NAME>();
					reList.add(STYPE_NAME.SSPANNING_RELATION);
					EList<SToken> tokens = ((SSpan) element).getSDocumentGraph().getOverlappedSTokens(((SSpan) element), reList);
					reList.clear();
					reList.add(STYPE_NAME.STEXTUAL_RELATION);
					StringBuilder text = new StringBuilder();
					for (SToken token : tokens) {
						STextualDS ds = token.getSDocumentGraph().getSTextualDSs().get(0);
						SDataSourceSequence sequence = token.getSDocumentGraph().getOverlappedDSSequences(token, reList).get(0);
						text.append(ds.getSText().substring(sequence.getSStart(), sequence.getSEnd()) + " ");
					}
					return text.toString();
				}
				return element;
			}

			@Override
			protected TextCellEditor getCellEditor(Object element) {
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Reference;
			}
		});

		// To set width of the column to tree width
		treeViewer.getControl().addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				column.getColumn().setWidth(
						((Tree) e.getSource()).getBounds().width);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof IFileEditorInput)) {
			throw new RuntimeException("Wrong input");
		}
		setSite(site);
		setInput(input);
	}

	private StringBuilder buildSaltProjectInfo(IFile file) {
		final String nl = "\n";
		StringBuilder contents = new StringBuilder();
		SaltProject project = SaltFactory.eINSTANCE.createSaltProject();
		project.loadSaltProject(URI.createFileURI(file.getRawLocation().makeAbsolute().toOSString()));
		contents.append("Information about SaltProject " + project.getSName() + nl);
		int headlineLength = contents.length();
		for (int i = 1; i < headlineLength; i++) {
			contents.append("=");
		}
		contents.append(nl + nl + "Name: " + project.getSName());
		contents.append(nl + "Corpora: " + project.getSCorpusGraphs().get(0).getSCorpora().size() + nl + nl);
		EList<SDocument> sDocs = project.getSCorpusGraphs().get(0).getSDocuments();
		String sDocsString = nl + "| Documents (" + sDocs.size() + ") |";
		for (int j = 1; j < sDocsString.length(); j++)
			contents.append("-");
		contents.append(sDocsString + nl);
		for (int j = 1; j < sDocsString.length(); j++)
			contents.append("-");
		for (SDocument sDoc : sDocs) {
			SDocumentGraph graph = sDoc.getSDocumentGraph();
			int ind = ECollections.indexOf(sDocs, sDoc, 0);
			contents.append(nl + "(" + (ind + 1) + ") "+ sDoc.getSName());
			contents.append(nl + "Tokens: " + graph.getSTokens().size());
			contents.append(nl + "Nodes: " + graph.getSNodes().size());
		}
		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}

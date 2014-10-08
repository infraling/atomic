/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Graph;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd.ReferenceViewDropListener;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;
import de.uni_jena.iaa.linktype.atomic.editors.utils.GraphResolver;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceView extends ViewPart {

	private SDocumentGraph graph;
	private URI projectURI;
	private URI graphURI;
	private SDocument document;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(parent);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new ReferenceTreeContentProvider(
				(ReferenceModel) treeViewer.getInput()));
		treeViewer.setLabelProvider(new ReferenceTreeLabelProvider());
		graph = resolveGraph();
		ReferenceModel model = new ReferenceModel(getGraph());
		treeViewer.setInput(model);
		treeViewer.addDropSupport(operations, transferTypes,
				new ReferenceViewDropListener(treeViewer));
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

	private SDocumentGraph resolveGraph() {
		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFile file = null;
		if (editor instanceof CoreferenceEditor) {
			CoreferenceEditor corefEditor = (CoreferenceEditor) editor;
			IFileEditorInput input = (IFileEditorInput) corefEditor
					.getEditorInput();
			file = input.getFile();
			if (file.getName().endsWith(SaltFactory.FILE_ENDING_SALT)
					&& !(file.getName().equals(SaltFactory.FILE_SALT_PROJECT))) {
				GraphResolver graphResolver = new GraphResolver(file);
				projectURI = graphResolver.getProjectURI();
				graphURI = graphResolver.getGraphURI();
				SaltProject saltProject = SaltFactory.eINSTANCE
						.createSaltProject();
				saltProject.loadSCorpusStructure(projectURI);
				for (SDocument sDocument : saltProject.getSCorpusGraphs()
						.get(0).getSDocuments()) {
					if (sDocument.getSDocumentGraphLocation() == (graphURI)) {
						sDocument.loadSDocumentGraph();
						SDocumentGraph sDocumentGraph = sDocument
								.getSDocumentGraph();
						document = sDocument;
						return sDocumentGraph;
					}
				}
			}
		}
		return null;
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
	 * @param graph
	 *            the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

	/**
	 * @author Stephan Druskat
	 * 
	 */
	public class TreeLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof Reference) {
				return ((Reference) element).getName();
			}
			return element.toString();
		}
	}

}

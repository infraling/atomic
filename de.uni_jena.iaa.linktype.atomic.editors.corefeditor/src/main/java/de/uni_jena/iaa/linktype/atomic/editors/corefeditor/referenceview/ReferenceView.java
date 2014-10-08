/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd.ReferenceViewDropListener;
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(parent);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
	    treeViewer.setContentProvider(new ReferenceTreeContentProvider((ReferenceModel) treeViewer.getInput()));
	    treeViewer.setLabelProvider(new ReferenceTreeLabelProvider());
	    graph = resolveGraph();
	    ReferenceModel model = new ReferenceModel(getGraph());
	    treeViewer.setInput(model);
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

	private SDocumentGraph resolveGraph() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFile file = null;
		if (editor instanceof CoreferenceEditor) {
			CoreferenceEditor corefEditor = (CoreferenceEditor) editor;
			IFileEditorInput input = (IFileEditorInput) corefEditor.getEditorInput();
			file = input.getFile();
			if (file.getName().endsWith(SaltFactory.FILE_ENDING_SALT) && !(file.getName().equals(SaltFactory.FILE_SALT_PROJECT))) {
				GraphResolver graphResolver = new GraphResolver(file);
				projectURI = graphResolver.getProjectURI();
				graphURI = graphResolver.getGraphURI();
				SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
				saltProject.loadSCorpusStructure(projectURI);
				for (SDocument sDocument : saltProject.getSCorpusGraphs().get(0).getSDocuments()) {
					if (sDocument.getSDocumentGraphLocation() == (graphURI)) {
						sDocument.loadSDocumentGraph();
						SDocumentGraph sDocumentGraph = sDocument.getSDocumentGraph();
						document = sDocument;
						return sDocumentGraph;
					}
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
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
	
	// FIXME: DELETE ##############################################################
	
	// FIXME: DELETE ##############################################################

}

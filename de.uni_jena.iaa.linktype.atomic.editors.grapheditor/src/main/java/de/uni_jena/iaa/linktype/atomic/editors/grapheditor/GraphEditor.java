/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.io.File;
import java.util.EventObject;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicGraphicalViewerKeyHandler;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditor extends GraphicalEditorWithFlyoutPalette {

	private boolean isModelGraph = false; // Used for instance-testing for setContents()
	private HashSet<SToken> tokenMap; // For use as model for sub-graph editing
	private SDocumentGraph graph; // For use as model for graph editing
	private URI projectURI, graphURI;
	private SDocument document;

	/**
	 * 
	 */
	public GraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
	
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new AtomicEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new AtomicGraphicalViewerKeyHandler(viewer));
	}
	
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		if (isModelGraph) {
			getGraphicalViewer().setContents(graph);
		}
		else {
			getGraphicalViewer().setContents(tokenMap);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		document.setSDocumentGraph(graph);
		document.saveSDocumentGraph(graphURI);
		getCommandStack().markSaveLocation();
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) input).getFile();
			if (file.getName().equals(SaltFactory.FILE_SALT_PROJECT)) {
				// TODO Handle this
			}
			else if (file.getName().endsWith(SaltFactory.FILE_ENDING_SALT) && !(file.getName().equals(SaltFactory.FILE_SALT_PROJECT))) {
				GraphResolver graphResolver = new GraphResolver(file);
				projectURI = graphResolver.getProjectURI();
				graphURI = graphResolver.getGraphURI();
				SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
				saltProject.loadSCorpusStructure(projectURI);
				for (SDocument sDocument : saltProject.getSCorpusGraphs().get(0).getSDocuments()) {
					if (sDocument.getSDocumentGraphLocation() == (graphURI)) {
						sDocument.loadSDocumentGraph();
						graph = sDocument.getSDocumentGraph();
						document = sDocument;
					}
				}
				isModelGraph = true;
			}
		}
		else /*if (input instanceof AtomicSubGraphInput) */{ // TODO to be defined in Overview Editor
			
		}
	}
	
	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class GraphResolver {

		private final Shell shell = Display.getCurrent().getActiveShell();
		private IFile file;
		private URI saltProjectURI, sDocumentGraphURI;
		private SaltFactory sf = SaltFactory.eINSTANCE;

		public GraphResolver(IFile file) {
			this.file = file;
			saltProjectURI = determineSaltProjectURI();
			sDocumentGraphURI = determineSDocumentGraphURI();
		}

		/**
		 * @param file
		 * @return saltProject
		 */
		private URI determineSaltProjectURI() {
			URI localSaltProjectURI;
			if (file.getProject().getFile(SaltFactory.FILE_SALT_PROJECT).exists()) {
				localSaltProjectURI = URI.createFileURI(new File(file.getProject().getFile(SaltFactory.FILE_SALT_PROJECT).getLocation().toString()).getAbsolutePath());
			}
			else { // I.e., graph is an "orphan" (not attached to a project)
				localSaltProjectURI = URI.createFileURI(new File(file.getProject().getLocation().toString()).getAbsolutePath());
				String documentName = file.getName().substring(0, file.getName().length() - 5);
				String corpusName = file.getParent().getName();
				SaltProject temporaryProject = sf.createSaltProject();
				createSimpleCorpusStructure(temporaryProject, corpusName, documentName);
				temporaryProject.saveSaltProject(localSaltProjectURI);
				MessageDialog.openInformation(shell, "Salt project file created!", "The corpus document you are opening is an orphan, i.e., is not associated with an existing Salt project.\nTherefore, a new Salt project has been created, and the document has been attached to it.");
				try {
					file.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					MessageDialog.openError(shell, "Refresh Error!", "Auto-refreshing the Navigation View did not work. Please refresh manually by pressing F5 in the Navigation View, or \"Refresh\" in the context manu of the Navigation View.");
					e.printStackTrace();
				}
			}
			return localSaltProjectURI;
		}

		private URI determineSDocumentGraphURI() {
			URI localSDocumentGraphURI = URI.createFileURI(new File(file.getLocation().toString()).getAbsolutePath());
			if (saltProjectURI != null) {
				SaltProject localSaltProject = sf.createSaltProject();
				localSaltProject.loadSCorpusStructure(saltProjectURI);
				if (localSaltProject.getSDocumentGraphLocations().containsValue(localSDocumentGraphURI)) {
					return localSDocumentGraphURI;
				}
				else {
					MessageDialog.openError(shell, "Graph Resolution Error", "No document graph with the URI " + localSDocumentGraphURI + " is registered in the project.");
					return null;
				}
			}
			return null;
		}

		private void createSimpleCorpusStructure(SaltProject saltProject, String corpusName, String documentName) {
			saltProject.setSName("Generic Salt project for " + documentName);
			SaltFactory sf = SaltFactory.eINSTANCE;
			SCorpusGraph corpusGraph = sf.createSCorpusGraph();
			saltProject.getSCorpusGraphs().add(corpusGraph);
			SCorpus corpus = sf.createSCorpus();
			corpus.setSName(corpusName);
			corpusGraph.addSNode(corpus);
			SDocument document = sf.createSDocument();
			document.setSName(documentName);
			SDocumentGraph protograph = sf.loadSDocumentGraph(URI.createFileURI(new File(file.getLocation().toString()).getAbsolutePath()));
			document.setSDocumentGraph(protograph);
			corpusGraph.addSDocument(corpus, document);
		}

		public URI getGraphURI() {
			return sDocumentGraphURI;
		}

		public URI getProjectURI() {
			return saltProjectURI;
		}
	}

}

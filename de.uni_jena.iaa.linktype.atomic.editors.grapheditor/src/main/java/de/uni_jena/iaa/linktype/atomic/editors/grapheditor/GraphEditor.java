/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.io.File;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelLoader;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.GraphEditorPaletteFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicGraphicalViewerKeyHandler;

/**
 * @author Stephan Druskat
 * 
 */
public class GraphEditor extends GraphicalEditorWithFlyoutPalette { // FIXME: CLean up this mess!

	private static final Logger log = LoggerFactory.getLogger(GraphEditor.class);

//	private boolean isModelGraph = false; // Used for instance-testing for
											// setContents()
//	private HashSet<SToken> tokenMap; // For use as model for sub-graph editing
	private SDocumentGraph graph; // For use as model for graph editing
//	private URI projectURI, graphURI;

	/**
	 * 
	 */
	public GraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
		getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
	}

	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new AtomicEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new AtomicGraphicalViewerKeyHandler(viewer));
		getGraphicalViewer().addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));
		getEditDomain().getPaletteViewer().addDragSourceListener(new TemplateTransferDragSourceListener(getEditDomain().getPaletteViewer()));
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
		ZoomManager zoomManager = root.getZoomManager();
		root.getZoomManager().setZoomLevels(new double[] { 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 3.0, 4.0 });
		List<String> zoomContributions = Arrays.asList(new String[] { ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH });
		zoomManager.setZoomLevelContributions(zoomContributions);
		zoomManager.setZoomAnimationStyle(ZoomManager.ANIMATE_ZOOM_IN_OUT);
		IAction zoomIn = new ZoomInAction(zoomManager);
		IAction zoomOut = new ZoomOutAction(zoomManager);
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}

	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		if (getEditorInput() instanceof FileEditorInput) {
			graph = ModelRegistry.getModel(((FileEditorInput) getEditorInput()).getFile());
			getGraphicalViewer().setContents(graph);
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Action not applicable", "The Annotation Graph Editor cannot process the input you have selected.");
		}
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart();
		ConnectionLayer connLayer = (ConnectionLayer) root.getLayer(LayerConstants.CONNECTION_LAYER);
		GraphicalEditPart contentEditPart = (GraphicalEditPart) root.getContents();
		FanRouter fanRouter = new FanRouter();
		fanRouter.setSeparation(30);
		AutomaticRouter router = fanRouter;
		ShortestPathConnectionRouter shortestPathConnectionRouter = new ShortestPathConnectionRouter(contentEditPart.getFigure());
		shortestPathConnectionRouter.setSpacing(15);
		router.setNextRouter(shortestPathConnectionRouter);
		connLayer.setConnectionRouter(router);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot
	 * ()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return GraphEditorPaletteFactory.createPalette();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile documentIFile = ((FileEditorInput) getEditorInput()).getFile();
		SDocument document = ModelLoader.getSDocumentFromSDocumentIFile(((FileEditorInput) getEditorInput()).getFile());
		document.setSDocumentGraph(graph);
		URI uri = URI.createFileURI(new File(documentIFile.getLocation().toString()).getAbsolutePath());
		document.saveSDocumentGraph(uri);
		getCommandStack().markSaveLocation();
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

//	/**
//	 * @author Stephan Druskat
//	 * 
//	 */
//	public class GraphResolver {
//
//		private final Shell shell = Display.getCurrent().getActiveShell();
//		private IFile file;
//		private URI saltProjectURI, sDocumentGraphURI;
//		private SaltFactory sf = SaltFactory.eINSTANCE;
//
//		public GraphResolver(IFile file) {
//			this.file = file;
//			saltProjectURI = determineSaltProjectURI();
//			sDocumentGraphURI = determineSDocumentGraphURI();
//		}
//
//		/**
//		 * @param file
//		 * @return saltProject
//		 */
//		private URI determineSaltProjectURI() {
//			URI localSaltProjectURI;
//			if (file.getProject().getFile(SaltFactory.FILE_SALT_PROJECT).exists()) {
//				localSaltProjectURI = URI.createFileURI(new File(file.getProject().getFile(SaltFactory.FILE_SALT_PROJECT).getLocation().toString()).getAbsolutePath());
//			}
//			else { // I.e., graph is an "orphan" (not attached to a project)
//				localSaltProjectURI = URI.createFileURI(new File(file.getProject().getLocation().toString()).getAbsolutePath());
//				String documentName = file.getName().substring(0, file.getName().length() - 5);
//				String corpusName = file.getParent().getName();
//				SaltProject temporaryProject = sf.createSaltProject();
//				createSimpleCorpusStructure(temporaryProject, corpusName, documentName);
//				temporaryProject.saveSaltProject(localSaltProjectURI);
//				MessageDialog.openInformation(shell, "Salt project file created!", "The corpus document you are opening is an orphan, i.e., is not associated with an existing Salt project.\nTherefore, a new Salt project has been created, and the document has been attached to it.");
//				try {
//					file.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
//				}
//				catch (CoreException e) {
//					MessageDialog.openError(shell, "Refresh Error!", "Auto-refreshing the Navigation View did not work. Please refresh manually by pressing F5 in the Navigation View, or \"Refresh\" in the context manu of the Navigation View.");
//					e.printStackTrace();
//				}
//			}
//			return localSaltProjectURI;
//		}
//
//		private URI determineSDocumentGraphURI() {
//			URI localSDocumentGraphURI = URI.createFileURI(new File(file.getLocation().toString()).getAbsolutePath());
//			if (saltProjectURI != null) {
//				SaltProject localSaltProject = sf.createSaltProject();
//				localSaltProject.loadSCorpusStructure(saltProjectURI);
//				if (localSaltProject.getSDocumentGraphLocations().containsValue(localSDocumentGraphURI)) {
//					return localSDocumentGraphURI;
//				}
//				else {
//					MessageDialog.openError(shell, "Graph Resolution Error", "No document graph with the URI " + localSDocumentGraphURI + " is registered in the project.");
//					return null;
//				}
//			}
//			return null;
//		}
//
//		private void createSimpleCorpusStructure(SaltProject saltProject, String corpusName, String documentName) {
//			saltProject.setSName("Generic Salt project for " + documentName);
//			SaltFactory sf = SaltFactory.eINSTANCE;
//			SCorpusGraph corpusGraph = sf.createSCorpusGraph();
//			saltProject.getSCorpusGraphs().add(corpusGraph);
//			SCorpus corpus = sf.createSCorpus();
//			corpus.setSName(corpusName);
//			corpusGraph.addSNode(corpus);
//			SDocument document = sf.createSDocument();
//			document.setSName(documentName);
//			SDocumentGraph protograph = sf.loadSDocumentGraph(URI.createFileURI(new File(file.getLocation().toString()).getAbsolutePath()));
//			document.setSDocumentGraph(protograph);
//			corpusGraph.addSDocument(corpus, document);
//		}
//
//		public URI getGraphURI() {
//			return sDocumentGraphURI;
//		}
//
//		public URI getProjectURI() {
//			return saltProjectURI;
//		}
//	}

	public EditPartViewer getEditPartViewer() {
		return getGraphicalViewer();
	}

	public DefaultEditDomain getDomain() {
		return getEditDomain();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (getEditorInput() instanceof FileEditorInput) {
			ModelRegistry.deregisterEditor(((FileEditorInput) getEditorInput()).getFile());
		}
	}

}

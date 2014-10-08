/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.io.File;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.GraphEditorPaletteFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicGraphicalViewerKeyHandler;
import de.uni_jena.iaa.linktype.atomic.editors.utils.GraphResolver;

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
		root.getZoomManager().setZoomLevels(new double[] { 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0 , 3.0, 4.0});
		List<String> zoomContributions = Arrays.asList(new String[] {ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH });
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
		IEditorInput input = getEditorInput();
		IFile file = ((FileEditorInput) input ).getFile();
		if (file.getName().equals(SaltFactory.FILE_SALT_PROJECT)) {
			SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
			saltProject.loadSaltProject(URI.createFileURI(new File(file.getLocation().toString()).getAbsolutePath()));
			String name = saltProject.getSName();
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Action not applicable", "Cannot open project file for editing.\nPlease open a document file.\n\n"
					+ "Project information for \"" + name + "\"\n"
					+ "No. of contained documents: " + saltProject.getSCorpusGraphs().get(0).getSDocuments().size());
			return;
		}
		if (isModelGraph) {
			getGraphicalViewer().setContents(graph);
		}
		else {
			getGraphicalViewer().setContents(tokenMap);
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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return GraphEditorPaletteFactory.createPalette();
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
			if (file.getName().endsWith(SaltFactory.FILE_ENDING_SALT) && !(file.getName().equals(SaltFactory.FILE_SALT_PROJECT))) {
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
	
	public EditPartViewer getEditPartViewer() {
		return getGraphicalViewer();
	}

	public DefaultEditDomain getDomain() {
		return getEditDomain();
	}

}

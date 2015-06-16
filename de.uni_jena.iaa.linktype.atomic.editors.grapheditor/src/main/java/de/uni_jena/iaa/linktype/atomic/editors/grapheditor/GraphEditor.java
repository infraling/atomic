/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphService;
import de.uni_jena.iaa.linktype.atomic.core.editors.AtomicGraphicalEditor;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.GraphEditorPaletteFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.SpanPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.StructurePart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.TokenPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AdHocSentenceDetectionWizard;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicGraphicalViewerKeyHandler;
import de.uni_jena.iaa.linktype.atomic.views.layerview.util.NewLayer;

/**
 * @author Stephan Druskat
 * 
 */
public class GraphEditor extends AtomicGraphicalEditor {

	IPartListener2 partListener = new IPartListener2() {

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			try {
				PlatformUI.getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.editors.grapheditor.perspective", PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
			catch (WorkbenchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			if (partRef.getPart(false).getClass() == GraphEditor.class) {
				try {
					getSite().getWorkbenchWindow().getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.core.perspective", getSite().getWorkbenchWindow());
				}
				catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			GraphPart part = (GraphPart) getGraphicalViewer().getRootEditPart().getContents();
		}
	};

	ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection incomingSelection) {
			long startTime = System.nanoTime();
			if (!(incomingSelection instanceof IStructuredSelection)) {
				return;
			}
			IStructuredSelection selection = (IStructuredSelection) incomingSelection;
			for (Object element : selection.toList()) {
				// Check if we need to perform an operation at all, i.e.
				// if the selection is interesting
				if (!(element instanceof SSpan || element instanceof SLayer || (element instanceof String && (element.equals(ModelRegistry.NO_LAYERS_SELECTED) || element.equals(ModelRegistry.NO_SENTENCES_SELECTED))) || element instanceof NewLayer || getLayerNames().contains(element))) {
					return;
				}
			}
			if (selection.toArray().length == 0) {
				// A selection of type <empty selection>
				return;
			}
			if (selection.toArray()[0] instanceof NewLayer) {
				// New layer
			}
			boolean containsOnlySpans = true;
			boolean containsOnlyLayers = true;
			for (Object element : selection.toList()) {
				if (!(element instanceof SSpan)) {
					containsOnlySpans = false;
					break;
				}
			}
			for (Object element : selection.toList()) {
				if (!(element instanceof String)) {
					if (!(getLayerNames().contains(element))) {
						containsOnlyLayers = false;
						break;
					}
				}
			}
			GraphPart graphPart = ((GraphPart) getGraphicalViewer().getRootEditPart().getContents());
			if (selection.isEmpty()) {
				graphPart.getLayers().clear();
				graphPart.getSortedTokens().clear();
				graphPart.refresh();
			}
			else if (selection.getFirstElement().equals(ModelRegistry.NO_LAYERS_SELECTED)) {
				graphPart.getLayers().clear();
				getGraphicalViewer().getRootEditPart().getContents().refresh();
			}
			else if (selection.getFirstElement().equals(ModelRegistry.NO_SENTENCES_SELECTED)) {
				graphPart.getSortedTokens().clear();
				getGraphicalViewer().getRootEditPart().getContents().refresh();
			}
			else if (selection.toList().get(0) instanceof NewLayer) {
				graphPart.setActiveLayer(((NewLayer) selection.getFirstElement()).getNewLayer());
			}
			else if (containsOnlySpans) {
				graphPart.getSortedTokens().clear();
				graphPart.setSortedTokens(GraphService.getOrderedTokensForSentenceSpans(selection.toList()));
				graphPart.refresh();
				for (Object child : graphPart.getChildren()) {
					if (child instanceof TokenPart || child instanceof SpanPart || child instanceof StructurePart) {
						((AbstractGraphicalEditPart) child).refresh();
					}
				}
			}
			else if (containsOnlyLayers) {
				graphPart.getLayers().clear();
				graphPart.setLayers(new HashSet<String>(selection.toList()));
				// TODO: Next refresh is needed!
				try {
					new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Please wait while the annotation graph is rendered.", IProgressMonitor.UNKNOWN);
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									getGraphicalViewer().getRootEditPart().getContents().refresh();
								}
							});
							monitor.done();
						}
					});
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	};
	private GraphicalViewer viewer;

	private Set<String> layerNames;

	/**
	 * 
	 */
	public GraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
		getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().getPage().addSelectionListener(listener);
		getSite().getPage().addPartListener(partListener);
		// try {
		// PlatformUI.getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.editors.grapheditor.perspective",
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		// }
		// catch (WorkbenchException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		setViewer(getGraphicalViewer());
		getViewer().setEditPartFactory(new AtomicEditPartFactory());
		getViewer().setRootEditPart(new ScalableFreeformRootEditPart());
		getViewer().setKeyHandler(new AtomicGraphicalViewerKeyHandler(getViewer()));
		getGraphicalViewer().addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));
		getEditDomain().getPaletteViewer().addDragSourceListener(new TemplateTransferDragSourceListener(getEditDomain().getPaletteViewer()));
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getViewer().getRootEditPart();
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

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		SLayer sentenceLayer = getGraph().getSLayer(ModelRegistry.SENTENCE_LAYER_SID);
		if (sentenceLayer == null || sentenceLayer.getNodes().isEmpty()) {
			WizardDialog adHocSentenceDetectionsWizard = new WizardDialog(Display.getCurrent().getActiveShell(), new AdHocSentenceDetectionWizard(getGraph()));
			adHocSentenceDetectionsWizard.open();
			// Save document in case layers have changed
			doSave(null);
			// Refresh all tokens (notify them them so they will refresh
			// themselves,
			// as newly added sentence spans' relations will otherwise point
			// into nirvana.
			for (SToken token : getGraph().getSTokens()) {
				token.eNotify(new NotificationImpl(Notification.SET, false, true));
			}
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
		setLayerNames(populateLayerNames());
	}

	/**
	 * @return
	 */
	private Set<String> populateLayerNames() {
		Set<String> layerNames = new HashSet<String>();
		for (SLayer layer : getGraph().getSLayers()) {
			layerNames.add(layer.getSName());
		}
		layerNames.add("\u269B NO ASSIGNED LEVEL \u269B");
		return layerNames;
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
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
	 * .EventObject)
	 */
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

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(listener);
		getSite().getPage().removePartListener(partListener);
	}

	/**
	 * @return the viewer
	 */
	public GraphicalViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 *            the viewer to set
	 */
	public void setViewer(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return the layerNames
	 */
	public Set<String> getLayerNames() {
		return layerNames;
	}

	/**
	 * @param layerNames
	 *            the layerNames to set
	 */
	public void setLayerNames(Set<String> layerNames) {
		this.layerNames = layerNames;
	}

	class RefreshEditorJob extends Job {
		public RefreshEditorJob() {
			super("title");

			setUser(true);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					getGraphicalViewer().getRootEditPart().getContents().refresh();

				}
			});
			// ... your code

			return Status.OK_STATUS;
		}
	}

}

/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.console.AtomicALConsole;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SaltEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.handlers.CustomGraphicalViewerKeyHandler;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.AtomicEditorPaletteFactory;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicSaltEditor extends GraphicalEditorWithFlyoutPalette {
	
	private SDocumentGraph graph;
	private IFile file;
	private SaltProject project;

	/**
	 * 
	 */
	public AtomicSaltEditor() {
		setEditDomain(new DefaultEditDomain(this));
		getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
	}
	
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class)
			return getGraphicalViewer().getProperty(ZoomManager.class.toString());
		return super.getAdapter(type);
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		// TODO: Look into resource-based loading, and replace if necessary.
		// Note: SaltProject.loadSaltProject may already implement resource-based loading...
		ModelLoader modelLoader = new ModelLoader(input);
		setFile(modelLoader.getResolvedIFile());
		if (getFile().getName().equalsIgnoreCase("saltproject.salt")) {
			// TODO Display project info in MessageDialog & wait until editor is open, then close it
			return;
		}
		Assert.isTrue(modelLoader.isProjectResolved());
		setProject(modelLoader.getResolvedProject());
		Assert.isTrue(modelLoader.isGraphResolved());
		setGraph(modelLoader.getResolvedGraph());
		String oldPartName = getPartName();
		String newPartName = oldPartName + " - " + getFile().getFullPath().toString();
		setPartName(newPartName);
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IConsoleView consoleView = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
		IOConsole myConsole = findConsole("AtomicAL Console");
		IOConsoleOutputStream out = myConsole.newOutputStream();
		out.setColor(new Color(null, 255, 0, 0));
		out.setActivateOnWrite(true);
		try {
			out.write("AtomicAL Console\nFor help with available commands, type \"--help\".\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private AtomicALConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName())) {
	            return (AtomicALConsole) existing[i];
	         }
	      // No console found, so create a new one
	      AtomicALConsole myConsole = new AtomicALConsole("AtomicAL Console", null, null, "UTF-8", true, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		if (getGraph() != null)
			viewer.setContents(getGraph());
		else {
			// FIXME Abort (close the EditorPart!)
			return;
		}
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
		ConnectionLayer connLayer = (ConnectionLayer) root.getLayer(LayerConstants.CONNECTION_LAYER);
		GraphicalEditPart contentEditPart = (GraphicalEditPart) root.getContents();
		ShortestPathConnectionRouter shortestPathConnectionRouter = new ShortestPathConnectionRouter(contentEditPart.getFigure());
		connLayer.setConnectionRouter(shortestPathConnectionRouter);
	}
	
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new SaltEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new CustomGraphicalViewerKeyHandler(viewer));
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
		// FIXME: Are these helpful?
		List<String> zoomContributions = Arrays.asList(new String[] { 
			ZoomManager.FIT_ALL, 
			ZoomManager.FIT_HEIGHT, 
			ZoomManager.FIT_WIDTH });
		rootEditPart.getZoomManager().setZoomLevelContributions(zoomContributions);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return AtomicEditorPaletteFactory.createPalette();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// FIXME: Check the output of SDocumentGraphAdapter's notifyChanged() when this is called!
		// SDocument might be set to null!
		BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
			@Override
		    public void run() {
				if (getProject() != null)
				getProject().saveSaltProject(URI.createFileURI(new File(getFile().getProject().getLocation().toString()).getAbsolutePath()));
				getCommandStack().markSaveLocation();
		    }
		});
	}
	
	/**
	* Fire a {@link IEditorPart#PROP_DIRTY} property change and
	* call super implementation.
	*/
	@Override 
	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
		super.commandStackChanged(event);
	} 
	
	public DefaultEditDomain getDomain() {
		return this.getEditDomain();
	}

	public EditPartViewer getEditPartViewer() {
		return this.getGraphicalViewer();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			conMan.removeConsoles(existing);
	}

	/**
	 * @return the file
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(IFile file) {
		this.file = file;
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

	/**
	 * @return the project
	 */
	public SaltProject getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(SaltProject project) {
		this.project = project;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class ModelLoader {

		private IFile resolvedIFile = null;
		private SaltProject resolvedProject = null;
		private SDocumentGraph resolvedGraph = null;
		private boolean isIFileResolved = false;
		private boolean isProjectResolved = false;
		private boolean isGraphResolved = false;
		private URI projectURI = null;
		
		public ModelLoader(IEditorInput input) {
			setResolvedIFile(getIFileFromInput(input));
			setResolvedProject(resolveProject());
			setResolvedGraph(resolveGraph());
		}

		private SDocumentGraph resolveGraph() {
			SDocumentGraph graph = null;
			if (getProjectURI() != null) {
				getResolvedProject().loadSCorpusStructure(getProjectURI());
				System.out.println("PROJ " + getResolvedProject().getSCorpusGraphs().get(0).getSDocuments());
			}
			else {
				// This is being caught later and an error message displayed, so just return null here
				return null;
			}
			URI graphURI = URI.createFileURI(new File(getResolvedIFile().getLocation().toString()).getAbsolutePath());
//			if (!isGraphOrphan()) {
				if (getResolvedProject().getSDocumentGraphLocations().containsValue(graphURI)) {
					for (SDocument document : getResolvedProject().getSCorpusGraphs().get(0).getSDocuments()) {
						if (document.getSDocumentGraphLocation().equals(graphURI)) {
							graph = document.getSDocumentGraph();
						}
					}
				}
//			}
			else {
//				getResolvedProject().loadSCorpusStructure(projectURI);
				if (getResolvedProject().getSDocumentGraphLocations().containsValue(graphURI)) {
					for (SDocument document : getResolvedProject().getSCorpusGraphs().get(0).getSDocuments()) {
						if (document.getSDocumentGraphLocation().equals(graphURI)) {
							document.loadSDocumentGraph(graphURI);
							graph = document.getSDocumentGraph();
						}
					}
				}
			}
			if (graph != null && graph.getSDocument() != null && graph.getSDocument().getSCorpusGraph().getSaltProject() != null)
				setGraphResolved(true);
			return graph;
		}

		private SaltProject resolveProject() {
			SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
			if (getResolvedIFile().getName().equalsIgnoreCase("saltproject.salt")) {
				saltProject.loadSaltProject(URI.createFileURI(new File(getResolvedIFile().getLocation().toString()).getAbsolutePath()));
				String name = saltProject.getSName();
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Action not applicable", "Cannot open project file for editing.\nPlease open a document file.\n\n"
						+ "Project information\n"
						+ "No. of contained documents: " + saltProject.getSCorpusGraphs().get(0).getSDocuments().size());
				return null;
			}
			else {
				// Check if we have a project file at all, i.e., if graph is orphaned
				// Case: saltProject.salt exists in project
				if (getResolvedIFile().getProject().getFile("saltProject.salt").exists()) {
					File projectFile = new File(getResolvedIFile().getProject().getFile("saltProject.salt").getLocation().toString());
					setProjectURI(URI.createFileURI(projectFile.getAbsolutePath()));
				}
				// Case: saltProject.salt doesn't exist in project
				else {
					URI saveURI = URI.createFileURI(new File(getResolvedIFile().getProject().getLocation().toString()).getAbsolutePath());
					// documentName = name of document file sans ".salt"
					String documentNameWithFileEnding = getResolvedIFile().getName();
					// substring of above: 0 - length-5 (".salt")
					String documentName = documentNameWithFileEnding.substring(0, documentNameWithFileEnding.length() - 5);
					// corpusName = name of folder that contains document
					String corpusName = getResolvedIFile().getParent().getName();
					createSimpleCorpusStructure(saltProject, corpusName, documentName);
					saltProject.saveSaltProject(saveURI);
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Salt project file created!", "The corpus document you are opening is an orphan, "
							+ "i.e., is not associated with an existing Salt project.\n\n"
							+ "Therefore, a new Salt project has been created,"
							+ "and the document has been attached to it.");
					setProjectURI(URI.createFileURI(new File(getResolvedIFile().getProject().getFile("saltProject.salt").getLocation().toString()).getAbsolutePath()));
					try {
						getResolvedIFile().getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// Resolve corpus structure
			saltProject.loadSCorpusStructure(getProjectURI());
			if (saltProject.getSCorpusGraphs().get(0).getSDocuments() != null) {
				setProjectResolved(true);
				setResolvedProject(saltProject);
			}
			return saltProject;
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
			SDocumentGraph protograph = sf.loadSDocumentGraph(URI.createFileURI(new File(getResolvedIFile().getLocation().toString()).getAbsolutePath()));
			document.setSDocumentGraph(protograph);
			corpusGraph.addSDocument(corpus, document);
		}

		public IFile getResolvedIFile() {
			return resolvedIFile;
		}

		public SDocumentGraph getResolvedGraph() {
			return resolvedGraph;
		}

		public SaltProject getResolvedProject() {
			return resolvedProject;
		}

		private IFile getIFileFromInput(IEditorInput input) {
			IFile iFile = null;
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileEditorInput = (IFileEditorInput) input;
				iFile = fileEditorInput.getFile();
			}
			else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Wrong input!", "Input is not of type FileEditorInput.\nPlease report this on the Atomic User mailing list.");
			}
			if (iFile != null)
				setIFileResolved(true);
			return iFile;
		}
		
		public boolean isIFileResolved() {
			return isIFileResolved;
		}

		public boolean isProjectResolved() {
			return isProjectResolved;
		}

		public boolean isGraphResolved() {
			return isGraphResolved;
		}

		/**
		 * @param resolvedProject the resolvedProject to set
		 */
		public void setResolvedProject(SaltProject resolvedProject) {
			this.resolvedProject = resolvedProject;
		}

		/**
		 * @param resolvedGraph the resolvedGraph to set
		 */
		public void setResolvedGraph(SDocumentGraph resolvedGraph) {
			this.resolvedGraph = resolvedGraph;
		}

		/**
		 * @param resolvedIFile the resolvedIFile to set
		 */
		public void setResolvedIFile(IFile resolvedIFile) {
			this.resolvedIFile = resolvedIFile;
		}

		/**
		 * @param isIFileResolved the isIFileResolved to set
		 */
		public void setIFileResolved(boolean isIFileResolved) {
			this.isIFileResolved = isIFileResolved;
		}

		/**
		 * @param isProjectResolved the isProjectResolved to set
		 */
		public void setProjectResolved(boolean isProjectResolved) {
			this.isProjectResolved = isProjectResolved;
		}

		/**
		 * @param isGraphResolved the isGraphResolved to set
		 */
		public void setGraphResolved(boolean isGraphResolved) {
			this.isGraphResolved = isGraphResolved;
		}

		/**
		 * @return the projectURI
		 */
		public URI getProjectURI() {
			return projectURI;
		}

		/**
		 * @param projectURI the projectURI to set
		 */
		public void setProjectURI(URI projectURI) {
			this.projectURI = projectURI;
		}
		
	}


	
}

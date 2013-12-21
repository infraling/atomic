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

import java.io.IOException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
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

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.console.AtomicALConsole;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SaltEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.handlers.CustomGraphicalViewerKeyHandler;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.AtomicEditorPaletteFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.util.ModelLoader;

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
		file = ModelLoader.getIFileFromInput(input);
		graph = ModelLoader.loadSDocumentGraph(file);
		project = ModelLoader.getSaltProject();
		String oldPartName = getPartName();
		String newPartName = oldPartName + " - " + file.getFullPath().toString();
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
		viewer.setContents(graph);
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
				project.saveSaltProject(URI.createFileURI((file.getParent().getLocation().toFile()).getAbsolutePath()));
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
	
}

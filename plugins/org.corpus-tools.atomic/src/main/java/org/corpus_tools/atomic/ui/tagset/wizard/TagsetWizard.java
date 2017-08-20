/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.tagset.impl.TagsetFactory;
import org.corpus_tools.atomic.ui.tagset.editor.TagsetEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetWizard extends Wizard implements IWizard, INewWizard {
	
	private static final Logger log = LogManager.getLogger(TagsetWizard.class);
	
	private TagsetWizardPage page = new TagsetWizardPage();

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * // TODO Add description
	 * 
	 */
	@Override
	public void addPages() {
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IProject project = page.getProject();
		String fileName = TagsetFactory.getTagsetFileName(project.getName());
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject projectResource = workspace.getProject(project.getName());
		
		if (!project.isOpen())
			try {
				project.open(null);
			}
			catch (CoreException e1) {
				log.error("Could not open project {}.", project.getName(), e1);
			}
		IFile file = project.getFile(fileName);
		if (!file.exists()) {
		    byte[] bytes = "".getBytes();
		    InputStream source = new ByteArrayInputStream(bytes);
		    try {
				file.create(source, IResource.NONE, null);
				log.info("Wrote tagset file {} with size {}.", file.getName(), Files.size(Paths.get(file.getRawLocationURI())));
			}
			catch (CoreException | IOException e) {
				log.error("Could not create the tagset file for project {}.", project.getName(), e);
			}
		}
		try {
			projectResource.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			log.error("An error occurred while refreshing the workspace to show the new tagset file.", e);
		}
		IEditorInput editorInput = new FileEditorInput(file);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(editorInput, TagsetEditor.ID);
		}
		catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * // TODO Add description
	 * 
	 * @return
	 */
	@Override 
	public String getWindowTitle() {
		return "Tagset";
	}

}

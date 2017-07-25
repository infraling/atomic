/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.tagset.impl.TagsetFactory;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//		IFolder folder = project.getFolder("/");
		IFile file = project.getFile(fileName);
//		if (!folder.exists()) 
//		    folder.create(IResource.NONE, true, null);
		if (!file.exists()) {
		    byte[] bytes = "".getBytes();
		    InputStream source = new ByteArrayInputStream(bytes);
		    try {
				file.create(source, IResource.NONE, null);
			}
			catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			projectResource.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
//				try {
//				projectFile.create(null, true, null);
//				workspace.refreshLocal(IResource.DEPTH_INFINITE, null);
//			}
//			catch (CoreException e) {
//				log.error("An error occurred while writing the tagset file and refreshing the workspace!", e);
//			}
//			return true;
//		}
//		else {
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Tagset exists!", "Cannot create a new tagset file for project " + project.getName() + ", because a tagset file already exists for this project.");
//			return true;
//		}
	}
	
	@Override 
	public String getWindowTitle() {
		return "Tagset";
	}

}

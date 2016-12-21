/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.projects.wizard;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.pepper.AtomicPepperStarter;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.PepperModuleDesc;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.connectors.PepperConnector;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard for creating new Atomic (i.e., Salt) projects.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class NewAtomicProjectWizard extends Wizard implements INewWizard {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizard".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizard.class);
	
	private PepperConnector pepper = null;
	
	private NewAtomicProjectWizardPage page = new NewAtomicProjectWizardPage();
	
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

//	/* 
//	 * @copydoc @see org.eclipse.jface.wizard.Wizard#canFinish()
//	 */
//	@Override
//	public boolean canFinish() {
//		return page.getCorpus().getName() != null && page.getCorpus().getPath() != null;
//	}
	
	/*
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() { // FIXME: Add logs
		AtomicPepperStarter pepperStarter = new AtomicPepperStarter();
		pepperStarter.startPepper();
		this.pepper = pepperStarter.getPepper();
		log.trace("Procured an instance of {}: {}.", this.pepper.getClass().getName(), this.pepper.toString());
		String pepperJobId = pepper.createJob();
		PepperJob pepperJob = pepper.getJob(pepperJobId);
		log.trace("Procured an instance of {}: {}.", pepperJob.getClass().getName(), pepperJob.toString());
		
		// Create import step
		StepDesc importStepDesc = pepperJob.createStepDesc();
		importStepDesc.setModuleType(MODULE_TYPE.IMPORTER);
		String importPath = page.getCorpus().getPath();
		importStepDesc.getCorpusDesc().setCorpusPath(URI.createFileURI(importPath));
		log.trace("Procured an instance of {}: {}.", importStepDesc.getClass().getName(), importStepDesc.toString());
		PepperModuleDesc importer = null;
		for (PepperModuleDesc md : pepper.getRegisteredModules()) {
			if (md.getModuleType().equals(MODULE_TYPE.IMPORTER) && md.getName().equals("TextImporter")) {
				importer = md;
				break;
			}
		}
		importStepDesc.setName(importer.getName());
		pepperJob.addStepDesc(importStepDesc);
		log.info("Successfully set up an importer of type {} for the creation of a new Atomic project from path {}.", importer.getName(), importPath);
		
		// Set up Eclipse project
		String projectName = page.getCorpus().getName();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		log.trace("Workspace root is {}.", workspaceRoot.getLocation().toOSString());
		IProject iProject = workspaceRoot.getProject(projectName);
		log.trace("Created IProject OBJECT with name \"{}\" at workspace root.", iProject.getName());
		try {
			iProject.create(null);
			log.info("Successfully created Atomic project \"{}\".", iProject.getName());
			iProject.open(null);
			log.trace("Opened IProject \"{}\".", iProject.getName());
		}
		catch (CoreException e) {
			MessageDialog.openError(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), "Atomic project note created!", "A project by the name of \"" + projectName + "\" already exists.");
			log.error("Could not create and open the IProject for SaltProject {}!", projectName, e);
			return false;
		}
		File iProjectLocation = new File(iProject.getLocation().toString());

		// Create export step
		StepDesc exportStepDesc = pepperJob.createStepDesc();
		exportStepDesc.setModuleType(MODULE_TYPE.EXPORTER);
		String outputCanonicalPath;
		try {
			outputCanonicalPath = iProjectLocation.getCanonicalPath();
		}
		catch (IOException ex) {
			outputCanonicalPath = iProjectLocation.getAbsolutePath();
		}
		exportStepDesc.getCorpusDesc().setCorpusPath(URI.createFileURI(outputCanonicalPath));
		log.trace("Procured an instance of {}: {}.", importStepDesc.getClass().getName(), outputCanonicalPath);
		PepperModuleDesc exporter = null;
		for (PepperModuleDesc md : pepper.getRegisteredModules()) {
			if (md.getModuleType().equals(MODULE_TYPE.EXPORTER) && md.getName().equals("SaltXMLExporter")) {
				exporter = md;
				break;
			}
		}
		exportStepDesc.setName(exporter.getName());
		pepperJob.addStepDesc(exportStepDesc);
		log.info("Successfully set up an exporter of type {} for the creation of a new Atomic project to path {}.", importer.getName(), outputCanonicalPath);

		// Convert
		log.trace("Starting conversion with Pepper job {}.", pepperJob.toString());
		try {
			pepperJob.convert();
			SaltProject proj = pepperJob.getSaltProject();
			log.info("WOOT WOOT, GOT A PROJECT! {}", proj.toString());
		} catch (Exception e) {
			log.warn("EXCEPTIOOOOOOOOOOOOOON: ", e);
		} finally {
			pepper.removeJob(pepperJobId);
		}
		// FIXME CONVERT HERE
		// FIXME Check how this is reported in UI!
//		Job saltProjectCreationJob = new Job("Creating Atomic project ...") {
//			
//			@Override
//			  protected IStatus run(IProgressMonitor monitor) {
//				SubMonitor subMonitor = SubMonitor.convert(monitor);
//			    if (monitor.isCanceled()) {
//			    	log.info("Saving the SaltProject \"{}\" was interrupted by the user.", projectName);
//			    	return Status.CANCEL_STATUS;
//			    }
//		    	subMonitor.setTaskName("Saving Atomic project \"" + projectName + "\".");
//		    	project.saveSaltProject(uri);
//			    return Status.OK_STATUS;
//			}
//		};
//		saltProjectCreationJob.schedule();
		try {
			iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			log.error("Could not refresh IProject for SaltProject {}!", projectName, e);
		}
		// Finally, return true (i.e., wizard is done).
		return true;
	}
	
	@Override
	public String getWindowTitle() {
		return "New Atomic Project";
	}

}

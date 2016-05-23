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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.salt.SaltProjectCompiler;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
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
	
	private NewAtomicProjectWizardPageProjectStructure structurePage = new NewAtomicProjectWizardPageProjectStructure();
	private NewAtomicProjectWizardPageTokenization tokenizationPage = new NewAtomicProjectWizardPageTokenization();
	
	/*
	 * @copydoc @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPages() {
		addPage(getStructurePage());
		addPage(getTokenizationPage());
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return (getStructurePage().isPageComplete() && getTokenizationPage().isPageComplete());
	}
	
	/*
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Corpus projectData = getStructurePage().getModel();
		// TODO: Compile SaltProject form projectData
		SaltProjectCompiler compiler = new SaltProjectCompiler(projectData);
		final SaltProject project = compiler.run();
		List<Tokenizer> orderedTokenizers = getTokenizationPage().getTokenizers();
		for (SCorpusGraph corpusGraph : project.getCorpusGraphs()) {
			for (SDocument document : corpusGraph.getDocuments()) {
				for (Tokenizer tokenizer : orderedTokenizers) {
					tokenizer.processDocument(document);
				}
			}
		}
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		log.info("Workspace root is {}.", workspaceRoot.getLocation().toOSString());
		IProject iProject = workspaceRoot.getProject(project.getName());
		log.info("Created IProject OBJECT with name \"{}\" at workspace root.", iProject.getName());
		try {
			iProject.create(null);
			log.info("Successfully created IProject \"{}\".", iProject.getName());
			iProject.open(null);
			log.info("Opened IProject \"{}\".", iProject.getName());
		}
		catch (CoreException e) {
			// FIXME Check this in wizard!
			MessageDialog.openError(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), "Atomic project note created!", "A project by the name of \"" + project.getName() + "\" already exists.");
			log.error("Could not create and open the IProject for SaltProject {}!", project.getName(), e);
			return false;
		}
		File iProjectLocation = new File(iProject.getLocation().toString());
		final URI uri = URI.createFileURI(iProjectLocation.getAbsolutePath());
		
		// FIXME Check how this is reported in UI!
		Job saltProjectCreationJob = new Job("Create SaltProject.") {
			
			@Override
			  protected IStatus run(IProgressMonitor monitor) {
			    SubMonitor subMonitor = SubMonitor.convert(monitor);
			    if (monitor.isCanceled()) {
			    	log.info("Saving the SaltProject \"{}\" was interrupted by the user.", project.getName());
			    	return Status.CANCEL_STATUS;
			    }
		    	subMonitor.setTaskName("Saving Atomic project \"" + project.getName() + "\".");
		    	project.saveSaltProject(uri);
			    return Status.OK_STATUS;
			}
		};
		saltProjectCreationJob.schedule();
		try {
			iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			log.error("Could not refresh IProject for SaltProject {}!", project.getName(), e);
		}
		// Finally, return true
		return true;
	}
	
	@Override
	public String getWindowTitle() {
		return "New Atomic Project";
	}

	/**
	 * @return the structurePage
	 */
	private NewAtomicProjectWizardPageProjectStructure getStructurePage() {
		return structurePage;
	}

	/**
	 * @return the tokenizationPage
	 */
	public NewAtomicProjectWizardPageTokenization getTokenizationPage() {
		return tokenizationPage;
	}

	/**
	 * @param tokenizationPage the tokenizationPage to set
	 */
	public void setTokenizationPage(NewAtomicProjectWizardPageTokenization tokenizationPage) {
		this.tokenizationPage = tokenizationPage;
	}

}

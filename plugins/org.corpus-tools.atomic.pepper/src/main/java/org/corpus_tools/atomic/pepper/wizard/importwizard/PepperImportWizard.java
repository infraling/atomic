/*******************************************************************************
* Copyright 2014 Friedrich Schiller University Jena
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
*     Michael Grübsch - initial API and implementation
*     Stephan Druskat - update to Pepper 3.x API
*******************************************************************************/

package org.corpus_tools.atomic.pepper.wizard.importwizard;

import java.util.ArrayList;
import java.util.List;

import org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard;
import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageDirectory;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageFormat;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageModule;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageProperties;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.common.PepperModuleDesc;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * An implementation of {@link AbstractPepperWizard} for corpus imports.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class PepperImportWizard extends AbstractPepperWizard implements IImportWizard {
	protected String projectName;

	public PepperImportWizard() {
		super("Import via Pepper", WizardMode.IMPORT);
	}

	/* 
	 * @copydoc @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialize();
	}

	/*
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new PepperWizardPageModule(this, "selectImporter", "Select Import Module", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper import module."));
		addPage(new PepperWizardPageFormat(this, "selectFormat", "Select Import Format", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper import format."));
		addPage(new PepperWizardPageDirectory(this, "selectTargetPath", "Select Import Path", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper import path."));
		addPage(new PepperWizardPageProperties(this, "selectProperties", "Select Import Properties", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Edit the pepper import module properties."));
		addPage(new PepperImportWizardPageProjectName(this, "selectProjectName", "Select Project Name", DEFAULT_PAGE_IMAGE_DESCRIPTOR));
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#getSupportedFormats()
	 */
	@Override
	public List<FormatDesc> getSupportedFormats() {
		PepperModuleDesc module = getPepperModule();
		return module != null ? module.getSupportedFormats() : new ArrayList<FormatDesc>();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#getProject()
	 */
	@Override
	protected IProject getProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(getProjectName());
		project.create(null);
		project.open(null);
		return project;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#createModuleRunnable(org.eclipse.core.resources.IProject, boolean)
	 */
	@Override
	protected PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable) {
		return new ImportModuleRunnable(this, project, cancelable);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#canPerformFinish()
	 */
	@Override
	protected boolean canPerformFinish() {
		return super.canPerformFinish() && projectName != null && !ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists();
	}

}

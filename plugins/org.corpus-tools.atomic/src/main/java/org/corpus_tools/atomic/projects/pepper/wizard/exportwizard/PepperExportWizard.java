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

package org.corpus_tools.atomic.projects.pepper.wizard.exportwizard;

import java.util.ArrayList;
import java.util.List;

import org.corpus_tools.atomic.projects.pepper.wizard.AbstractPepperWizard;
import org.corpus_tools.atomic.projects.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.atomic.projects.pepper.wizard.PepperWizardPageDirectory;
import org.corpus_tools.atomic.projects.pepper.wizard.PepperWizardPageFormat;
import org.corpus_tools.atomic.projects.pepper.wizard.PepperWizardPageModule;
import org.corpus_tools.atomic.projects.pepper.wizard.PepperWizardPageProperties;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.common.PepperModuleDesc;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * An implementation of {@link AbstractPepperWizard} for corpus exports.
 *
 * @author Michael Grübsch
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class PepperExportWizard extends AbstractPepperWizard implements IExportWizard {
	protected IProject selectedProject = null;

	public PepperExportWizard() {
		super("Export via Pepper", WizardMode.EXPORT);
	}

	/*
	 * @copydoc @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialize();
		if (1 == selection.size()) {
			Object element = selection.getFirstElement();
			if (element instanceof IProject) {
				selectedProject = (IProject) element;
			}
			else {
				new MessageDialog(this.getShell(), "Error", null, "Selection is not a project!", MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0).open();
			}
		}
		else {
			new MessageDialog(this.getShell(), "Error", null, "To run the Pepper Export Wizard select exactly one project!", MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0).open();
		}
	}

	/*
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		if (selectedProject != null) {
			addPage(new PepperWizardPageModule(this, "selectExporter", "Select Export Module", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper export module."));
			addPage(new PepperWizardPageFormat(this, "selectFormat", "Select Export Format", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper export format."));
			addPage(new PepperWizardPageDirectory(this, "selectTargetPath", "Select Export Path", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Select the pepper export path."));
			addPage(new PepperWizardPageProperties(this, "selectProperties", "Select Export Properties", DEFAULT_PAGE_IMAGE_DESCRIPTOR, "Edit the pepper export module properties."));
		}
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#getSupportedFormats()
	 */
	@Override
	public List<FormatDesc> getSupportedFormats() {
		PepperModuleDesc module = getPepperModule();
		return module != null ? module.getSupportedFormats() : new ArrayList<FormatDesc>();
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#createModuleRunnable(org.eclipse.core.resources.IProject, boolean)
	 */
	@Override
	protected PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable) {
		return new ExportModuleRunnable(this, project, cancelable);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard#getProject()
	 */
	@Override
	protected IProject getProject() throws CoreException {
		return selectedProject;
	}
}

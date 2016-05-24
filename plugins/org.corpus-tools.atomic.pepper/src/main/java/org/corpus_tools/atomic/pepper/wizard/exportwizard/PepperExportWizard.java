/*******************************************************************************
 * Copyright 2014 Friedrich Schiller University Jena
 * Vivid Sky - Softwaremanufaktur, Michael Gr�bsch.
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

package org.corpus_tools.atomic.pepper.wizard.exportwizard;

import java.util.List;

import org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard;
import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageDirectory;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageFormat;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageModule;
import org.corpus_tools.atomic.pepper.wizard.PepperWizardPageProperties;
import org.corpus_tools.pepper.modules.PepperExporter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Michael Gr�bsch
 * @version $Revision$, $Date$
 */
public class PepperExportWizard extends AbstractPepperWizard<PepperExporter> implements IExportWizard {
	protected IProject selectedProject = null;

	public PepperExportWizard() {
		super("Export via Pepper", WizardMode.EXPORT);
	}

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		if (selectedProject != null) {
			addPage(new PepperWizardPageModule<PepperExporter>(this, "selectExporter", "Select Export Module", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper export module."));
			addPage(new PepperWizardPageFormat<PepperExporter>(this, "selectFormat", "Select Export Format", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper export format."));
			addPage(new PepperWizardPageDirectory<PepperExporter>(this, "selectTargetPath", "Select Export Path", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper export path."));
			addPage(new PepperWizardPageProperties<PepperExporter>(this, "selectProperties", "Select Export Properties", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Edit the pepper export module properties."));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<PepperExporter> resolvePepperModules(PepperModuleResolver pepperModuleResolver) {
		return pepperModuleResolver.getPepperExporters();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EList<FormatDefinition> getSupportedFormats() {
		PepperExporter module = getPepperModule();
		return module != null ? module.getSupportedFormats() : new BasicEList<FormatDefinition>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable) {
		return new ExportModuleRunnable(this, project, cancelable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProject getProject() throws CoreException {
		return selectedProject;
	}
}

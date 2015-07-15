/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * Michael Gr�bsch
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

package de.uni_jena.iaa.linktype.atomic.model.pepper.importwizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.MODULE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperModuleDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.core.ModuleResolver;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperImporter;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.AbstractPepperWizard;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperModuleRunnable;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperWizardPageDirectory;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperWizardPageFormat;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperWizardPageModule;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperWizardPageProperties;
import de.uni_jena.iaa.linktype.atomic.model.salt.project.AtomicProjectService;

public class PepperImportWizard 
  extends 
    AbstractPepperWizard<PepperImporter>
  implements 
    IImportWizard
{
  protected String projectName;

  public PepperImportWizard()
  {
    super("Import via Pepper", WizardMode.IMPORT);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    initialize();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages()
  {
    addPage(new PepperWizardPageModule<PepperImporter>(this, "selectImporter", "Select Import Module", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper import module."));
    addPage(new PepperWizardPageFormat<PepperImporter>(this, "selectFormat", "Select Import Format", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper import format."));
    addPage(new PepperWizardPageDirectory<PepperImporter>(this, "selectTargetPath", "Select Import Path", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Select the pepper import path."));
    addPage(new PepperWizardPageProperties<PepperImporter>(this, "selectProperties", "Select Import Properties", DEFAULT_PAGE_IAMGE_DESCRIPTOR, "Edit the pepper import module properties."));
    addPage(new PepperImportWizardPageProjectName(this, "selectProjectName", "Select Project Name", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
  }

//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  protected List<PepperImporter> resolvePepperModules(ModuleResolver pepperModuleResolver)
//  {
//    return pepperModuleResolver.getPepperImporters();
//  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FormatDesc> getSupportedFormats()
  {
    PepperImporter module = getPepperModule();
    return module != null ? module.getSupportedFormats() : new ArrayList<FormatDesc>();
  }

  public String getProjectName()
  {
    return projectName;
  }

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IProject getProject() throws CoreException
  {
    return AtomicProjectService.getInstance().createIProject(getProjectName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable)
  {
    return new ImportModuleRunnable(this, project, cancelable);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean canPerformFinish()
  {
    AtomicProjectService atomicProjectService = AtomicProjectService.getInstance();
    return 
        super.canPerformFinish()
     && projectName != null
     && ! atomicProjectService.isProjectExisting(projectName);
  }

/** 
 * {@inheritDoc}
 */
@Override
public List<PepperModuleDesc> getPepperModules() {
	return(super.getPepperModules(MODULE_TYPE.IMPORTER));
}
}

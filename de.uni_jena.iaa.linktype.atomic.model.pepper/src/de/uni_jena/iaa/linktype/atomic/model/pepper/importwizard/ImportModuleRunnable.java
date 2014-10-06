/*******************************************************************************
 * Copyright 2014 Friedrich Schiller University Jena
 * Vivid Sky - Softwaremanufaktur, Michael Grübsch.
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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ExporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ImporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParamsFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.saltXML.SaltXMLExporter;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.AbstractPepperWizard;
import de.uni_jena.iaa.linktype.atomic.model.pepper.wizard.PepperModuleRunnable;

public class ImportModuleRunnable extends PepperModuleRunnable
{
  public ImportModuleRunnable(PepperImportWizard pepperWizard, IProject project, boolean cancelable)
  {
    super(pepperWizard, project, cancelable);
  }

  @Override
  protected ImporterParams createImporterParams()
  {
    ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
    importerParams.setModuleName(pepperWizard.getPepperModule().getName());
    importerParams.setFormatName(pepperWizard.getFormatDefinition().getFormatName());
    importerParams.setFormatVersion(pepperWizard.getFormatDefinition().getFormatVersion());
    importerParams.setSourcePath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
    return importerParams;
  }

  @Override
  protected ExporterParams createExporterParams()
  {
    ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
    SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
    exporterParams.setModuleName(saltXMLExporter.getName());
    exporterParams.setFormatName(AbstractPepperWizard.SALT_XML_FORMAT_NAME);
    exporterParams.setFormatVersion(AbstractPepperWizard.SALT_XML_FORMAT_VERSION);
    exporterParams.setDestinationPath(URI.createURI(project.getLocationURI().toString()));
    return exporterParams;
  }
}
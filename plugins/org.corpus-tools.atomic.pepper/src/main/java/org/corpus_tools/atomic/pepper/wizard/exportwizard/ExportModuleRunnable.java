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

import java.io.File;

import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

public class ExportModuleRunnable //extends PepperModuleRunnable
{
//  public ExportModuleRunnable(PepperExportWizard pepperWizard, IProject project, boolean cancelable)
//  {
//    super(pepperWizard, project, cancelable);
//  }
//
//  @Override
//  protected ImporterParams createImporterParams()
//  {
//    ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
//    SaltXMLImporter saltXMLImporter = new SaltXMLImporter();
//    importerParams.setModuleName(saltXMLImporter.getName());
//    importerParams.setFormatName(AbstractPepperWizard.SALT_XML_FORMAT_NAME);
//    importerParams.setFormatVersion(AbstractPepperWizard.SALT_XML_FORMAT_VERSION);
//    importerParams.setSourcePath(URI.createURI(project.getLocationURI().toString()));
//    return importerParams;
//  }
//
//  @Override
//  protected ExporterParams createExporterParams()
//  {
//    ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
//    exporterParams.setModuleName(pepperWizard.getPepperModule().getName());
//    exporterParams.setFormatName(pepperWizard.getFormatDefinition().getFormatName());
//    exporterParams.setFormatVersion(pepperWizard.getFormatDefinition().getFormatVersion());
//    exporterParams.setDestinationPath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
//    return exporterParams;
//  }
}
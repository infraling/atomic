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

package org.corpus_tools.atomic.pepper.wizard.importwizard;

import java.io.File;

import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.modules.coreModules.SaltXMLExporter;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

public class ImportModuleRunnable extends PepperModuleRunnable
{
  public ImportModuleRunnable(PepperImportWizard pepperWizard, IProject project, boolean cancelable)
  {
    super(pepperWizard, project, cancelable);
  }

  @Override
  protected StepDesc createImporterParams()
  {
	  StepDesc stepDesc= new StepDesc();
	  stepDesc.setCorpusDesc(new CorpusDesc());
	  stepDesc.getCorpusDesc().setCorpusPath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
	  stepDesc.setName(pepperWizard.getPepperModule().getName());
//	  stepDesc.setVersion(saltXMLImporter.getVersion());
	  stepDesc.setModuleType(MODULE_TYPE.IMPORTER);
	  System.err.println(stepDesc);
	  System.out.println("################ " + System.getProperty("osgi.bundles"));
	  return(stepDesc);
	  
//    ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
//    importerParams.setModuleName(pepperWizard.getPepperModule().getName());
//    importerParams.setFormatName(pepperWizard.getFormatDesc().getFormatName());
//    importerParams.setFormatVersion(pepperWizard.getFormatDesc().getFormatVersion());
//    importerParams.setSourcePath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
//    return importerParams;
  }

  /**
   * Creates a dummy exporter step containing the SaltXMLExporter description.
   */
  @Override
  protected StepDesc createExporterParams()
  {
	  SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
	  System.err.println(saltXMLExporter);
	  StepDesc stepDesc= new StepDesc();
	  System.err.println(stepDesc);
	  stepDesc.setCorpusDesc(new CorpusDesc());
	  System.err.println(stepDesc.getCorpusDesc());
	  stepDesc.getCorpusDesc().setCorpusPath(URI.createURI(project.getLocationURI().toString()));
	  stepDesc.setName(saltXMLExporter.getName());
	  stepDesc.setVersion(saltXMLExporter.getVersion());
	  stepDesc.setModuleType(MODULE_TYPE.EXPORTER);
	  return(stepDesc);
	  
//    ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
//    SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
//    exporterParams.setModuleName(saltXMLExporter.getName());
//    exporterParams.setFormatName(AbstractPepperWizard.SALT_XML_FORMAT_NAME);
//    exporterParams.setFormatVersion(AbstractPepperWizard.SALT_XML_FORMAT_VERSION);
//    exporterParams.setDestinationPath(URI.createURI(project.getLocationURI().toString()));
//    return exporterParams;
  }
}
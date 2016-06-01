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
import java.io.IOException;

import org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard;
import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.modules.coreModules.SaltXMLExporter;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

public class ImportModuleRunnable extends PepperModuleRunnable {
	public ImportModuleRunnable(PepperImportWizard pepperWizard, IProject project, boolean cancelable) {
		super(pepperWizard, project, cancelable);
	}

	@Override
	protected StepDesc createImporterParams() {
		StepDesc stepDesc = new StepDesc();
		stepDesc.setName(pepperWizard.getPepperModule().getName());
		stepDesc.setVersion(pepperWizard.getPepperModule().getVersion());
		stepDesc.setCorpusDesc(new CorpusDesc().setCorpusPath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath())));
		stepDesc.setModuleType(MODULE_TYPE.IMPORTER);
		// System.out.println("################ " + System.getProperty("osgi.bundles"));
		return (stepDesc);

		// ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
		// importerParams.setModuleName(pepperWizard.getPepperModule().getName());
		// importerParams.setFormatName(pepperWizard.getFormatDesc().getFormatName());
		// importerParams.setFormatVersion(pepperWizard.getFormatDesc().getFormatVersion());
		// importerParams.setSourcePath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
		// return importerParams;
	}

	/**
	 * Creates a dummy exporter step containing the SaltXMLExporter description.
	 */
	@Override
	protected StepDesc createExporterParams() {
		SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
		StepDesc stepDesc = new StepDesc();
		stepDesc.setCorpusDesc(new CorpusDesc());
		stepDesc.getCorpusDesc().setCorpusPath(URI.createFileURI(new File(project.getLocation().toString()).getAbsolutePath()));
		stepDesc.setName(saltXMLExporter.getName());
		stepDesc.setVersion(saltXMLExporter.getVersion());
		stepDesc.setModuleType(MODULE_TYPE.EXPORTER);
		return (stepDesc);

		// ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
		// SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
		// exporterParams.setModuleName(saltXMLExporter.getName());
		// exporterParams.setFormatName(AbstractPepperWizard.SALT_XML_FORMAT_NAME);
		// exporterParams.setFormatVersion(AbstractPepperWizard.SALT_XML_FORMAT_VERSION);
		// exporterParams.setDestinationPath(URI.createURI(project.getLocationURI().toString()));
		// return exporterParams;
	}

	// @Override
	// /**
	// * Creates and starts a Pepper job. The job is created via {@link AbstractPepperWizard#getPepper()}.
	// * @throws IOException
	// * @throws CoreException
	// */
	// protected void runPepper() throws IOException, CoreException
	// {
	// System.err.println("IN LOCAL RUNPEPPER()");
	//
	//// ImporterParams importerParams = createImporterParams();
	//// setSpecialParams(importerParams);
	////
	//// ExporterParams exporterParams = createExporterParams();
	////
	//// PepperJobParams pepperJobParams = PepperParamsFactory.eINSTANCE.createPepperJobParams();
	//// pepperJobParams.setId(AbstractPepperWizard.PEPPER_JOB_ID.incrementAndGet());
	//// pepperJobParams.getImporterParams().add(importerParams);
	//// pepperJobParams.getExporterParams().add(exporterParams);
	////
	//// PepperParams pepperParams = PepperParamsFactory.eINSTANCE.createPepperParams();
	//// pepperParams.getPepperJobParams().add(pepperJobParams);
	////
	//// PepperConverter pepperConverter = pepperWizard.getPepper();
	//// pepperConverter.setPepperParams(pepperParams);
	//// pepperConverter.start();
	//
	// Pepper pepper = pepperWizard.getPepper();
	// String jobId = pepper.createJob();
	// PepperJob pepperJob= pepper.getJob(jobId);
	// System.err.println("PEPPERJOB: " + pepperJob);
	// pepperJob.addStepDesc(createImporterParams());
	//// pepperJob.addStepDesc(createExporterParams());
	// System.err.println("STEP DESCS: " + pepperJob.getStepDescs());
	// pepperJob.convertFrom(); // TODO: CONVERT FROM
	//// System.err.println("PROJECT: " + pepperJob.getSaltProject());
	//// for (SDocument doc : pepperJob.getSaltProject().getCorpusGraphs().get(0).getDocuments()) {
	//// System.err.println(doc.getDocumentGraph().getTextualDSs().get(0).getText());
	//// }
	// SaltProject saltProject = pepperJob.getSaltProject();
	// URI uri = URI.createFileURI(new File(project.getLocation().toString()).getAbsolutePath());//project.getLocation().toString());//getLocationURI().toString());
	// System.err.println("URI: " + uri);
	//
	// saltProject.saveSaltProject(uri);
	//
	// project.refreshLocal(IResource.DEPTH_INFINITE, null);
	// }
}
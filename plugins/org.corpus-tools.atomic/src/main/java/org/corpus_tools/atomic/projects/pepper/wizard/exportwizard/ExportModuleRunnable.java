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

import java.io.File;

import org.corpus_tools.atomic.projects.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.atomic.projects.pepper.wizard.importwizard.ImportModuleRunnable;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.modules.coreModules.SaltXMLImporter;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

/**
 * A {@link PepperModuleRunnable} for export jobs.
 * TODO FIXME: Implement in parallel to {@link ImportModuleRunnable}!
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class ExportModuleRunnable extends PepperModuleRunnable {
	public ExportModuleRunnable(PepperExportWizard pepperWizard, IProject project, boolean cancelable) {
		super(pepperWizard, project, cancelable);
	}

	/**
	 * Creates a dummy import step for the SalXMLImporter.
	 */
	@Override
	protected StepDesc createImporterParams() {
		SaltXMLImporter saltXMLImporter = new SaltXMLImporter();
		StepDesc stepDesc = new StepDesc();
		stepDesc.setCorpusDesc(new CorpusDesc());
		stepDesc.getCorpusDesc().setCorpusPath(URI.createURI(project.getLocationURI().toString()));
		stepDesc.setName(saltXMLImporter.getName());
		stepDesc.setVersion(saltXMLImporter.getVersion());
		stepDesc.setModuleType(MODULE_TYPE.IMPORTER);
		return (stepDesc);
	}

	@Override
	protected StepDesc createExporterParams() {
		StepDesc stepDesc = new StepDesc();
		stepDesc.setCorpusDesc(new CorpusDesc());
		stepDesc.getCorpusDesc().setCorpusPath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath()));
		stepDesc.setName(pepperWizard.getPepperModule().getName());
		// stepDesc.setVersion(saltXMLImporter.getVersion());
		stepDesc.setModuleType(MODULE_TYPE.EXPORTER);
		return (stepDesc);
	}
}
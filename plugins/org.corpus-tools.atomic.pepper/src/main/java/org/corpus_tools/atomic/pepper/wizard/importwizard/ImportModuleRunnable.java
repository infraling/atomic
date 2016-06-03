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

import java.io.File; 
import org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.modules.coreModules.SaltXMLExporter;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

/**
 * An implementation of {@link PepperModuleRunnable} for import jobs.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class ImportModuleRunnable extends PepperModuleRunnable {
	public ImportModuleRunnable(PepperImportWizard pepperWizard, IProject project, boolean cancelable) {
		super(pepperWizard, project, cancelable);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable#createImporterParams()
	 */
	@Override
	protected StepDesc createImporterParams() {
		StepDesc stepDesc = new StepDesc();
		stepDesc.setName(pepperWizard.getPepperModule().getName());
		stepDesc.setVersion(pepperWizard.getPepperModule().getVersion());
		stepDesc.setCorpusDesc(new CorpusDesc().setCorpusPath(URI.createFileURI(new File(pepperWizard.getExchangeTargetPath()).getAbsolutePath())));
		stepDesc.setModuleType(MODULE_TYPE.IMPORTER);
		return (stepDesc);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.PepperModuleRunnable#createExporterParams()
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
	}
}
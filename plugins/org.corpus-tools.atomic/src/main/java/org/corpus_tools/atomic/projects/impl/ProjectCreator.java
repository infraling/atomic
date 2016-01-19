/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
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
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.projects.impl;

import org.corpus_tools.atomic.projects.IAtomicProjectData;
import org.corpus_tools.atomic.projects.IProjectCreator;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectCreator implements IProjectCreator {

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IProjectCreator#createSingleCorpusProject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SaltProject createSingleCorpusProject(String projectName, String corpusName, String documentName, String sourceText) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IProjectCreator#createMultiCorpusProject(org.corpus_tools.atomic.projects.IAtomicProjectData)
	 */
	@Override
	public SaltProject createMultiCorpusProject(IAtomicProjectData projectGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IProjectCreator#saveProject(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject)
	 */
	@Override
	public void saveProject(SaltProject project) {
		// TODO Auto-generated method stub
		
	}

}

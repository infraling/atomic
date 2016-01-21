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
package org.corpus_tools.atomic.internal.projects;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.corpus_tools.atomic.projects.AtomicComplexProjectData;

/**
 * This class provides a default implementation of {@link AtomicComplexProjectData}
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicComplexProjectData extends DefaultAtomicProjectData implements AtomicComplexProjectData {

	private Map<String, LinkedHashSet<String>> rootCorpora = new HashMap<>();

	/**
	 * Passes on the project name to {@link DefaultAtomicProjectData} and
	 * initializes the {@link #rootCorpora} field.
	 * 
	 * @param projectName
	 */
	public DefaultAtomicComplexProjectData(String projectName) {
		super(projectName);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicComplexProjectData#getRootCorpora()
	 */
	@Override
	public Map<String, LinkedHashSet<String>> getRootCorpora() {
		return rootCorpora ;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicComplexProjectData#createDocumentAndAddToSingleRootSubCorpus(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createDocumentAndAddToSingleRootSubCorpus(String subCorpusName, String documentName, String documentSourceText) {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicComplexProjectData#createDocumentAndAddToSubCorpus(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createDocumentAndAddToSubCorpus(String rootCorpusName, String corpusName, String documentName, String documentSourceText) {
		// TODO Auto-generated method stub

	}

}

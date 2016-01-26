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

import org.corpus_tools.atomic.projects.ProjectData;
import org.corpus_tools.atomic.projects.ProjectNode;

/**
 * Default implementation of {@link ProjectData}. This implementation
 * notably uses a {@link HashSet} to store the project's
 * root corpora.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class DefaultProjectData implements ProjectData {

	private String name;
	private HashMap<String, ProjectNode> corpora = new HashMap<>();

	/**
	 * Constructor taking the name of the project.
	 * 
	 * @param name
	 */
	public DefaultProjectData(String name) {
		this.name = name;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#getCorpora()
	 */
	@Override
	public HashMap<String, ProjectNode> getCorpora() {
		return corpora;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#addCorpus(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public void addCorpus(ProjectNode corpus) {
		getCorpora().put(corpus.getName(), corpus);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#removeCorpus(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public ProjectNode removeCorpus(String corpusName) {
		return getCorpora().remove(corpusName);
	}

}

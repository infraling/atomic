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
import org.corpus_tools.atomic.models.AbstractBean;
import org.corpus_tools.atomic.projects.ProjectData;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.eclipse.core.runtime.Assert;

/**
 * Default implementation of {@link ProjectData}. This implementation
 * notably uses a {@link HashMap} to store the project's
 * root corpora.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class DefaultProjectData extends AbstractBean implements ProjectData {
	
	/**
	 * Property <code>name</name>, readable and writable.
	 */
	private String name = null;

	/**
	 * Property <code>corpora</name>, readable and writable.
	 */
	private HashMap<String, ProjectNode> corpora = null;
	
	/**
	 * Default no-arg constructor (JavaBean compliance). 
	 */
	public DefaultProjectData() {
		corpora = new HashMap<>();
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		final String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#getCorpora()
	 */
	@Override
	public HashMap<String, ProjectNode> getCorpora() {
		return corpora;
	}

	/**
	 * @param corpora the corpora to set
	 */
	public void setCorpora(final HashMap<String, ProjectNode> corpora) {
		final HashMap<String, ProjectNode> oldCorpora = this.corpora;
		this.corpora = corpora;
		firePropertyChange("corpora", oldCorpora, this.corpora);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#addCorpus(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public void addCorpus(final ProjectNode corpus) {
		Assert.isNotNull(corpus);
		corpus.setParent(this);
		final HashMap<String,ProjectNode> newCorpora = getCorpora();
		newCorpora.put(corpus.getName(), corpus);
		setCorpora(newCorpora);
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectData#removeCorpus(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public ProjectNode removeCorpus(final String corpusName) {
		Assert.isNotNull(corpusName);
		final HashMap<String, ProjectNode> newCorpora = getCorpora();
		ProjectNode removedCorpus = newCorpora.remove(corpusName);
		setCorpora(newCorpora);
		return removedCorpus;
	}

}

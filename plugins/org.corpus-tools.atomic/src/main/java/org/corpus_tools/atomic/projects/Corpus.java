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
package org.corpus_tools.atomic.projects;

import java.util.LinkedHashMap;

/**
 * This class represents a corpus, i.e., a node in the
 * corpus structure tree. A corpus can be a root corpus,
 * i.e., the topmost structural element in a project. It
 * must have one or more children: at least one sub-corpus
 * or one document.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Corpus implements ProjectNode {
	
	private String name;
	private LinkedHashMap<String, ProjectNode> children = new LinkedHashMap<>();

	/**
	 * Constructor taking the name of the corpus.
	 */
	public Corpus(String name) {
		this.name = name;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getChildren()
	 */
	@Override
	public LinkedHashMap<String, ProjectNode> getChildren() {
		return children ;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#addChild(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public void addChild(ProjectNode child) {
		getChildren().put(child.getName(), child);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#removeChild(java.lang.String)
	 */
	@Override
	public ProjectNode removeChild(String childName) {
		return getChildren().remove(childName);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#setName(java.lang.String)
	 */
	@Override
	public String setName(String name) {
		String oldName = getName();
		this.name = name;
		return oldName;
	}

}

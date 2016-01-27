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

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a document, i.e., a leaf in the
 * corpus structure tree. A document must have a name
 * and a source text.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Document extends MutablePair<String, String> implements ProjectNode {

	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "Document".
	 */
	private static final Logger log = LogManager.getLogger(Document.class);
	
	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -4638023814060887391L;
	private String sourceText;
	private String name;
	
	/**
	 * Constructor taking a document name and a document source text as arguments.
	 */
	public Document(String name, String sourceText) {
		this.name = name;
		this.sourceText = sourceText;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getChildren()
	 */
	@Override
	public LinkedHashMap<String, ProjectNode> getChildren() {
		log.warn("A document cannot have any children, therefore calling \"getChildren()\" on one always returns null.");
		return null;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#addChild(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public void addChild(ProjectNode child) {
		log.warn("A document cannot have any children, therefore calling \"addChild()\" on one does onthing.");
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#removeChild(java.lang.String)
	 */
	@Override
	public ProjectNode removeChild(String childName) {
		log.warn("A document cannot have any children, therefore calling \"removeChild()\" on one always returns null.");
		return null;
	}

	/**
	 * @return the sourceText
	 */
	public String getSourceText() {
		return sourceText;
	}

	public String setSourceText(String newSourceText) {
		String oldSourceText = getSourceText();
		this.sourceText = newSourceText; 
		return oldSourceText;
	}
	
	/* 
	 * @copydoc @see java.util.Map.Entry#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(String value) {
		return setSourceText(value);
	}

	/* 
	 * @copydoc @see org.apache.commons.lang3.tuple.Pair#getLeft()
	 */
	@Override
	public String getLeft() {
		return getName();
	}

	/* 
	 * @copydoc @see org.apache.commons.lang3.tuple.Pair#getRight()
	 */
	@Override
	public String getRight() {
		return getSourceText();
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

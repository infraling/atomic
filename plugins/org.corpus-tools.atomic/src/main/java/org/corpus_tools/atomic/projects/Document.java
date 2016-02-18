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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.models.AbstractBean;

/**
 * This class represents a document, i.e., a leaf in the
 * corpus structure tree. A document must have a name
 * and a source text.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Document extends AbstractBean implements ProjectNode {

	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "Document".
	 */
	private static final Logger log = LogManager.getLogger(Document.class);
	
	/**
	 * String representing the source text of the document.
	 */
	private String sourceText = null;
	
	/**
	 * String representing the name of the document. 
	 */
	private String name = null;
	
	/**
	 * Default no-arg constructor (JavaBean compliance). 
	 */
	public Document() {
	}

	/**
	 * @return the sourceText
	 */
	public String getSourceText() {
		return sourceText;
	}

	/**
	 * @param sourceText the sourceText to set
	 */
	public void setSourceText(String sourceText) {
		final String oldSourceText = this.sourceText;
		this.sourceText = sourceText;
		firePropertyChange("sourceText", oldSourceText, this.sourceText);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
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
	public ProjectNode addChild(ProjectNode child) {
		log.warn("A document cannot have any children, therefore calling \"addChild()\" on one does nothing.");
		return null;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#removeChild(java.lang.String)
	 */
	@Override
	public ProjectNode removeChild(String childName) {
		log.warn("A document cannot have any children, therefore calling \"removeChild()\" on one always returns null.");
		return null;
	}

}

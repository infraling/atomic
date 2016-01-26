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
 * A project node is a node in the n-ary corpus structure tree, i.e.,
 * an abstraction over corpora and documents. Every {@link ProjectNode}
 * must have a {@link String} name.
 * <p>
 * In Atomic, a project (the topmost structural entity for data) can
 * contain one or more corpora, which in turn can contain zero or more
 * corpora ("sub-corpora") as well as zero or more documents (containing
 * the source text).
 * <p>
 * In other words, a project in Atomic can contain one or more
 * n-ary trees, the nodes of which are implementations of this
 * interface, i.e., a corpus or a document.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 * 
 * <p>@see {@link ProjectData}
 *
 */
public interface ProjectNode {
	
	/**
	 * Returns the name of the project node.
	 *
	 * @return the name of the {@link ProjectNode}
	 */
	public String getName();
	
	/**
	 * Returns the value of the project node.
	 * In case of a corpus, this is the corpus'
	 * name. In case of a document, this is the
	 * name and the source text of the document. 
	 *
	 * @return the value of the project node
	 */
	public Object value();
	
	/**
	 * Returns the child nodes of this project element.
	 * <p>
	 * This method must return null when the project
	 * node represents a document, as documents can 
	 * have no children. It must return a non-empty
	 * {@link LinkedHashMap} when the project node 
	 * represents a corpus, as corpora must have at
	 * least one child, either a sub-corpus or a
	 * document.
	 *
	 * @return the project element's children
	 */
	public LinkedHashMap<String, ProjectNode> getChildren();
	
	/**
	 * Adds a child, i.e., a {@link ProjectNode}, to this
	 * {@link ProjectNode}. The new child is added to the
	 * {@link LinkedHashMap} of children.
	 *
	 * @param the child to add
	 */
	public void addChild(ProjectNode child);
	
	/**
	 * Removes a corpus from the project. The argument is
	 * the name of the {@link ProjectNode} to remove. 
	 * <p>
	 * Returns the previous node associated with this name
	 * or null if there was no node of this name.
	 *
	 * @param the child to remove
	 * @return the removed node or null
	 */
	public ProjectNode removeChild(String childName);
}

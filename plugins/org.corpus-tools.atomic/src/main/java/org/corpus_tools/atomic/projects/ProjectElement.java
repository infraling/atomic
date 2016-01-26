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

import java.util.Collection;

/**
 * A project element is an abstraction over corpora and documents.
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
public interface ProjectElement {
	
	/**
	 * Returns the value of a project element.
	 *
	 * @return the value of the project element
	 */
	public Object value();
	
	/**
	 * Returns the child nodes of this project element.
	 * <p>
	 * This method must never return null or an empty collection, 
	 * unless the object on which this method is called 
	 * represents a document, in which case this method must 
	 * return an empty collection. Clients
	 * implementing this interface should be aware that the
	 * order of elements in the {@link Collection} of
	 * {@link ProjectElement}s depends on the concrete 
	 * implementation of this interface.
	 *
	 * @return the project element's children
	 */
	public Collection<ProjectElement> getChildren();
	
	/**
	 * Adds a child, i.e., a {@link ProjectElement}, to this
	 * {@link ProjectElement}. The new child is added to the
	 * {@link Collection} of children as per default. Clients
	 * implementing this interface should be aware that the
	 * order of elements in the {@link Collection} of
	 * {@link ProjectElement}s returned by {@link #getChildren()}
	 * depends on the concrete implementation of this interface.
	 *
	 * @param the child to add
	 */
	public void addChild(ProjectElement child);
	
	/**
	 * Removes a corpus from the project. The argument is
	 * the {@link ProjectElement} to remove. Returns whether
	 * project had contained the corpus to remove, i.e.,
	 * whether the project has changed due to the method
	 * call. When returned, the element is not in the
	 * project anymore.
	 *
	 * @param the child to remove
	 * @return true if the child was removed, i.e., the
	 * parent has changed
	 */
	public boolean removeChild(ProjectElement child);
}

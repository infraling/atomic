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
 * A project is the topmost structural entity for data in Atomic.
 * <p>
 * A project can contain one or more n-ary trees, the nodes of
 * which must be implementations of {@link ProjectNode}. 
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface ProjectData {
	
	/**
	 * Returns the name of the project. Must not return null.
	 *
	 * @return the project name.
	 */
	public String getName();
	
	/**
	 * Returns the corpora ("root corpora") the project
	 * contains, i.e., a {@link Collection} of {@link ProjectNode}s
	 * which are the roots of n-ary trees of {@link ProjectNode}s.
	 *
	 * @return the project's corpora
	 */
	public Collection<ProjectNode> getCorpora();
	
	/**
	 * Adds a corpus to the project. The argument is
	 * the root {@link ProjectNode} of the n-ary
	 * corpus structure tree.
	 *
	 * @param the corpus to add
	 */
	public void addCorpus(ProjectNode corpus);
	
	/**
	 * Removes a corpus from the project. The argument is
	 * the {@link ProjectNode} to remove. Returns whether
	 * project had contained the corpus to remove, i.e.,
	 * whether the project has changed due to the method
	 * call. When returned, the element is not in the
	 * project anymore.
	 *
	 * @param the corpus to remove
	 * @return true if the corpus has been removed, i.e.,
	 * the project has changed
	 */
	public boolean removeCorpus(ProjectNode corpus);

}

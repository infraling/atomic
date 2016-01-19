/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
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

import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;

/**
 * Atomic project data is basically a String-based graph representing
 * the Atomic project structure, i.e.:
 * <p>
 * A project, which contains exactly one {@link SaltProject} (not
 * represented as {@link SaltProject}s are always XMI-persisted in
 * a file called SaltProject.salt), which contains a corpus graph
 * (not represented as Atomic assumes that there is always only
 * exactly one corpus graph), which contains n corpora, which
 * contain m documents each, which contain exactly one source text
 * each.
 * <p>
 * The relation between project and corpora is one of association
 * rather than structural containment.
 * <p> 
 * Classes implementing this interface provide the necessary methods
 * to construct an object containing all data that a multi-corpus,
 * multi-document project in Atomic can hold.
 * <p>
 * See also the graphical in {@link IProjectCreator}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface IAtomicProjectData {
	
	/**
	 * Returns the name of the project.
	 *
	 * @return the project name
	 */
	public String getProjectName();
	
	/**
	 * Returns the corpora contained in the project, as a {@link Map}.
	 * The map contains corpora with their name as the key. The map values
	 * are insertion-ordered sets of documents, i.e., tuples of document
	 * name and document text.
	 *
	 * @return the map of corpora
	 */
	public Map<String, LinkedHashSet<Pair<String, String>>> getCorpora();

}

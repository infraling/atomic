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

import org.apache.commons.lang3.tuple.Pair;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;

/**
 * Atomic project data is basically a String-based graph representing
 * the Atomic project structure, i.e.:
 * <p>
 * A project, which contains exactly one {@link SaltProject} (not
 * represented as {@link SaltProject}s are always XMI-persisted in
 * a file called SaltProject.salt).
 * <p>
 * Zero or more root corpora, depending on how the user wants to
 * organize her or his project:
 * <ul>
 * <li>If the project will contain only one corpus, there will be
 * one single corpus (which will be contained in one single
 * {@link SCorpusGraph}, not represented here).</li>
 * <li>If the project will contain more than one corpus, the
 * structure depends on how the user will organize the project:
 * <ul>
 * <li>If all corpora are to be contained on one level, one default
 * root corpus will be created to contain all corpora. This root
 * corpus, in turn, will be contained in in one single
 * {@link SCorpusGraph}, not represented here.</li>
 * <li>If the corpora will be organized under different
 * root corpora, these root corpora will be created to
 * contain their respective sub corpora. Each of these
 * root corpora will be contained in a seperate
 * {@link SCorpusGraph} to ensure structural compatibility
 * with ANNIS.</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * Each non-root corpus can contain m documents, 
 * which contain exactly one source text each.
 * <p>
 * The relation between project and corpora is one of association
 * rather than structural containment.
 * <p> 
 * Classes implementing this interface provide the necessary methods
 * to construct an object containing all data that a multi-corpus,
 * multi-document project in Atomic can hold.
 * <p>
 * See also the graphical in {@link ProjectCreator}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface AtomicProjectData {
	
	/**
	 * Returns the name of the project. Clients are responsible
	 * for setting the project name, so that this method does
	 * not return null.
	 *
	 * @return the project name
	 */
	public String getProjectName();
	
	/**
	 * Replaces the source text of a document with a replacement source text. 
	 * Returns true if the original source text (returned by {@link Pair#setValue(Object)}) 
	 * does not equal the replacement source text parameter.
	 *
	 * @param documentInCorpus The document for which the source text should be changed
	 * @param replacementSourceText The replacement source text
	 * @return True if the replacement source does not equal the original source text, otherwise false
	 */
	public boolean replaceDocumentSourceText(Pair<String, String> documentInCorpus, String replacementSourceText);

	
}

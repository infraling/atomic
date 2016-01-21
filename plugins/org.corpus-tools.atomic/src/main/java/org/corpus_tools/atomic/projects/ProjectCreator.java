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

import java.io.File;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;

/**
 * A {@link SaltProject} can contain multiple corpora, which can in turn contain multiple documents (containers for the single source texts and their annotations).
 * <p>
 * Hence, classes implementing a project creator for {@link SaltProject}s ("project": a project in Atomic, which is in fact a {@link SaltProject} must at least implement methods for the following scenarios:
 * <ul>
 * <li>Create a project with one corpus</li>
 * <ul>
 * <li>where the corpus contains exactly one document</li>
 * <li>where the corpus contains more than one document</li>
 * </ul>
 * <li>Create a project with more than one corpus</li>
 * <ul>
 * <li>where the corpus contains exactly one document</li>
 * <li>where the corpus contains more than one document</li>
 * </ul>
 * </ul>
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public interface ProjectCreator {

	/**
	 * Takes a single source text and writes it into a {@link SaltProject} which contains only one corpus and one document.
	 *
	 * @param projectName A name for the entire project
	 * @param corpusName A name for the single corpus
	 * @param documentName A name for the single document
	 * @param sourceText The source text of the single corpus document
	 * @return project A {@link SaltProject} containing a single corpus with a single document which is the container for the source text and its annotations.
	 */
	SaltProject createSingleCorpusProject(String projectName, String corpusName, String documentName, String sourceText);

	/**
	 * Takes a pre-defined project structure and writes it into a {@link SaltProject}.
	 * <p>
	 * The structure must be defined as follows.
	 * <p>
	 * <pre>
	 *                       Project name
	 *                         (String)
	 *                         /  |  \
	 *                        /   |   \
	 *         [Root corpus name]
	 *              (String)
	 *                   |
	 *              Corpus name  ...  ...
	 *               (String)
	 *               /   |   \
	 *              /    |    \
	 *  Document name   ...   Document name
	 *    (String)       |       (String)
	 *       |           |          |
	 *       |           |          |
	 *   Source text    ...     Source text
	 *    (String)               (String)    
	 * </pre>
	 * <p>
	 * Takes multiple source texts and creates a {@link SaltProject} with a more complex corpus structure
	 *
	 * @param projectGraph The project graph containing String representations of the named elements
	 * in the project (the project itself, its corpora, their documents), and the source text for 
	 * each document.
	 * @return The Salt project
	 */
	SaltProject createMultiCorpusProject(AtomicProjectData project);
	
	/**
	 * Saves the {@link SaltProject}.
	 *
	 * @param project
	 */
	SaltProject saveProject(SaltProject project, File saveFolder);
	
}

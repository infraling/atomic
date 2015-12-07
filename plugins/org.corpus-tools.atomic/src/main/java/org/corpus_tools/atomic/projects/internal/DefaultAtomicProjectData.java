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
package org.corpus_tools.atomic.projects.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.IAtomicProjectData;

/**
 * This internal class is the default implementation of {@link IAtomicProjectData}. It is not API and will only be used internally.
 * <p>
 * Clients should implement their own project data class is necessary.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class DefaultAtomicProjectData implements IAtomicProjectData {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DefaultAtomicProjectData".
	 */
	private static final Logger log = LogManager.getLogger(DefaultAtomicProjectData.class);

	private String projectName = null;
	private Map<String, Set<Pair<String, String>>> corpora = new HashMap<>(); // Each key = corpus -> Set of document<name,text>

	/**
	 * Constructor taking the name of the project as argument.
	 */
	public DefaultAtomicProjectData(String projectName) {
		this.setProjectName(projectName);
	}

	public void createDocumentAndAddToCorpus(String corpus, String documentName, String documentSourceText) {
		Pair<String, String> document = new MutablePair<String, String>(documentName, documentSourceText);
		if (getCorpora().containsKey(corpus)) {
			if (!getCorpora().get(corpus).add(document)) {
				log.warn("Could not add document {} to corpus {}.", document, corpus);
			}
		}
		else {
			Set<Pair<String, String>> newDocumentSet = new HashSet<>();
			if (newDocumentSet.add(document)) {
				getCorpora().put(corpus, newDocumentSet);
			}
			else {
				log.warn("Could not add document {} to corpus {}.", document, corpus);
			}
		}
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the corpora
	 */
	public Map<String, Set<Pair<String, String>>> getCorpora() {
		return corpora;
	}

	/**
	 * @param corpora the corpora to set
	 */
	public void setCorpora(Map<String, Set<Pair<String, String>>> corpora) {
		this.corpora = corpora;
	}

}

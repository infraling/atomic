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
package org.corpus_tools.atomic.internal.projects;

import org.apache.commons.lang3.tuple.Pair;
import org.corpus_tools.atomic.projects.AtomicProjectData;

/**
 * This class provides a default implementation for the methods in {@link AtomicProjectData}.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public abstract class DefaultAtomicProjectData implements AtomicProjectData {

	private String projectName;

	/**
	 * Constructor providing the possibility to set the project name.
	 */
	public DefaultAtomicProjectData(String projectName) {
		this.projectName = projectName;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicProjectData#getProjectName()
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicProjectData#replaceDocumentSourceText(org.apache.commons.lang3.tuple.Pair, java.lang.String)
	 */
	public boolean replaceDocumentSourceText(Pair<String, String> documentInCorpus, String replacementSourceText) {
		String originalSourceText = documentInCorpus.getValue();
		if (originalSourceText.equals(replacementSourceText)) {
			return false; // Not logically correct, but nothing actually changes.
		}
		originalSourceText = documentInCorpus.setValue(replacementSourceText); // SET is called here!
		return !replacementSourceText.equals(originalSourceText);
	}

}

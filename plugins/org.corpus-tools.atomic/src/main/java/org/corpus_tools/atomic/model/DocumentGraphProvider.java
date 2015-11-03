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
package org.corpus_tools.atomic.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IFile;

/**
 * Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DocumentGraphProvider {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DocumentGraphProvider".
	 */
	private static final Logger log = LogManager.getLogger(DocumentGraphProvider.class);
	
	private static DocumentGraphProvider instance;
	
	private DocumentGraphProvider() {
		log.trace("Created DocumentGraphProvider singleton object.");
	}
	
	public static DocumentGraphProvider getInstance() {
		if (instance == null) {
			log.trace("DocumentGraphProvider singleton object does not exist yet, creating it.");
			instance = new DocumentGraphProvider();
		}
		return instance;
	}
	
	public /*SDocumentGraph FIXME*/void getDocumentGraph(IFile iFile) {
		log.info("Preparing loading SDocumentGraph object from IFile argument (concrete argument = {}).", iFile);
//		return null;
	}
}

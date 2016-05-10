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
package org.corpus_tools.atomic.projects.salt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.Document;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;

/**
 * Creates a corpus document of type {@link SDocument}, and adds the features
 * provided by the {@link #documentData} argument.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DocumentCreationRunnable implements Runnable {
	
	// FIXME: Needs unit tests!
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DocumentCreationRunnable".
	 */
	private static final Logger log = LogManager.getLogger(DocumentCreationRunnable.class);
	
	private SDocument sDocument = null;

	private Document documentData = null;

	private SCorpus sCorpus = null;

	/**
	 * @param corpus
	 * @param child
	 */
	public DocumentCreationRunnable(SCorpus corpus, Document document) {
		this.documentData = document;
		this.sCorpus = corpus;
	}

	/* 
	 * @copydoc @see java.lang.Runnable#run()
	 */
	/**
	 * Creates an instance of {@link SDocument} and fills it with 
	 * the data from documentData.
	 *
	 * @param documentData the data for this document
	 * @return a newly created instance of {@link SDocument}, containing the data from documentData
	 */
	public void run() {
		log.entry(getDocumentData());
		SaltFactory factory = SaltFactory.eINSTANCE;
		setsDocument(factory.createSDocument());
		getsDocument().setSName(getDocumentData().getName());
		getsDocument().setSDocumentGraph(factory.createSDocumentGraph());
		STextualDS sourceText = factory.createSTextualDS();
		sourceText.setSText(getDocumentData().getSourceText());
		getsDocument().getSDocumentGraph().addSNode(sourceText);
		log.exit(getsDocument());
	}

	/**
	 * @return the document
	 */
	private Document getDocumentData() {
		return documentData;
	}

	/**
	 * @return the sDocument
	 */
	public SDocument getsDocument() {
		return sDocument;
	}

	/**
	 * @param sDocument the sDocument to set
	 */
	private void setsDocument(SDocument sDocument) {
		this.sDocument = sDocument;
	}

	/**
	 * @return the sCorpus
	 */
	public SCorpus getsCorpus() {
		return sCorpus;
	}

}

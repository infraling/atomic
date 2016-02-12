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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.projects.ProjectNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * Creates a root corpus of type {@link SCorpus}, and adds the relevant nodes
 * to its corpus structure, by iterating recursively through its children.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class CorpusCreationRunnable implements Runnable {
	
	private Map<Thread, Runnable> documentThreads = new HashMap<>();
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "CorpusCreationRunnable".
	 */
	private static final Logger log = LogManager.getLogger(CorpusCreationRunnable.class);
	
	private ProjectNode rootCorpus = null;
	private SCorpusGraph corpusGraph = null;
	private SaltFactory factory = SaltFactory.eINSTANCE;

	/**
	 * 
	 */
	public CorpusCreationRunnable(ProjectNode rootCorpus) {
		setRootCorpus(rootCorpus);
		setCorpusGraph(factory.createSCorpusGraph());
	}

	/* 
	 * @copydoc @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.trace("Creating the corpus graph and corpus structure for the root corpus {}.", rootCorpus.getName());
		getCorpusGraph().setSId("corpus-graph-" + rootCorpus.getName().replaceAll(" ", "_"));
		createCorpusStructure(getRootCorpus(), null);
		/* 
		 * At this point, all corpora exist in the structure, so now create
		 * all documents!
		 */
		if (!getDocumentThreads().isEmpty()) {
			for (Thread thread : getDocumentThreads().keySet()) {
				thread.start();
				try {
					thread.join();
					log.trace("Document thread for document {} has finished.", ((DocumentCreationRunnable) getDocumentThreads().get(thread)).getsDocument().getSName());
				}
				catch (InterruptedException e) {
					log.error("Document creation thread has been interrupted!", e);
				}
			}
			for (Runnable runnable : getDocumentThreads().values()) {
				DocumentCreationRunnable documentRunnable = (DocumentCreationRunnable) runnable;
				getCorpusGraph().addSDocument(documentRunnable.getsCorpus(), documentRunnable.getsDocument()); 
			}
		}
		log.trace("Finished creating the corpus structure for root corpus {}.", getRootCorpus().getName());
	}
	
	/**
	 * Creates a corpus structure via recursive traversal. The root node for the traversal
	 * is the {@link ProjectNode} argument.
	 * <p>
	 * <ol>
	 * <li>Creates an {@link SCorpus} object, sets its ID to corpusData.getName() and adds it to corpusGraph</li>
	 * <li>Traverses corpusData's children:
	 *   <ul>
	 *   <li>If the child is an instance of {@link Document}, call {@link #createDocument(Document)} on it to create
	 *   an instance of {@link SDocument} and add it to corpusGraph.</li>
	 *   <li>If the child is an instance of {@link Corpus}, recursively call {@link #createCorpusStructure(SCorpusGraph, ProjectNode, SCorpus)}, with
	 *   the child as new root node.</li>
	 *   </ul>
	 * </li>
	 * </ol>
	 *
	 * @param corpusGraph The {@link SCorpusGraph} for the nodes root corpus
	 * @param corpusData The traversal {@link ProjectNode} root
	 * @param parentCorpus The {@link SCorpus} to which the corpusData's children will be added
	 */
	private void createCorpusStructure(ProjectNode corpusData, SCorpus parentCorpus) {
		log.entry(corpusData, parentCorpus);
		if (corpusData == null) {
			final NullPointerException e = new NullPointerException("Corpus data is null!");
			throw log.throwing(e);
		}
		else if (getCorpusGraph() == null) {
			final NullPointerException e = new NullPointerException("Corpus graph is null!");
			throw log.throwing(e);
		}

		/* 
		 * Create the corpus in the corpus graph.
		 * At this point, the ProjectNode argument
		 * must be of type Corpus, as the recursion is
		 * only continued if the argument is of that type,
		 * cf. below
		 */
		SCorpus corpus = factory.createSCorpus();
		corpus.setSName(corpusData.getName());
		if (parentCorpus == null) {
			getCorpusGraph().addSNode(corpus);
		}
		else {
			getCorpusGraph().addSSubCorpus(parentCorpus, corpus);
		}

		for (ProjectNode child : corpusData.getChildren().values()) {
			Set<Document> workedDocs = new HashSet<>();
			if (child instanceof Document && !workedDocs.contains(child)) {
				DocumentCreationRunnable runnable = new DocumentCreationRunnable(corpus, (Document) child);
				Thread worker = new Thread(runnable);
				worker.setName("Worker thread for runnable creating structure for root corpus " + rootCorpus.getName());
				getDocumentThreads().put(worker, runnable);
				workedDocs.add((Document) child);
			}
			else if (child instanceof Corpus) {
				createCorpusStructure((Corpus) child, corpus);
			}
			else {
				log.warn("{} is an instance of neither Document nor Corpus...", child);
			}
		}
		log.exit();
	}
	
	/**
	 * @return the rootCorpus
	 */
	private ProjectNode getRootCorpus() {
		return rootCorpus;
	}

	/**
	 * @param rootCorpus the rootCorpus to set
	 */
	private void setRootCorpus(ProjectNode rootCorpus) {
		this.rootCorpus = rootCorpus;
	}

	/**
	 * @return the corpusGraph
	 */
	public SCorpusGraph getCorpusGraph() {
		return corpusGraph;
	}

	/**
	 * @param corpusGraph the corpusGraph to set
	 */
	private void setCorpusGraph(SCorpusGraph corpusGraph) {
		this.corpusGraph = corpusGraph;
	}

	/**
	 * @return the documentThreads
	 */
	private Map<Thread, Runnable> getDocumentThreads() {
		return documentThreads;
	}

}

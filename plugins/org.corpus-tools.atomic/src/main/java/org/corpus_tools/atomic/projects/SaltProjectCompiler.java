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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;

/**
 * Compiles a {@link SaltProject} from a {@link ProjectData} object.
 * This class is meant to be used with Salt version 2.1.1.
 * 
 * @version %I%, %G%
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class SaltProjectCompiler implements ProjectCompiler {

	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "SaltProjectCompiler".
	 */
	private static final Logger log = LogManager.getLogger(SaltProjectCompiler.class);
	
	private ProjectData projectData = null;
	private SaltFactory factory = SaltFactory.eINSTANCE;

	/**
	 * Constructor taking an instance of {@link ProjectData}
	 * and setting the {@link #projectData} field.
	 */
	public SaltProjectCompiler(ProjectData projectData) {
		setProjectData(projectData);
	}

	/**
	 * Runs the compilation task:
	 * <ol>
	 * <li>Create a {@link SaltProject} and assign it the name from {@link #projectData}</li>
	 * <li>For every corpus (i.e., root corpus) in {@link ProjectData#getCorpora()}:
	 *   <ul>
	 *   <li>Start a thread</li>
	 *   <li>Create an {@link SCorpusGraph}, give it an ID, and add it to the {@link SaltProject}</li>
	 *   <li>Call {@link #createCorpusStructure(SCorpusGraph, ProjectNode, SCorpus)} for the root corpus and add it to the {@link SCorpusGraph}</li>
	 *   </ul>
	 * </li>
	 * <li>Add the filled {@link SCorpusGraph} to the {@link SaltProject}</li>
	 * </ol>
	 * <p>
	 * Returns the complete {@link SaltProject}.
	 *
	 * @return the complete {@link SaltProject}
	 */
	@Override
	public Object run() {
		SaltProject project = factory.createSaltProject();
		project.setSName(getProjectData().getName());
		log.trace("Created a SaltProject and set its name to {}.", projectData.getName());

		// Multi-threaded create corpusgraph and add structure for each corpus in getcorpora
		for (ProjectNode rootCorpus : getProjectData().getCorpora().values()) {
			log.trace("Creating the corpus graph and corpus structure for the root corpus {}.", rootCorpus.getName());
			SCorpusGraph corpusGraph = factory.createSCorpusGraph();
			corpusGraph.setSId("corpus-graph-" + rootCorpus.getName().replaceAll(" ", "_"));

			createCorpusStructure(corpusGraph, rootCorpus, null);

			project.getSCorpusGraphs().add(corpusGraph);
			log.trace("Finished creating the corpus structure for root corpus {}.", rootCorpus);
		}
		return project;
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
	private void createCorpusStructure(SCorpusGraph corpusGraph, ProjectNode corpusData, SCorpus parentCorpus) {
		log.entry(corpusGraph, corpusData, parentCorpus);
		if (corpusData == null) {
			final NullPointerException e = new NullPointerException("Corpus data is null!");
			throw log.throwing(e);
		}
		else if (corpusGraph == null) {
			final NullPointerException e = new NullPointerException("Corpus graph is null!");
			throw log.throwing(e);
		}

		// Create the corpus in the corpus graph
		SCorpus corpus = factory.createSCorpus();
		corpus.setSName(corpusData.getName());
		if (parentCorpus == null) {
			corpusGraph.addSNode(corpus);
		}
		else {
			corpusGraph.addSSubCorpus(parentCorpus, corpus);
		}

		// Traverse through children
		for (ProjectNode child : corpusData.getChildren().values()) {
			if (child instanceof Document) {
				corpusGraph.addSDocument(corpus, (SDocument) createDocument((Document) child));
				return;
			}
			else if (child instanceof Corpus) {
				createCorpusStructure(corpusGraph, (Corpus) child, corpus);
			}
			else {
				log.warn("{} is an instance of neither Document nor Corpus...", child);
			}
		}
		log.exit();
	}

	/**
	 * Creates an instance of {@link SDocument} and fills it with 
	 * the data from documentData.
	 *
	 * @param documentData the data for this document
	 * @return a newly created instance of {@link SDocument}, containing the data from documentData
	 */
	private Object createDocument(Document documentData) {
		log.entry(documentData);
		SDocument document = factory.createSDocument();
		document.setSName(documentData.getName());
		document.setSDocumentGraph(factory.createSDocumentGraph());
		STextualDS sourceText = factory.createSTextualDS();
		sourceText.setSText(documentData.getSourceText());
		document.getSDocumentGraph().addSNode(sourceText);
		return log.exit(document);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectCompiler#setProjectData(org.corpus_tools.atomic.projects.ProjectData)
	 */
	@Override
	public void setProjectData(ProjectData projectData) {
		this.projectData = projectData;
	}

	/**
	 * @return the projectData
	 */
	private ProjectData getProjectData() {
		return projectData;
	}

}

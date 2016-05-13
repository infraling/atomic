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
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.ProjectCompiler;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SNode;

/**
 * Compiles a {@link SaltProject} from a {@link Corpus} object.
 * This class is meant to be used with Salt version 2.1.1.
 * 
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
	
	private Corpus projectData = null;

	/**
	 * Constructor taking an instance of {@link ProjectData}
	 * and setting the {@link #projectData} field.
	 */
	public SaltProjectCompiler(Corpus projectData) {
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
	public SaltProject run() {
		SaltProject project = SaltFactory.createSaltProject();
		project.setName(getProjectData().getName());
		log.trace("Created a SaltProject and set its name to {}.", projectData.getName());

		// Multi-threaded create corpusgraph and add structure for each corpus in getcorpora
		Map<Thread, Runnable> threads = new HashMap<>();
		for (ProjectNode rootCorpus : getProjectData().getChildren()) {
			CorpusCreationRunnable runnable = new CorpusCreationRunnable(rootCorpus);
			Thread worker = new Thread(runnable);
			worker.setName("Worker thread for runnable creating structure for root corpus " + rootCorpus.getName());
			worker.start();
			threads.put(worker, runnable);
		}
		// Wait for all threads to finish
		for (Thread thread : threads.keySet()) {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				log.error("The thread processing a root corpus has been interrupted.", e);
			}
		}
		// Add all created SCorpusGraphs to the project
		SCorpusGraph corpusGraph = SaltFactory.createSCorpusGraph();
		corpusGraph.setName("corpus-graph" + project.getName());
		project.addCorpusGraph(corpusGraph);
		for (Runnable runnable : threads.values()) {
			corpusGraph.addNode(((CorpusCreationRunnable) runnable).getRootCorpus());
			List<SCorpus[]> subCorpora = ((CorpusCreationRunnable) runnable).getSubCorpora();
			for (int i = 0; i < subCorpora.size(); i++) {
				corpusGraph.addSubCorpus(subCorpora.get(i)[0], subCorpora.get(i)[1]);
			}
			List<SNode[]> documents = ((CorpusCreationRunnable) runnable).getDocuments();
			for (int i = 0; i < documents.size(); i++) {
				corpusGraph.addDocument(((SCorpus) documents.get(i)[0]), ((SDocument) documents.get(i)[1]));
			}
		}
		return project;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectCompiler#setProjectData(org.corpus_tools.atomic.projects.ProjectData)
	 */
	@Override
	public void setProjectData(Corpus projectData) {
		this.projectData = projectData;
	}

	/**
	 * @return the projectData
	 */
	private Corpus getProjectData() {
		return projectData;
	}

}

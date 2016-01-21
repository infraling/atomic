///*******************************************************************************
// * Copyright 2016 Friedrich-Schiller-Universität Jena
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Contributors:
// *     Stephan Druskat - initial API and implementation
// *******************************************************************************/
//package org.corpus_tools.atomic.projects.impl;
//
//import java.io.File;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.LinkedList;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.corpus_tools.atomic.projects.AtomicProjectData;
//import org.corpus_tools.atomic.projects.ProjectCreator;
//import org.eclipse.emf.common.util.URI;
//
//import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
//
///**
// * TODO Description
// * <p>
// * @author Stephan Druskat <stephan.druskat@uni-jena.de>
// */
//public class DefaultProjectCreator implements ProjectCreator {
//
//	/**
//	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DefaultProjectCreator".
//	 */
//	private static final Logger log = LogManager.getLogger(DefaultProjectCreator.class);
//
//	/*
//	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectCreator#createSingleCorpusProject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	@Override
//	public SaltProject createSingleCorpusProject(String projectName, String corpusName, String documentName, String sourceText) {
//		SaltFactory factory = SaltFactory.eINSTANCE;
//		SaltProject project = factory.createSaltProject();
//		project.setSName(projectName);
//
//		SCorpusGraph corpusGraph = factory.createSCorpusGraph();
//		corpusGraph.setId("corpus graph 1");
//
//		SCorpus corpus = factory.createSCorpus();
//		corpus.setSName(corpusName);
//		corpusGraph.addSNode(corpus);
//
//		SDocument document = factory.createSDocument();
//		document.setSName(documentName);
//		SDocumentGraph documentGraph = factory.createSDocumentGraph();
//		documentGraph.createSTextualDS(sourceText);
//		document.setSDocumentGraph(documentGraph);
//		corpusGraph.addSDocument(corpus, document);
//
//		project.getSCorpusGraphs().add(corpusGraph);
//		return project;
//	}
//
////	/*
////	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectCreator#createMultiCorpusProject(org.corpus_tools.atomic.projects.AtomicProjectData)
////	 */
////	/*
////	 * More than one corpus = 1 root corpus! More than 1 root corpus
////	 */
////	@Override
////	public SaltProject createMultiCorpusProject(AtomicProjectData projectData) {
////		return project;
////	}
//
//	/*
//	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectCreator#saveProject(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject)
//	 */
//	@Override
//	public SaltProject saveProject(SaltProject project, File saveFolder) {
//		URI uri = URI.createFileURI(saveFolder.getAbsolutePath());
//		project.saveSaltProject(uri);
//		if (saveFolder.exists() && saveFolder.isDirectory()) {
//			return project;
//		}
//		else {
//			log.error("Something went wrong trying to save {} to the folder {}", project, saveFolder.getAbsolutePath());
//			return null;
//		}
//	}
//
//}

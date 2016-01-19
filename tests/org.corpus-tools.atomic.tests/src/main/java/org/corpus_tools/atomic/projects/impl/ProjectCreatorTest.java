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
package org.corpus_tools.atomic.projects.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;

/**
 * Unit tests for {@link ProjectCreator}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectCreatorTest {
	
	private ProjectCreator fixture = null;

	/**
	 * Set up fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new ProjectCreator());
	}

//	/**
//	 * TODO: Description
//	 *
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.impl.ProjectCreatorTest#createSingleCorpusProject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateSingleCorpusProject() {
		SaltProject project = getFixture().createSingleCorpusProject("project", "corpus", "document", "source");
		assertEquals("project", project.getSName());
		/* 
		 * Nested for loops with assert okay here since there should only be the one corpus graph with
		 * the one corpus in the project at this point! If the test fails, there is something wrong anyway!
		 */
		for (SCorpusGraph corpusGraph : project.getSCorpusGraphs()) {
			for (SCorpus corpus : corpusGraph.getSCorpora()) {
				assertEquals("corpus", corpus.getSName());
				/*
				 * Similarly, there should only be one document in the one corpus, so test shouldn't fail if everything is okay.
				 */
				for (SDocument document : corpus.getSCorpusGraph().getSDocuments()) {
					assertEquals("document", document.getSName());
					/*
					 * Again, one document, one document data source.
					 */
					for (STextualDS source : document.getSDocumentGraph().getSTextualDSs()) {
						assertEquals("source", source.getSText());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.impl.ProjectCreator#createMultiCorpusProject(org.corpus_tools.atomic.projects.IAtomicProjectData)}.
	 */
	@Test
	public void testCreateMultiCorpusProject() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.impl.ProjectCreator#saveProject(SaltProject)}.
	 */
	@Test
	public void testSaveProject() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * @return the fixture
	 */
	private ProjectCreator getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(ProjectCreator fixture) {
		this.fixture = fixture;
	}

}

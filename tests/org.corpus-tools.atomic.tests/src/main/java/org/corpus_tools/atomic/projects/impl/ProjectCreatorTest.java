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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.corpus_tools.atomic.internal.projects.DefaultAtomicProjectData;
import org.corpus_tools.atomic.projects.IAtomicProjectData;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusDocumentRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * Unit tests for {@link ProjectCreator}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectCreatorTest {
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
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
		testSimpleProject(project);
	}

/**
 * TODO: Description
 *
 * @param project
 */
private void testSimpleProject(SaltProject project) {
	assertEquals("project", project.getSName());
	/* 
	 * Nested for loops with assert okay here since there should only be the one corpus graph with
	 * the one corpus in the project at this point! If the test fails, there is something wrong anyway!
	 */
	assertEquals(1, project.getSCorpusGraphs().size());
	for (SCorpusGraph corpusGraph : project.getSCorpusGraphs()) {
		assertEquals(1, corpusGraph.getSCorpora().size());
		assertEquals(1, corpusGraph.getSDocuments().size());
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
		IAtomicProjectData dummy = createMultiCorpusProject();
		SaltProject project = getFixture().createMultiCorpusProject(dummy);
		assertEquals("project", project.getSName());
		/* There should now be
		 * - 1 corpus graph
		 * - 1 root corpus (!!! When there is mor than one corpus involved, 
		 *   the default case is that there will always be one root corpus and
		 *   the single corpora underneath it !!!)
		 * - 3 corpora with
		 * -- 3 documents each with the
		 * -- respective name and the
		 * -- respective source text
		 */
		assertEquals(1, project.getSCorpusGraphs().size());
		assertNotNull(project.getSCorpusGraphs().get(0));
		SCorpusGraph corpusGraph = project.getSCorpusGraphs().get(0);
		assertEquals(1, corpusGraph.getSRootCorpus());
		assertNotNull(corpusGraph.getSRootCorpus().get(0));
		SCorpus rootCorpus = corpusGraph.getSRootCorpus().get(0);
		// Number of SCorpusRelations going to the 3 subcorpora
		assertEquals(3, rootCorpus.getOutgoingSRelations().size());
		ArrayList<SCorpus> subCorpora = new ArrayList<>();
		for (SRelation relation : rootCorpus.getOutgoingSRelations()) {
			if (relation instanceof SCorpusRelation) {
				SNode target = relation.getSTarget();
				if (target instanceof SCorpus) {
					subCorpora.add((SCorpus) target);
				}
			}
		}
		assertEquals(3, subCorpora.size());
		SortedSet<String> subCorpusDocumentNumbers = new TreeSet<>();
		for (SCorpus subCorpus : subCorpora) {
			subCorpusDocumentNumbers.clear();
			int i = 0;
			for (SCorpusDocumentRelation cDRelation : corpusGraph.getSCorpusDocumentRelations()) {
				if (cDRelation.getSCorpus().equals(subCorpus)) {
					assertEquals(subCorpus.getSName().charAt(1), cDRelation.getSDocument().getSName().charAt(1));
					assertEquals(subCorpus.getSName().charAt(1), cDRelation.getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().charAt(1));
					subCorpusDocumentNumbers.add(String.valueOf(cDRelation.getSDocument().getSName().charAt(3)));
					i++;
				}
			}
			assertEquals(3, i);
			assertTrue(subCorpusDocumentNumbers.containsAll(Arrays.asList(new String[]{"1", "2", "3"})));
		}
	}

	/**
	 * Helper method that creates a multi-corpus project for testing purposes.
	 *
	 * @return the test project
	 */
	private IAtomicProjectData createMultiCorpusProject() {
		IAtomicProjectData project = new DefaultAtomicProjectData("project");
		project.createDocumentAndAddToCorpus("c1", "d1_1", "t1_1");
		project.createDocumentAndAddToCorpus("c1", "d1_2", "t1_2");
		project.createDocumentAndAddToCorpus("c1", "d1_3", "t1_3");

		project.createDocumentAndAddToCorpus("c2", "d2_1", "t2_1");
		project.createDocumentAndAddToCorpus("c2", "d2_2", "t2_2");
		project.createDocumentAndAddToCorpus("c2", "d2_3", "t2_3");

		project.createDocumentAndAddToCorpus("c3", "d3_1", "t3_1");
		project.createDocumentAndAddToCorpus("c3", "d3_2", "t3_2");
		project.createDocumentAndAddToCorpus("c3", "d3_3", "t3_3");
		
		assertNotNull(project);
		
		return project;
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.impl.ProjectCreator#saveProject(SaltProject)}.
	 */
	@Test
	public void testSaveProject() {
		SaltProject project = getFixture().createSingleCorpusProject("project", "corpus", "document", "source");
		assertTrue(getFixture().saveProject(project, folder.getRoot()));
		SaltProject loadedProject = SaltFactory.eINSTANCE.createSaltProject();
		loadedProject.loadSaltProject(URI.createFileURI(folder.getRoot().getAbsolutePath()));
		testSimpleProject(loadedProject);
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

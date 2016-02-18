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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.corpus_tools.atomic.internal.projects.DefaultProjectData;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.projects.salt.SaltProjectCompiler;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * Unit test for {@link SaltProjectCompiler}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class SaltProjectCompilerTest {
	
	private SaltProjectCompiler fixture = null;

	/**
	 * Set up the fixture and a dummy {@link ProjectData} object (FIXME: Mock this!).
	 * Structure as follows:
	 * 
	 * PROJECT (project)
	 * 
	 *   - c1
	 *     - d1:t1
	 *     - d2:t2
	 *   - c2
	 *     - c21
	 *       - d211:t211
	 *       - d212:t212
	 *     - c22
	 *       - c221
	 *         - d221:t221
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		DefaultProjectData data = new DefaultProjectData("project");
		Corpus c1 = new Corpus();
		c1.setName("c1");
		  Document d1 = new Document();
		  d1.setName("d1");
		  d1.setSourceText("t1");
		  Document d2 = new Document();
		  d2.setName("d2");
		  d2.setSourceText("t2");
		  c1.addChild(d1);
		  c1.addChild(d2);
		Corpus c2 = new Corpus();
		c2.setName("c2");
		  Corpus c21 = new Corpus();
		  c21.setName("c21");
		    Document d211 = new Document();
		    d211.setName("d211");
		    d211.setSourceText("t211");
		    Document d212 = new Document();
		    d212.setName("d212");
		    d212.setSourceText("t212");
		  c21.addChild(d211);
		  c21.addChild(d212);
		c2.addChild(c21);
		  Corpus c22 = new Corpus();
		  c22.setName("c22");
		    Corpus c221 = new Corpus();
		    c221.setName("c221");
		      Document d221 = new Document();
		      d221.setName("d221");
		      d221.setSourceText("t221");
		    c221.addChild(d221);
		  c22.addChild(c221);
		c2.addChild(c22);
		data.addCorpus(c1);
		data.addCorpus(c2);
		
		setFixture(new SaltProjectCompiler(data));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.SaltProjectCompiler#run()}.
	 */
	@Test
	public void testRun() {
		Object retVal = getFixture().run();
		assertTrue(retVal instanceof SaltProject);
		SaltProject project = (SaltProject) retVal;
		assertEquals(2, project.getSCorpusGraphs().size());
		// Assert that one corpus graph includes 4 corpora, the other has 1
		SCorpusGraph corpusGraphC1 = null;
		SCorpusGraph corpusGraphC2 = null;
		for (SCorpusGraph corpusGraph : project.getSCorpusGraphs()) {
			if (corpusGraph.getSCorpora().size() == 1) {
				corpusGraphC1 = corpusGraph;
			}
			else {
				corpusGraphC2 = corpusGraph;
			}
		}
		assertEquals(1, corpusGraphC1.getSCorpora().size());
		assertEquals(4, corpusGraphC2.getSCorpora().size());
		// Assert that the corpus names are correct
		assertEquals("c1", corpusGraphC1.getSCorpora().get(0).getSName());
		List<String> corpusNamesC2 = new ArrayList<>();
		for (SCorpus corpus : corpusGraphC2.getSCorpora()) {
			corpusNamesC2.add(corpus.getSName());
		}
		Collections.sort(corpusNamesC2);
		assertEquals("c2", corpusNamesC2.get(0));
		assertEquals("c21", corpusNamesC2.get(1));
		assertEquals("c22", corpusNamesC2.get(2));
		assertEquals("c221", corpusNamesC2.get(3));
		// Assert that the corpora contain the right number of documents and right documents
		assertEquals(2, corpusGraphC1.getSDocuments().size());
		SDocument[] documentsC1 = new SDocument[2];
		for (SDocument document : corpusGraphC1.getSDocuments()) {
			documentsC1[Integer.parseInt(document.getSName().substring(1)) - 1] = document;
		}
		assertEquals("d1", documentsC1[0].getSName());
		assertEquals("d2", documentsC1[1].getSName());
		assertEquals("t1", documentsC1[0].getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t2", documentsC1[1].getSDocumentGraph().getSTextualDSs().get(0).getSText());
		
		assertEquals(3, corpusGraphC2.getSDocuments().size());
		Map<String, SDocument> documentsC2Map = new HashMap<>();
		for (SDocument document : corpusGraphC2.getSDocuments()) {
			documentsC2Map.put(document.getSName(), document);
		}
		assertEquals(3, documentsC2Map.size());
		assertTrue(documentsC2Map.containsKey("d211"));
		assertTrue(documentsC2Map.containsKey("d212"));
		assertTrue(documentsC2Map.containsKey("d221"));
		assertEquals("t211", documentsC2Map.get("d211").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t212", documentsC2Map.get("d212").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t221", documentsC2Map.get("d221").getSDocumentGraph().getSTextualDSs().get(0).getSText());
	}

	/**
	 * @return the fixture
	 */
	private SaltProjectCompiler getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(SaltProjectCompiler fixture) {
		this.fixture = fixture;
	}

}

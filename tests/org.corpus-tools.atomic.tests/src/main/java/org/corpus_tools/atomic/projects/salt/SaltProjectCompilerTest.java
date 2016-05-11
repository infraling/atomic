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
		Corpus data = new Corpus();
		data.setName("project");
		data.setProjectDataObject(true);
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
		data.addChild(c1);
		data.addChild(c2);
		
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
		assertEquals(1, project.getSCorpusGraphs().size());
		SCorpusGraph corpusGraph = project.getSCorpusGraphs().get(0);
		assertEquals(5, corpusGraph.getSCorpora().size());
		List<String> corpusNames = new ArrayList<>();
		for (SCorpus corpus : corpusGraph.getSCorpora()) {
			corpusNames.add(corpus.getSName());
		}
		Collections.sort(corpusNames);
		assertEquals("c1", corpusNames.get(0));
		assertEquals("c2", corpusNames.get(1));
		assertEquals("c21", corpusNames.get(2));
		assertEquals("c22", corpusNames.get(3));
		assertEquals("c221", corpusNames.get(4));
		// Assert that the corpora contain the right number of documents and right documents
		assertEquals(5, corpusGraph.getSDocuments().size());
		Map<String, SDocument> documentsMap = new HashMap<>();
		for (SDocument document : corpusGraph.getSDocuments()) {
			documentsMap.put(document.getSName(), document);
		}
		assertEquals(5, documentsMap.size());
		assertTrue(documentsMap.containsKey("d1"));
		assertTrue(documentsMap.containsKey("d2"));
		assertTrue(documentsMap.containsKey("d211"));
		assertTrue(documentsMap.containsKey("d212"));
		assertTrue(documentsMap.containsKey("d221"));
		assertEquals("t1", documentsMap.get("d1").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t2", documentsMap.get("d2").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t211", documentsMap.get("d211").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t212", documentsMap.get("d212").getSDocumentGraph().getSTextualDSs().get(0).getSText());
		assertEquals("t221", documentsMap.get("d221").getSDocumentGraph().getSTextualDSs().get(0).getSText());
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

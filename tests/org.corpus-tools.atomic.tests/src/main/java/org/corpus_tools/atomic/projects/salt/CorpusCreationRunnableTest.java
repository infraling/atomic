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

import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * Unit tests for {@link CorpusCreationRunnable}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class CorpusCreationRunnableTest {
	
	CorpusCreationRunnable fixture = null;

	/**
	 * Set up the fixture and a dummy {@link Corpus} object (FIXME: Mock this!).
	 * Structure as follows:
	 * 
	 * CorpusGraph1
	 * 	C1
	 *  	C12
	 *  	C13
	 * 		
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Corpus c1 = new Corpus("c1");
		c1.addChild(new Corpus("c12"));
		c1.addChild(new Corpus("c13"));
		setFixture(new CorpusCreationRunnable(c1));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.salt.CorpusCreationRunnable#run()}.
	 */
	@Test
	public void testRun() {
		getFixture().run();
		SCorpusGraph corpusGraph = getFixture().getCorpusGraph();
		assertNotNull(corpusGraph);
		assertEquals(3, corpusGraph.getSCorpora().size());
		assertNotNull(corpusGraph.getSRootCorpus());
		assertEquals(1, corpusGraph.getSRootCorpus().size());
		assertEquals("c1", corpusGraph.getSRootCorpus().get(0).getSName());
		assertEquals(2, corpusGraph.getSRootCorpus().get(0).getOutgoingSRelations().size());
		ArrayList<String> subCorpusNames = new ArrayList<>();
		for (SRelation outgoing : corpusGraph.getSRootCorpus().get(0).getOutgoingSRelations()) {
			assertTrue(outgoing instanceof SCorpusRelation);
			assertTrue(outgoing.getSTarget() instanceof SCorpus);
			subCorpusNames.add(outgoing.getSTarget().getSName());
		}
		Collections.sort(subCorpusNames);
		assertEquals("c12", subCorpusNames.get(0));
		assertEquals("c13", subCorpusNames.get(1));
	}

	/**
	 * @return the fixture
	 */
	private CorpusCreationRunnable getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(CorpusCreationRunnable fixture) {
		this.fixture = fixture;
	}

}

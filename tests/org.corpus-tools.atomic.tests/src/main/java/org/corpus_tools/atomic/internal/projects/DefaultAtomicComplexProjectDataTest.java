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
package org.corpus_tools.atomic.internal.projects;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicComplexProjectDataTest {
	
	private DefaultAtomicComplexProjectData fixture = null;

	/**
	 * Set fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new DefaultAtomicComplexProjectData("project"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicComplexProjectData#getRootCorpora()}.
	 */
	@Test
	public void testGetRootCorporaForSingleDeafultRootCorpus() {
		// 1 default root corpus
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc1", "d1", "t1");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc2", "d2", "t2");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc3", "d3", "t3");
		assertNotNull(getFixture().getRootCorpora());
	}
	
	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicComplexProjectData#getRootCorpora()}.
	 */
	@Test
	public void testGetRootCorporaForMultipleRootCorpora() {
		// Root corpus 1
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_1", "d1_1_1", "t1_1_1");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_1", "d1_1_2", "t1_1_2");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_1", "d1_1_3", "t1_1_3");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_2", "d1_2_1", "t1_2_1");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_2", "d1_2_2", "t1_2_2");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_2", "d1_2_3", "t1_2_3");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_3", "d1_3_1", "t1_3_1");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_3", "d1_3_2", "t1_3_2");
		getFixture().createDocumentAndAddToSubCorpus("r1", "s1_3", "d1_3_3", "t1_3_3");
		// Root corpus 2
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_1", "d2_1_1", "t2_1_1");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_1", "d2_1_2", "t2_1_2");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_1", "d2_1_3", "t2_1_3");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_2", "d2_2_1", "t2_2_1");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_2", "d2_2_2", "t2_2_2");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_2", "d2_2_3", "t2_2_3");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_3", "d2_3_1", "t2_3_1");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_3", "d2_3_2", "t2_3_2");
		getFixture().createDocumentAndAddToSubCorpus("r2", "s2_3", "d2_3_3", "t2_3_3");
		// Root corpus 3
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_1", "d3_1_1", "t3_1_1");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_1", "d3_1_2", "t3_1_2");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_1", "d3_1_3", "t3_1_3");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_2", "d3_2_1", "t3_2_1");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_2", "d3_2_2", "t3_2_2");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_2", "d3_2_3", "t3_2_3");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_3", "d3_3_1", "t3_3_1");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_3", "d3_3_2", "t3_3_2");
		getFixture().createDocumentAndAddToSubCorpus("r3", "s3_3", "d3_3_3", "t3_3_3");
		assertNotNull(getFixture().getRootCorpora());
		assertEquals(3, getFixture().getRootCorpora().size());
		assertTrue(getFixture().getRootCorpora().containsKey("r1"));
		assertTrue(getFixture().getRootCorpora().containsKey("r2"));
		assertTrue(getFixture().getRootCorpora().containsKey("r3"));
		assertEquals(3, getFixture().getRootCorpora().get("r1").size());
		assertEquals(3, getFixture().getRootCorpora().get("r2").size());
		assertEquals(3, getFixture().getRootCorpora().get("r3").size());
	}
	
	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicComplexProjectData#getRootCorpora()}.
	 */
	@Test
	public void testGetSubCorpora() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicComplexProjectData#createDocumentAndAddToSingleRootSubCorpus(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDocumentAndAddToSingleRootSubCorpus() {
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc1", "d1_1", "t1_1");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc1", "d1_2", "t1_2");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc1", "d1_3", "t1_3");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc2", "d2_1", "t2_1");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc2", "d2_2", "t2_2");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc2", "d2_3", "t2_3");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc3", "d3_1", "t3_1");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc3", "d3_2", "t3_2");
		getFixture().createDocumentAndAddToSingleRootSubCorpus("sc3", "d3_3", "t3_3");
		assertEquals(1, getFixture().getRootCorpora().size());
		assertEquals(3, getFixture().getRootCorpora().values().size());
		assertNotNull(getFixture().getRootCorpora().get(getFixture().getProjectName() + " root corpus"));
		LinkedHashSet<String> subCorpora = getFixture().getRootCorpora().get(getFixture().getProjectName() + " root corpus");
		Iterator<String> subCorpusIterator = subCorpora.iterator();
		int i = 0;
		while (subCorpusIterator.hasNext()) {
			i++;
			String subCorpus = (String) subCorpusIterator.next();
			assertEquals("sc" + String.valueOf(i), subCorpus);
			LinkedHashSet<Pair<String, String>> documents = getFixture().getSubCorpora().get(subCorpus);
			Iterator<Pair<String, String>> documentsIterator = documents.iterator();
			int j = 0;
			while (documentsIterator.hasNext()) {
				j++;
				Pair<String, String> document = (Pair<String, String>) documentsIterator.next();
				assertEquals("d" + String.valueOf(i) + "_" + String.valueOf(j), document.getLeft());
				assertEquals("t" + String.valueOf(i) + "_" + String.valueOf(j), document.getRight());
			}
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicComplexProjectData#createDocumentAndAddToSubCorpus(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDocumentAndAddToSubCorpus() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * @return the fixture
	 */
	private DefaultAtomicComplexProjectData getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(DefaultAtomicComplexProjectData fixture) {
		this.fixture = fixture;
	}

}

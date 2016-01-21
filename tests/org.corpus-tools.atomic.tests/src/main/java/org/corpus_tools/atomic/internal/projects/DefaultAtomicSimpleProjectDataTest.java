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

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultAtomicSimpleProjectData}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicSimpleProjectDataTest {
	
	private DefaultAtomicSimpleProjectData fixture = null;

	/**
	 * Set up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new DefaultAtomicSimpleProjectData("project", "corpus"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicSimpleProjectData#createDocumentAndAddToCorpus(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDocumentAndAddToCorpus() {
		assertNotNull(getFixture().getCorpus());
		getFixture().createDocumentAndAddToCorpus("d1", "t1");
		getFixture().createDocumentAndAddToCorpus("d2", "t2");
		getFixture().createDocumentAndAddToCorpus("d3", "t3");
		assertEquals("project", getFixture().getProjectName());
		assertNotNull(getFixture().getCorpus());
		assertEquals("corpus", getFixture().getCorpus().getLeft());
		assertEquals(3, getFixture().getCorpus().getRight().size());
		int i = 0;
		Iterator<Pair<String, String>> iterator = getFixture().getCorpus().getRight().iterator();
		while (iterator.hasNext()) {
			i++;
			Pair<java.lang.String, java.lang.String> document = (Pair<java.lang.String, java.lang.String>) iterator.next();
			assertNotNull(document.getLeft());
			assertNotNull(document.getRight());
			assertEquals("d" + i, document.getLeft());
			assertEquals("t" + i, document.getRight());
			assertEquals(String.valueOf(i), document.getLeft().substring(1));
			assertEquals(String.valueOf(i), document.getRight().substring(1));
			assertEquals(document.getLeft().substring(1), document.getRight().substring(1));
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicProjectData#getProjectName()}.
	 */
	@Test
	public void testGetProjectName() {
		assertEquals("project", getFixture().getProjectName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultAtomicProjectData#replaceDocumentSourceText(org.apache.commons.lang3.tuple.Pair, java.lang.String)}.
	 */
	@Test
	public void testReplaceDocumentSourceText() {
		getFixture().createDocumentAndAddToCorpus("d", "t");
		Pair<String, String> documentUnderTest = null;
		for (Pair<String, String> document : getFixture().getCorpus().getRight()) {
			if (document.getLeft().equals("d")) {
				assertTrue(document.getRight().equals("t"));
				documentUnderTest = document;
			}
		}
		getFixture().replaceDocumentSourceText(documentUnderTest, "t");
		for (Pair<String, String> document : getFixture().getCorpus().getRight()) {
			if (document.getLeft().equals("d")) {
				assertTrue(document.getRight().equals("t"));
			}
		}
		getFixture().replaceDocumentSourceText(documentUnderTest, "t-replacement");
		for (Pair<String, String> document : getFixture().getCorpus().getRight()) {
			if (document.getLeft().equals("d")) {
				assertTrue(document.getRight().equals("t-replacement"));
			}
		}
	}
	
	@Test
	public void testDocumentInsertionOrder() {
		DefaultAtomicSimpleProjectData fixture2 = new DefaultAtomicSimpleProjectData("Test 2", "c");
		fixture2.createDocumentAndAddToCorpus("d1", "1");
		fixture2.createDocumentAndAddToCorpus("d2", "2");
		fixture2.createDocumentAndAddToCorpus("d3", "3");
		fixture2.createDocumentAndAddToCorpus("d4", "4");
		fixture2.createDocumentAndAddToCorpus("d5", "5");
		LinkedHashSet<Pair<String, String>> corpus = fixture2.getCorpus().getRight();
		assertNotNull(corpus);
		int i = 0;
		for (Pair<String, String> d : corpus) {
			i++;
			assertEquals(String.valueOf(i), d.getRight());
		}
		fixture2.createDocumentAndAddToCorpus("d4", "4");
		fixture2.createDocumentAndAddToCorpus("d2", "2");
		fixture2.createDocumentAndAddToCorpus("d3", "3");
		fixture2.createDocumentAndAddToCorpus("d4", "4");
		i = 0;
		for (Pair<String, String> d : corpus) {
			i++;
			assertEquals(String.valueOf(i), d.getRight());
		}
	}


	/**
	 * @return the fixture
	 */
	private DefaultAtomicSimpleProjectData getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(DefaultAtomicSimpleProjectData fixture) {
		this.fixture = fixture;
	}

}

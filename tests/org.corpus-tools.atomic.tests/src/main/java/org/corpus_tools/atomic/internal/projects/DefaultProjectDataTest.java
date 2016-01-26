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
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.HashMap;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultProjectData}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultProjectDataTest {
	
	private DefaultProjectData fixture = null;

	/**
	 * Set up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new DefaultProjectData("project"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultProjectData#DefaultProjectData(java.lang.String)}.
	 */
	@Test
	public void testDefaultProjectData() {
		assertNotNull(getFixture().getName());
		assertEquals("project", getFixture().getName());
		assertNotNull(getFixture().getCorpora());
		assertEquals(0, getFixture().getCorpora().size());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultProjectData#getName()}.
	 */
	@Test
	public void testGetName() {
		assertNotNull(getFixture().getName());
		assertEquals("project", getFixture().getName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultProjectData#getCorpora()}.
	 */
	@Test
	public void testGetCorpora() {
		assertNotNull(getFixture().getCorpora());
		assertEquals(0, getFixture().getCorpora().size());
		getFixture().addCorpus(new Corpus("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		getFixture().addCorpus(new Corpus("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		getFixture().addCorpus(new Corpus("c2"));
		assertEquals(2, getFixture().getCorpora().size());
		assertThat(getFixture().getCorpora(), instanceOf(HashMap.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultProjectData#addCorpus(org.corpus_tools.atomic.projects.ProjectNode)}.
	 */
	@Test
	public void testAddCorpus() {
		getFixture().addCorpus(new Corpus("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		assertNotNull(getFixture().getCorpora().entrySet());
		assertTrue(getFixture().getCorpora().containsKey("c1"));
		assertNotNull(getFixture().getCorpora().get("c1"));
		assertThat(getFixture().getCorpora().get("c1"), instanceOf(Corpus.class));
		ProjectNode oldC1 = getFixture().getCorpora().get("c1");
		getFixture().addCorpus(new Corpus("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		assertTrue(getFixture().getCorpora().containsKey("c1"));
		assertFalse(getFixture().getCorpora().containsKey("c2"));
		assertNotNull(getFixture().getCorpora().get("c1"));
		assertThat(getFixture().getCorpora().get("c1"), instanceOf(Corpus.class));
		assertNotEquals(oldC1, getFixture().getCorpora().get("c1"));
		getFixture().addCorpus(new Corpus("c2"));
		assertEquals(2, getFixture().getCorpora().size());
		assertTrue(getFixture().getCorpora().containsKey("c2"));
		assertTrue(getFixture().getCorpora().containsKey("c1"));
		assertFalse(getFixture().getCorpora().containsKey("c3"));
		assertNotNull(getFixture().getCorpora().get("c2"));
		assertThat(getFixture().getCorpora().get("c2"), instanceOf(Corpus.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.DefaultProjectData#removeCorpus(org.corpus_tools.atomic.projects.ProjectNode)}.
	 */
	@Test
	public void testRemoveCorpus() {
		assertNull(getFixture().getCorpora().remove("c1"));
		getFixture().addCorpus(new Corpus("c1"));
		getFixture().addCorpus(new Corpus("c2"));
		assertNotNull(getFixture().getCorpora().get("c1"));
		assertNotNull(getFixture().getCorpora().get("c2"));
		assertEquals(2, getFixture().getCorpora().size());
		assertEquals("c1", getFixture().getCorpora().remove("c1").getName());
		assertNull(getFixture().getCorpora().get("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		assertTrue(getFixture().getCorpora().containsKey("c2"));
		assertNull(getFixture().getCorpora().remove("c1"));
		assertNull(getFixture().getCorpora().get("c1"));
		assertEquals(1, getFixture().getCorpora().size());
		assertEquals("c2", getFixture().getCorpora().remove("c2").getName());
		assertNull(getFixture().getCorpora().get("c2"));
		assertEquals(0, getFixture().getCorpora().size());
		assertFalse(getFixture().getCorpora().containsKey("c2"));
		assertNull(getFixture().getCorpora().remove("c2"));
		assertNull(getFixture().getCorpora().get("c2"));
	}

	/**
	 * @return the fixture
	 */
	private DefaultProjectData getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(DefaultProjectData fixture) {
		this.fixture = fixture;
	}

}

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
package org.corpus_tools.atomic.projects;

import static org.junit.Assert.*; 

import java.util.Iterator;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;

import com.codebox.bean.JavaBeanTester;

/**
 * Unit tests for {@link Corpus}.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class CorpusTest {

	private Corpus fixture = null;

	/**
	 * Sets the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Corpus c = new Corpus();
		c.setName("corpus");
		setFixture(c);
	}

	/**
	 * Test method for {@link<String, String>.of("d1", "t3") org.corpus_tools.atomic.projects.Corpus#Corpus(java.lang.String)}.
	 */
	@Test
	public void testCorpus() {
		assertNotNull(getFixture());
		assertEquals("corpus", getFixture().getName());
		assertNotNull(getFixture().getChildren());
		assertEquals(0, getFixture().getChildren().size());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Corpus#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		Corpus c1 = new Corpus();
		c1.setName("c1");
		getFixture().addChild(c1);
		Document d1 = new Document();
		d1.setName("d1");
		d1.setSourceText("t1");
		getFixture().addChild(d1);
		Corpus c2 = new Corpus();
		c2.setName("c2");
		getFixture().addChild(c2);
		Document d2 = new Document();
		d2.setName("d2");
		d2.setSourceText("t2");
		getFixture().addChild(d2);
		getFixture().addChild(c1);
		Document d1New = new Document();
		d1New.setName("d1");
		d1New.setSourceText("t3");
		getFixture().addChild(d1New);
		// Should have 4 children now, in the order c1, d1, c2, d2
		assertNotNull(getFixture().getChildren());
		assertEquals(4, getFixture().getChildren().size());
		assertTrue(getFixture().getChildren().containsKey("c1"));
		assertTrue(getFixture().getChildren().containsKey("c2"));
		assertTrue(getFixture().getChildren().containsKey("d1"));
		assertTrue(getFixture().getChildren().containsKey("d2"));
		Iterator<String> keys = getFixture().getChildren().keySet().iterator();
		String[] keysArray = new String[] { "c1", "d1", "c2", "d2" }; // This should be the correct order in getChildren()
		int i = 0;
		while (keys.hasNext()) {
			String name = (String) keys.next();
			assertEquals(keysArray[i], name);
			i++;
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Corpus#addChild(org.corpus_tools.atomic.projects.ProjectNode)}.
	 */
	@Test
	public void testAddChild() {
		Corpus c1 = new Corpus();
		c1.setName("c1");
		Corpus c2 = new Corpus();
		c2.setName("c2");
		Document d1 = new Document();
		d1.setName("d1");
		d1.setSourceText("t1");
		Document d2 = new Document();
		d2.setName("d2");
		d2.setSourceText("t2");
		assertEquals(c1, getFixture().addChild(c1));
		assertEquals(d1, getFixture().addChild(d1));
		assertEquals(c2, getFixture().addChild(c2));
		assertEquals(d2, getFixture().addChild(d2));
		Corpus c1New = new Corpus();
		c1New.setName("c1");
		getFixture().addChild(c1New);
		Document d1New = new Document();
		d1New.setName("d1");
		d1New.setSourceText("t3");
		getFixture().addChild(d1New);
		// Should have 4 children now, in the order c1, d1, c2, d2
		assertNotNull(getFixture().getChildren());
		assertEquals(4, getFixture().getChildren().size());
		assertTrue(getFixture().getChildren().containsKey("c1"));
		assertTrue(getFixture().getChildren().containsKey("c2"));
		assertTrue(getFixture().getChildren().containsKey("d1"));
		assertTrue(getFixture().getChildren().containsKey("d2"));
		assertNotEquals("t1", getFixture().getChildren().get("d1"));
		assertEquals(new MutablePair<String, String>("d1", "t3"), getFixture().getChildren().get("d1"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Corpus#removeChild(java.lang.String)}.
	 */
	@Test
	public void testRemoveChild() {
		Corpus c1 = new Corpus();
		c1.setName("c1");
		getFixture().addChild(c1);
		Document d1 = new Document();
		d1.setName("d1");
		d1.setSourceText("t1");
		getFixture().addChild(d1);
		Corpus c2 = new Corpus();
		c2.setName("c2");
		getFixture().addChild(c2);
		Corpus c3 = new Corpus();
		c3.setName("c3");
		getFixture().addChild(c3);
		Document d2 = new Document();
		d2.setName("d2");
		d2.setSourceText("t2");
		getFixture().addChild(d2);
		String[] keysArray = new String[] { "c1", "d1", "c2", "c3", "d2" }; // The original order in getChildren()
		Iterator<String> keys = getFixture().getChildren().keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String name = (String) keys.next();
			assertEquals(keysArray[i], name);
			i++;
		}
		// Remove last element
		Document d2New = new Document();
		d2New.setName("d2");
		d2New.setSourceText("t2");
		assertEquals(d2New, getFixture().removeChild("d2"));
		assertEquals(4, getFixture().getChildren().size());
		assertFalse(getFixture().getChildren().containsKey("d2"));
		assertNull(getFixture().getChildren().get("d2"));
		keysArray = new String[] { "c1", "d1", "c2", "c3" }; // sans d2
		keys = getFixture().getChildren().keySet().iterator();
		i = 0;
		while (keys.hasNext()) {
			String name = (String) keys.next();
			assertEquals(keysArray[i], name);
			i++;
		}
		// Remove first element
		assertEquals("c1", getFixture().removeChild("c1").getName());
		assertEquals(3, getFixture().getChildren().size());
		assertFalse(getFixture().getChildren().containsKey("c1"));
		assertNull(getFixture().getChildren().get("c1"));
		keysArray = new String[] { "d1", "c2", "c3" }; // sans d2
		keys = getFixture().getChildren().keySet().iterator();
		i = 0;
		while (keys.hasNext()) {
			String name = (String) keys.next();
			assertEquals(keysArray[i], name);
			i++;
		}
		// Remove middle element
		assertEquals("c2", getFixture().removeChild("c2").getName());
		assertEquals(2, getFixture().getChildren().size());
		assertFalse(getFixture().getChildren().containsKey("c2"));
		assertNull(getFixture().getChildren().get("c2"));
		keysArray = new String[] { "d1", "c3" }; // sans d2
		keys = getFixture().getChildren().keySet().iterator();
		i = 0;
		while (keys.hasNext()) {
			String name = (String) keys.next();
			assertEquals(keysArray[i], name);
			i++;
		}
		// Remove last element
		assertEquals("c3", getFixture().removeChild("c3").getName());
		assertEquals(1, getFixture().getChildren().size());
		assertFalse(getFixture().getChildren().containsKey("c3"));
		assertNull(getFixture().getChildren().get("c3"));
		assertEquals(1, getFixture().getChildren().size());
		assertTrue(getFixture().getChildren().containsKey("d1"));
		// Remove last remaining element
		Document d1New = new Document();
		d1New.setName("d1");
		d1New.setSourceText("t1");
		assertEquals(d1New, getFixture().removeChild("d1"));
		assertEquals(0, getFixture().getChildren().size());
		assertNotNull(getFixture().getChildren());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Corpus#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("corpus", getFixture().getName());
		assertEquals("korpus", getFixture().getName());
	}

	@Test
	public void testBeanFeatures() {
		Corpus c = new Corpus();
		c.setName("corpus");
		JavaBeanTester.builder(Corpus.class).loadData().testInstance(getFixture());
		JavaBeanTester.builder(Corpus.class).loadData().testEquals(getFixture(), c);
	}

	/**
	 * @return the fixture
	 */
	private Corpus getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(Corpus fixture) {
		this.fixture = fixture;
	}

}

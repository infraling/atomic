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

import org.junit.Before;
import org.junit.Test;

import com.codebox.bean.JavaBeanTester;

/**
 * Unit test for {@link Document}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DocumentTest {
	
	private Document fixture = null;

	/**
	 * Set the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Document d = new Document();
		d.setName("document");
		d.setSourceText("text");
		setFixture(d);
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#Document(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDocument() {
		assertNotNull(getFixture());
		Document d = new Document();
		d.setName("document");
		d.setSourceText("text");
		assertEquals(d.getName(), getFixture().getName());
		assertEquals(d.getSourceText(), getFixture().getSourceText());
		assertNull(getFixture().getChildren());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("document", getFixture().getName());
		getFixture().setName("dokument");
		assertEquals("dokument", getFixture().getName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		assertNull(getFixture().getChildren());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#addChild(org.corpus_tools.atomic.projects.ProjectNode)}.
	 */
	@Test
	public void testAddChild() {
		assertNull(getFixture().addChild(new Corpus()));
		assertNull(getFixture().addChild(new Document()));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#removeChild(java.lang.String)}.
	 */
	@Test
	public void testRemoveChild() {
		assertNull(getFixture().removeChild("x"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#getSourceText()}.
	 */
	@Test
	public void testGetSourceText() {
		assertEquals("text", getFixture().getSourceText());
	}

	@Test
	public void testBeanFeatures() {
		Document d = new Document();
		d.setName("document");
		d.setSourceText("text");
		JavaBeanTester.builder(Document.class).loadData().testInstance(getFixture());
		JavaBeanTester.builder(Document.class).loadData().testEquals(getFixture(), d);
	}

	/**
	 * @return the fixture
	 */
	private Document getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(Document fixture) {
		this.fixture = fixture;
	}

}

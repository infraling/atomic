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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.attribute.DocAttributeSet;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;

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
		setFixture(new Document("document", "text"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#Document(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDocument() {
		assertNotNull(getFixture());
		assertEquals(new MutablePair<String, String>("document", "text"), getFixture());
		assertNull(getFixture().getChildren());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("document", getFixture().getName());
		assertEquals("document", getFixture().setName("dokument"));
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
		assertNull(getFixture().addChild(new Corpus("c1")));
		assertNull(getFixture().addChild(new Document("d1", "t1")));
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

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#setSourceText(java.lang.String)}.
	 */
	@Test
	public void testSetSourceText() {
		assertEquals("text", getFixture().setSourceText("text2"));
		assertEquals("text2", getFixture().setSourceText("text2"));
		assertEquals("text2", getFixture().setSourceText("text"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.Document#setName(java.lang.String)}.
	 */
	@Test
	public void testSetName() {
		assertEquals("document", getFixture().setName("dokument"));
		assertEquals("dokument", getFixture().setName("dokument"));
		assertEquals("dokument", getFixture().setName("document"));
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

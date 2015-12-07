/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
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
package org.corpus_tools.atomic.tests;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.corpus_tools.atomic.projects.internal.DefaultAtomicProjectData;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicProjectDataTest {
	
	private DefaultAtomicProjectData fixture = null;

	/**
	 * TODO: Description
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new DefaultAtomicProjectData("Test"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.internal.DefaultAtomicProjectData#createDocumentAndAddToCorpus(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDocumentAndAddToCorpus() {
		// Empty corpus list
		assertEquals(0, (getFixture().getCorpora().size()));

		
		//////////////////////////
		// FIXME: Fleißarbeit: das Ganze noch für zweites Korpus
		// Noch checken, ob erwartetes eintrifft wenn das gleiche dokument nochmal eingefügt wird
		
		///////////////////////////////////////
		// Wenn diese Klasse von einem ANDEREN Objekt (bspw. Wizard) benutzt wird und man die Daten
		// dort modellieren will, benutze ein Mockup-Framework. Denn: wenn diese Tests fehlschlagen, 
		// sollen die Wizard-Tests nicht fehlschlagen
		
		// Add one document to corpus 1
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document", "test-source-text");
		assertEquals(1, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus"));
		assertEquals(1, getFixture().getCorpora().get("test-corpus").size());
		assertEquals("test-document", getFixture().getCorpora().get("test-corpus").iterator().next().getKey());
		assertEquals("test-source-text", getFixture().getCorpora().get("test-corpus").iterator().next().getValue());
		// Add second document to corpus 1
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document-two", "test-source-text-two");
		assertEquals(1, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus"));
		assertEquals(2, getFixture().getCorpora().get("test-corpus").size());
		boolean corpusContainsDocument = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus")) {
			if (document.getKey().equals("test-document-two")) {
				corpusContainsDocument = true;
				assertEquals("test-source-text-two", document.getValue());
			}
		}
		assertTrue(corpusContainsDocument);
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.internal.DefaultAtomicProjectData#getProjectName()}.
	 */
	@Test
	public void testGetProjectName() {
		assertEquals("Test", getFixture().getProjectName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.internal.DefaultAtomicProjectData#getCorpora()}.
	 */
	@Test
	public void testGetCorpora() {
		assertNotNull(getFixture().getCorpora());
	}

	/**
	 * @return the fixture
	 */
	private DefaultAtomicProjectData getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(DefaultAtomicProjectData fixture) {
		this.fixture = fixture;
	}

}

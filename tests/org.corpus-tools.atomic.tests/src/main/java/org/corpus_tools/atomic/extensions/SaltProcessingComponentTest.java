/*******************************************************************************
 * Copyright 2016 Stephan Druskat
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
package org.corpus_tools.atomic.extensions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

/**
 * Unit tests for {@link SaltProcessingComponent}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltProcessingComponentTest {
	
	SaltProcessingComponent fixture = null;
	SDocument document = null;
	
	/**
	 * Set up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new SaltProcessingComponent());
		SaltFactory factory = SaltFactory.eINSTANCE;
		SDocument document = factory.createSDocument();
		SDocumentGraph documentGraph = factory.createSDocumentGraph();
		document.setSDocumentGraph(documentGraph);
		documentGraph.createSTextualDS("Ride the dragon towards the crimson eye!");
		setDocument(document);
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.SaltProcessingComponent#getTargetLayerName(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)}.
	 */
	@Test
	public void testGetTargetLayerNameDefault() {
		SLayer layer = SaltFactory.eINSTANCE.createSLayer();
		getFixture().setTargetLayer(layer, "Name");
		assertEquals(layer.getSId() + ":Name", getFixture().getTargetLayerName(getDocument()));
	}
	
	/**
	 * Test method for {@link SaltProcessingComponent#getTargetLayer(SDocument)}.
	 *
	 */
	@Test
	public void testGetTargetLayer() {
		SLayer defaultLayer = getFixture().getTargetLayer(getDocument());
		assertNotNull(defaultLayer);
		assertNotNull(getFixture().getTargetLayer(getDocument()));
		getFixture().setTargetLayer(defaultLayer, "Name");
		assertTrue(defaultLayer == getFixture().getTargetLayer(getDocument()));
	}

	/**
	 * @return the fixture
	 */
	private SaltProcessingComponent getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(SaltProcessingComponent fixture) {
		this.fixture = fixture;
	}

	/**
	 * @return the document
	 */
	private SDocument getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	private void setDocument(SDocument document) {
		this.document = document;
	}

}

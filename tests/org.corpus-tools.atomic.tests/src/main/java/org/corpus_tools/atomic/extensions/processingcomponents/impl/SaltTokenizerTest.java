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
package org.corpus_tools.atomic.extensions.processingcomponents.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.junit.Before;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

/**
 * Unit tests for {@link SaltTokenizer}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltTokenizerTest {
	
	SaltTokenizer fixture = null;
	SDocument document = null;

	/**
	 * Set up fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new SaltTokenizer());
		SDocument document = SaltFactory.createSDocument();
		SDocumentGraph documentGraph = SaltFactory.createSDocumentGraph();
		document.setDocumentGraph(documentGraph);
		documentGraph.createTextualDS("Proceeds the orig. Weedian: m.f. Nazareth!");
		setDocument(document);
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer#processDocument(org.corpus_tools.salt.common.SDocument)}.
	 */
	@Test
	public void testProcessDocument() {
		assertEquals("Proceeds the orig. Weedian: m.f. Nazareth!", getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		getFixture().processDocument(getDocument());
		SDocumentGraph graph = getDocument().getDocumentGraph();
		List<SToken> tokens = graph.getTokens();
		assertEquals(9, graph.getTokens().size());
		assertEquals("Proceeds", graph.getText(tokens.get(0)));
		assertEquals("the", graph.getText(tokens.get(1)));
		assertEquals("orig", graph.getText(tokens.get(2)));
		assertEquals(".", graph.getText(tokens.get(3)));
		assertEquals("Weedian", graph.getText(tokens.get(4)));
		assertEquals(":", graph.getText(tokens.get(5)));
		assertEquals("m.f.", graph.getText(tokens.get(6)));
		assertEquals("Nazareth", graph.getText(tokens.get(7)));
		assertEquals("!", graph.getText(tokens.get(8)));
		SToken previousToken = null;
		for (SToken token : tokens) {
			if (previousToken != null) {
				for (SRelation<?, ?> edge : token.getInRelations()) {
					if (edge instanceof SOrderRelation) {
						assertTrue(((SOrderRelation) edge).getSource() == previousToken);
					}
				}
			}
			previousToken = token;
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer#processDocument(org.corpus_tools.salt.common.SDocument)}.
	 */
	@Test
	public void testProcessDocumentWithConfiguration() {
		assertEquals("Proceeds the orig. Weedian: m.f. Nazareth!", getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		getFixture().setConfiguration(createTokenizerConfiguration(true));
		getFixture().processDocument(getDocument());
		SDocumentGraph graph = getDocument().getDocumentGraph();
		List<SToken> tokens = graph.getTokens();
		assertTrue(graph.getTokens().size() == 8);
		assertEquals("Proceeds", graph.getText(tokens.get(0)));
		assertEquals("the", graph.getText(tokens.get(1)));
		assertEquals("orig.", graph.getText(tokens.get(2)));
		assertEquals("Weedian", graph.getText(tokens.get(3)));
		assertEquals(":", graph.getText(tokens.get(4)));
		assertEquals("m.f.", graph.getText(tokens.get(5)));
		assertEquals("Nazareth", graph.getText(tokens.get(6)));
		assertEquals("!", graph.getText(tokens.get(7)));
		SToken previousToken = null;
		for (SToken token : tokens) {
			if (previousToken != null) {
				for (SRelation<?, ?> edge : token.getInRelations()) {
					if (edge instanceof SOrderRelation) {
						assertTrue(((SOrderRelation) edge).getSource() == previousToken);
					}
				}
			}
			previousToken = token;
		}
	}
	
	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer#processDocument(org.corpus_tools.salt.common.SDocument)}.
	 */
	@Test
	public void testProcessDocumentWithAbbreviationlessConfiguration() {
		assertEquals("Proceeds the orig. Weedian: m.f. Nazareth!", getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		getFixture().setConfiguration(createTokenizerConfiguration(false));
		getFixture().processDocument(getDocument());
		SDocumentGraph graph = getDocument().getDocumentGraph();
		List<SToken> tokens = graph.getTokens();
		assertTrue(graph.getTokens().size() == 9);
		assertEquals("Proceeds", graph.getText(tokens.get(0)));
		assertEquals("the", graph.getText(tokens.get(1)));
		assertEquals("orig", graph.getText(tokens.get(2)));
		assertEquals(".", graph.getText(tokens.get(3)));
		assertEquals("Weedian", graph.getText(tokens.get(4)));
		assertEquals(":", graph.getText(tokens.get(5)));
		assertEquals("m.f.", graph.getText(tokens.get(6)));
		assertEquals("Nazareth", graph.getText(tokens.get(7)));
		assertEquals("!", graph.getText(tokens.get(8)));
		SToken previousToken = null;
		for (SToken token : tokens) {
			if (previousToken != null) {
				for (SRelation<?, ?> edge : token.getInRelations()) {
					if (edge instanceof SOrderRelation) {
						assertTrue(((SOrderRelation) edge).getSource() == previousToken);
					}
				}
			}
			previousToken = token;
		}
	}
	
	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer#processDocument(org.corpus_tools.salt.common.SDocument)}.
	 */
	@Test
	public void testProcessDocumentWithEmptyConfiguration() {
		assertEquals("Proceeds the orig. Weedian: m.f. Nazareth!", getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		getFixture().setConfiguration(new SaltTokenizerConfiguration());
		getFixture().processDocument(getDocument());
		SDocumentGraph graph = getDocument().getDocumentGraph();
		List<SToken> tokens = graph.getTokens();
		assertTrue(graph.getTokens().size() == 9);
		assertEquals("Proceeds", graph.getText(tokens.get(0)));
		assertEquals("the", graph.getText(tokens.get(1)));
		assertEquals("orig", graph.getText(tokens.get(2)));
		assertEquals(".", graph.getText(tokens.get(3)));
		assertEquals("Weedian", graph.getText(tokens.get(4)));
		assertEquals(":", graph.getText(tokens.get(5)));
		assertEquals("m.f.", graph.getText(tokens.get(6)));
		assertEquals("Nazareth", graph.getText(tokens.get(7)));
		assertEquals("!", graph.getText(tokens.get(8)));
		SToken previousToken = null;
		for (SToken token : tokens) {
			if (previousToken != null) {
				for (SRelation<?, ?> edge : token.getInRelations()) {
					if (edge instanceof SOrderRelation) {
						assertTrue(((SOrderRelation) edge).getSource() == previousToken);
					}
				}
			}
			previousToken = token;
		}
	}
	
	/**
	 * Creates a {@link SaltTokenizerConfiguration} object and fills it with parameters
	 * @param b 
	 *
	 * @return
	 */
	private SaltTokenizerConfiguration createTokenizerConfiguration(boolean addAbreviations) {
		SaltTokenizerConfiguration config = new SaltTokenizerConfiguration();
		if (addAbreviations) {
			config.setAbbreviations(new HashSet<String>(Arrays.asList("orig.", "m.f.")));
		}
		config.setLanguageCode(LanguageCode.aa);
		return config;
	}

	/**
	 * @return the fixture
	 */
	private SaltTokenizer getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(SaltTokenizer fixture) {
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

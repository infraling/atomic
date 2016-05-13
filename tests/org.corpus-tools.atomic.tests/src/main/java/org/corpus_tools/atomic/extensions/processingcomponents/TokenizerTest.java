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
package org.corpus_tools.atomic.extensions.processingcomponents;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

/**
 * A unit test for {@link Tokenizer}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenizerTest {
	
	Tokenizer fixture = null;
	SDocument document = null;

	/**
	 * Set up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Tokenizer tokenizer = new Tokenizer() {
			
			@Override
			public ProcessingComponentConfiguration<?> getConfiguration() {
				// TODO Auto-generated method stub
				// FIXME: Add real configuration
				return null;
			}
			
			@Override
			public List<String> tokenize(String rawSourceText) {
				List<String> stringTokens = new ArrayList<>();
				stringTokens.addAll(Arrays.asList(new String[]{"Ride", "the", "dragon", "towards", "the", "crimson", "eye", "!"}));
				return stringTokens;
			}
		};
		setFixture(tokenizer);
		SDocument document = SaltFactory.createSDocument();
		SDocumentGraph documentGraph = SaltFactory.createSDocumentGraph();
		document.setDocumentGraph(documentGraph);
		documentGraph.createTextualDS("Ride the dragon towards the crimson eye!");
		setDocument(document);
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#processDocument(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)}.
	 */
	@Test
	public void testProcessDocument() {
		assertEquals("Ride the dragon towards the crimson eye!", getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		getFixture().processDocument(getDocument());
		SDocumentGraph graph = getDocument().getDocumentGraph();
		List<SToken> tokens = graph.getTokens();
		assertTrue(graph.getTokens().size() == 8);
		assertEquals("Ride", graph.getText(tokens.get(0)));
		assertEquals("the", graph.getText(tokens.get(1)));
		assertEquals("dragon", graph.getText(tokens.get(2)));
		assertEquals("towards", graph.getText(tokens.get(3)));
		assertEquals("the", graph.getText(tokens.get(4)));
		assertEquals("crimson", graph.getText(tokens.get(5)));
		assertEquals("eye", graph.getText(tokens.get(6)));
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
	 * @return the fixture
	 */
	private Tokenizer getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(Tokenizer fixture) {
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

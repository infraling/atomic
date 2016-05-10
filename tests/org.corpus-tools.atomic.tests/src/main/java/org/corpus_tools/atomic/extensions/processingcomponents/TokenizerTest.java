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
import java.util.Collections;
import java.util.List;

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;

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
		SaltFactory factory = SaltFactory.eINSTANCE;
		SDocument document = factory.createSDocument();
		SDocumentGraph documentGraph = factory.createSDocumentGraph();
		document.setSDocumentGraph(documentGraph);
		documentGraph.createSTextualDS("Ride the dragon towards the crimson eye!");
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#processDocument(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)}.
	 */
	@Test
	public void testProcessDocument() {
		SaltFactory factory = SaltFactory.eINSTANCE;
		SDocument document = factory.createSDocument();
		SDocumentGraph documentGraph = factory.createSDocumentGraph();
		document.setSDocumentGraph(documentGraph);
		assertNotNull(document.getSDocumentGraph());
		getFixture().processDocument(document);
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

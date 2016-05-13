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
package org.corpus_tools.atomic.projects.salt;

import static org.junit.Assert.*; 

import java.util.List;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.salt.common.SCorpus;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link CorpusCreationRunnable}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class CorpusCreationRunnableTest {
	
	CorpusCreationRunnable fixture = null;

	/**
	 * Set up the fixture and a dummy {@link Corpus} object (FIXME: Mock this!).
	 * Structure as follows:
	 * 
	 * CorpusGraph1
	 * 	C1
	 *  	C12
	 *  	C13
	 * 		
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Corpus c1 = new Corpus();
		c1.setName("c1");
		Corpus c12 = new Corpus();
		c12.setName("c12");
		c1.addChild(c12);
		Corpus c13 = new Corpus();
		c13.setName("c13");
		c1.addChild(c13);
		setFixture(new CorpusCreationRunnable(c1));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.salt.CorpusCreationRunnable#run()}.
	 */
	@Test
	public void testRun() {
		getFixture().run();
		assertNotNull(getFixture().getRootCorpus());
		assertEquals("c1", getFixture().getRootCorpus().getName());
		final List<SCorpus[]> subCorpora = getFixture().getSubCorpora();
		assertEquals(2, subCorpora.size());
		for (int i = 0; i < subCorpora.size(); i++) {
			assertEquals("c1" + String.valueOf(i + 2), subCorpora.get(i)[1].getName());
		}
	}

	/**
	 * @return the fixture
	 */
	private CorpusCreationRunnable getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(CorpusCreationRunnable fixture) {
		this.fixture = fixture;
	}

}

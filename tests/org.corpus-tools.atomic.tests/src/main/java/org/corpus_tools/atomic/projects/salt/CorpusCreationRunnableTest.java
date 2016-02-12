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

import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.ProjectNode;
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
	 * PROJECT (project)
	 * 
	 *   - c1
	 *     - d1:t1
	 *     - d2:t2
	 *   - c2
	 *     - c21
	 *       - d211:t211
	 *       - d212:t212
	 *     - c22
	 *       - c221
	 *         - d221:t221
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Corpus corpus = new Corpus("corpus");
		setFixture(new CorpusCreationRunnable(corpus));
	}

	/**
	 * Constructs a corpus to be tested
	 *
	 * @return
	 */
	private ProjectNode constructCorpus() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.salt.CorpusCreationRunnable#run()}.
	 */
	@Test
	public void testRun() {
		fail("Not yet implemented"); // TODO
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

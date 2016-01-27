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

import org.corpus_tools.atomic.internal.projects.DefaultProjectData;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;

/**
 * Unit test for {@link SaltProjectCompiler}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class SaltProjectCompilerTest {
	
	private SaltProjectCompiler fixture = null;

	/**
	 * Set up the fixture and a dummy {@link ProjectData} object (FIXME: Mock this!).
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
		DefaultProjectData data = new DefaultProjectData("project");
		Corpus c1 = new Corpus("c1");
		  Document d1 = new Document("d1", "t1");
		  Document d2 = new Document("d2", "t2");
		  c1.addChild(d1);
		  c1.addChild(d2);
		Corpus c2 = new Corpus("corpus2");
		  Corpus c21 = new Corpus("c21");
		    Document d211 = new Document("d211", "t211");
		    Document d212 = new Document("d212", "t212");
		  c21.addChild(d211);
		  c21.addChild(d212);
		c2.addChild(c21);
		  Corpus c22 = new Corpus("c22");
		    Corpus c221 = new Corpus("c221");
		      Document d221 = new Document("d221", "t221");
		    c221.addChild(d221);
		  c22.addChild(c221);
		c2.addChild(c22);
		data.addCorpus(c1);
		data.addCorpus(c2);
		
		setFixture(new SaltProjectCompiler(data));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.SaltProjectCompiler#run()}.
	 */
	@Test
	public void testRun() {
		Object retVal = getFixture().run();
		assertTrue(retVal instanceof SaltProject);
		SaltProject project = (SaltProject) retVal;
		assertEquals(2, project.getSCorpusGraphs().size());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.projects.SaltProjectCompiler#setProjectData(org.corpus_tools.atomic.projects.ProjectData)}.
	 */
	@Test
	public void testSetProjectData() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * @return the fixture
	 */
	private SaltProjectCompiler getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(SaltProjectCompiler fixture) {
		this.fixture = fixture;
	}

}

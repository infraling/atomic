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
package org.corpus_tools.atomic.commands;

import static org.junit.Assert.*;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SwitchWorkspaceHandlerTest {

	private SwitchWorkspaceHandler fixture;

	/**
	 * TODO: Description
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new SwitchWorkspaceHandler());
	}

	/**
	 * TODO: Description
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.commands.SwitchWorkspaceHandler#execute(org.eclipse.core.commands.ExecutionEvent)}.
	 */
	@Test
	public void testExecute() {
		fail("Not yet implemented");
	}

	/**
	 * @return the fixture
	 */
	public SwitchWorkspaceHandler getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	public void setFixture(SwitchWorkspaceHandler fixture) {
		this.fixture = fixture;
	}

}

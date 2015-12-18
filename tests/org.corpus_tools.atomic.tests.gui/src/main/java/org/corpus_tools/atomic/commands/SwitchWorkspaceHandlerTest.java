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

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForWidget;
import static org.junit.Assert.*;

import org.corpus_tools.atomic.workspace.SelectWorkspaceDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
	private static SWTWorkbenchBot bot;

	/**
	 * TODO: Description
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
	}
	
	@Before
	public void beforeAllTests() {
		bot = new SWTWorkbenchBot();
		bot.resetWorkbench();
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
	public void testDialogOpenedAndCloses() {
		SWTBotMenu fileMenu = bot.menu("File");
		assertNotNull(fileMenu);
		SWTBotMenu switchWorkspaceMenu = bot.menu("Switch Workspace");
		assertNotNull(switchWorkspaceMenu);
		switchWorkspaceMenu.click();
		bot.captureScreenshot("screenshots/screenshot-switchworkspacehandlertest-after-click.jpeg");
//		bot.waitUntilWidgetAppears(new DefaultCondition() {
//			
//			public boolean test() throws Exception {
//				return null != bot.shell("Switch Workspace");
//			}
//			
//			public String getFailureMessage() {
//				return "Could not find widget: Dialog \"Switch workspace\"...";
//			}
//		});
		// Open dialog
		SWTBotShell dialog = bot.shell("Switch workspace");
		assertNotNull(dialog);
		dialog.activate();
		bot.captureScreenshot("screenshots/screenshot-switchworkspacehandlertest-dialog-should-be-open.jpeg");
		SWTBot dialogBot = dialog.bot();
		SWTBotButton cancelButton = dialogBot.button("Cancel");
		assertNotNull(cancelButton);
		cancelButton.click();
		assertTrue(dialog.widget.isDisposed());
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

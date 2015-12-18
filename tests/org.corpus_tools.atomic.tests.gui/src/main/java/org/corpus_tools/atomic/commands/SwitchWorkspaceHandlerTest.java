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

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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

	private static SWTWorkbenchBot bot;

	@Before
	public void beforeAllTests() {
		bot = new SWTWorkbenchBot();
		bot.resetWorkbench();
	}

	/**
	 * Test opening and cancelling of dialog
	 */
	@Test
	public void testDialogOpenedAndCloses() {
		SWTBotMenu fileMenu = bot.menu("File");
		assertNotNull(fileMenu);
		SWTBotMenu switchWorkspaceMenu = bot.menu("Switch Workspace");
		assertNotNull(switchWorkspaceMenu);
		switchWorkspaceMenu.click();
		
		// Open dialog
		SWTBotShell dialog = bot.shell("Switch workspace");
		assertNotNull(dialog);
		dialog.activate();
		SWTBot dialogBot = dialog.bot();
		SWTBotButton cancelButton = dialogBot.button("Cancel");
		assertNotNull(cancelButton);
		cancelButton.click();
		assertTrue(dialog.widget.isDisposed());
	}

	/**
	 * Test opening and cancelling of dialog
	 */
	@Test
	public void testDialogOpenedViaShortcutAndCloses() {
		SWTBotShell shell = bot.activeShell();
		shell.pressShortcut(SWT.ALT | SWT.SHIFT, 'W');
		
		// Open dialog
		SWTBotShell dialog = bot.shell("Switch workspace");
		assertNotNull(dialog);
		dialog.activate();
		SWTBot dialogBot = dialog.bot();
		SWTBotButton cancelButton = dialogBot.button("Cancel");
		assertNotNull(cancelButton);
		cancelButton.click();
		assertTrue(dialog.widget.isDisposed());
	}
	
}

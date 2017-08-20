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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertVisible; 
import static org.junit.Assert.*;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * GUI tests for {@link SwitchWorkspaceHandler}
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SwitchWorkspaceHandlerTest {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeAllTests() {
		bot = new SWTWorkbenchBot();
		bot.resetWorkbench();
	}

	/**
	 * Test opening and cancelling of dialog
	 */
	@Test
	public void testDialogOpenedAndCloses() {
		SWTBotShell dialog = openDialog();
		assertVisible(dialog);
		SWTBot dialogBot = dialog.bot();
		cancelDialog(dialog, dialogBot);
	}

	/**
	 * Test opening and cancelling of dialog
	 */
	@Test
	public void testDialogOpenedViaShortcutAndCloses() {
		SWTBotShell shell = bot.activeShell();
		shell.pressShortcut(SWT.ALT | SWT.SHIFT, 'W');

		SWTBotShell dialog = bot.shell("Switch workspace");
		assertNotNull(dialog);
		assertTrue(dialog.isOpen());
		dialog.activate();
		SWTBot dialogBot = dialog.bot();
		cancelDialog(dialog, dialogBot);
	}

	/**
	 * Opens the dialog
	 *
	 * @return an {@link SWTBotShell} for the dialog
	 */
	private SWTBotShell openDialog() {
		bot.menu("File").menu("Switch Workspace").click();

		SWTBotShell dialog = bot.shell("Switch workspace");
		assertNotNull(dialog);
		assertTrue(dialog.isOpen());
		dialog.activate();
		return dialog;
	}
	
	/**
	 * Cancels the dialog
	 *
	 * @param dialog
	 * @param dialogBot
	 */
	private void cancelDialog(SWTBotShell dialog, SWTBot dialogBot) {
		dialogBot.button("Cancel").click();
		assertFalse(dialog.isOpen());
		assertTrue(dialog.widget.isDisposed());
	}

}

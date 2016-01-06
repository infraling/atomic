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
package org.corpus_tools.atomic.workspace;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertVisible;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * GUI tests for {@link SelectWorkspaceDialog}
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SelectWorkspaceDialogTest {
	
	private static SWTWorkbenchBot bot;
	private SWTBotShell dialog = null;

	@Before
	public void setUp() {
		bot = new SWTWorkbenchBot();
		bot.resetWorkbench();
		dialog  = openDialog();
	}
	
	@After
	public void tearDown(){
		cancelDialog();
	}
	
//	/**
//	 * Test filling in dialog
//	 */
//	@Test
//	public void testFillingInCombo() {
//		SWTBotShell dialog = openDialog();
//		SWTBot dialogBot = dialog.bot();
//
//		SWTBotCombo combo = dialogBot.comboBox();
//		assertNotNull(combo);
//		combo.setText(System.getProperty("user.home") + "/atomic");
//		dialogBot.captureScreenshot("screenshots/combo-filled.jpeg");
//		assertEquals(System.getProperty("user.home") + "/atomic", combo.getText());
//		cancelDialog(dialog, dialogBot);
//	}
//	
//	@Test
//	public void testTogglingCheckbox() {
//		SWTBotShell dialog = openDialog();
//		SWTBot dialogBot = dialog.bot();
//		
//		SWTBotCheckBox rememberButton = dialogBot.checkBox("Remember workspace");
//		assertNotNull(rememberButton);
//		boolean isChecked = rememberButton.isChecked();
//		rememberButton.click();
//		assertEquals(!isChecked, rememberButton.isChecked());
//		rememberButton.click();
//		assertEquals(isChecked, rememberButton.isChecked());
//		cancelDialog(dialog, dialogBot);
//	}
//	
	@Test
	public void testBrowseButton() {
		assertVisible(bot.button("Browse..."));
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
	private void cancelDialog() {
		dialog.bot().button("Cancel").click();
		assertFalse(dialog.isOpen());
		assertTrue(dialog.widget.isDisposed());
	}
	
}

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
import static org.junit.Assert.*;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
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
	
	/**
	 * Test filling in dialog
	 */
	@Test
	public void testFillingInCombo() {
		SWTBotCombo combo = bot.comboBox();
		assertNotNull(combo);
		combo.setText(System.getProperty("user.home") + "/atomic");
		bot.captureScreenshot("screenshots/combo-filled.jpeg");
		assertEquals(System.getProperty("user.home") + "/atomic", combo.getText());
	}
	
	@Test
	public void testTogglingCheckbox() {
		SWTBotCheckBox rememberButton = bot.checkBox("Remember workspace");
		assertNotNull(rememberButton);
		boolean isChecked = rememberButton.isChecked();
		rememberButton.click();
		assertEquals(!isChecked, rememberButton.isChecked());
		rememberButton.click();
		assertEquals(isChecked, rememberButton.isChecked());
	}
	
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

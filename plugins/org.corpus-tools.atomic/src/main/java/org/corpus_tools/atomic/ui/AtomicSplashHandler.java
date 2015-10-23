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
package org.corpus_tools.atomic.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;
import org.osgi.framework.FrameworkUtil;

/**
 * Constructs the splash handler from hardcoded values.
 * FIXME: Externalize Strings
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class AtomicSplashHandler extends BasicSplashHandler {

	/**
	 * Initializes the splash screen
	 * 
	 * @param shell The shell that contains the splash screen
	 *  
	 * @copydoc @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	public void init(final Shell shell) {
		super.init(shell);
		// Location of the progress bar
		Rectangle progressRect = new Rectangle(10, 10, 300, 15);
		setProgressRect(progressRect);

		// Location of the progress messages
		Rectangle messageRect = new Rectangle(10, 35, 300, 30);
		setMessageRect(messageRect);

		// Construct version string
		setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE).getRGB());
		final String version = "Version " + FrameworkUtil.getBundle(this.getClass()).getVersion().toString();

		// Position the version message
		Rectangle buildIdRectangle = new Rectangle(222, 260, 200, 40);

		// Create label for version message
		Label versionLabel = new Label(getContent(), SWT.RIGHT);
		versionLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		versionLabel.setBounds(buildIdRectangle);
		versionLabel.setText(version);
	}
}

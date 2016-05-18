/*******************************************************************************
 * Copyright 2016 Stephan Druskat
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
package org.corpus_tools.atomic.projects.wizard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration; 
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog that collects configuration setting for a specific 
 * implementation of {@link Tokenizer} from the user. It also
 * holds an instance of the respective 
 * {@link ProcessingComponentConfiguration} and keeps it synced. 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenizerConfigurationDialog extends Dialog {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "TokenizerConfigurationDialog".
	 */
	private static final Logger log = LogManager.getLogger(TokenizerConfigurationDialog.class);
	
	private ProcessingComponentConfiguration<?> configuration = null;
	private IConfigurationElement tokenizer;

	/**
	 * 
	 */
	public TokenizerConfigurationDialog(Shell parentShell, IConfigurationElement tokenizer, ProcessingComponentConfiguration<?> configuration) {
		super(parentShell);
		this.configuration = configuration;
		this.tokenizer = tokenizer;
	}
	
	/* 
	 * @copydoc @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configuration of " + tokenizer.getAttribute("name"));
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}
	

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = (Composite) super.createDialogArea(parent);
        content.setLayout(new FillLayout());

		ScrolledComposite sc = new ScrolledComposite(content, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        Composite composite = new Composite(sc, SWT.NONE);
        composite.setLayout(new FillLayout());

		Object controls = null;
		try {
			controls = tokenizer.createExecutableExtension("configurationControls");
			if (controls instanceof ProcessingComponentConfigurationControls) {
				Composite controlsComposite = ((ProcessingComponentConfigurationControls) controls).execute(composite, SWT.NONE);
				initDataBindings(configuration, controlsComposite);
			}
		}
		catch (CoreException e) {
			log.error("Could not create an executable extension for {}!", tokenizer.getAttribute("configurationControls"), e);
		}

        sc.setContent(composite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return parent; 
	}

	/**
	 * Parses the children of composite and creates databinding for each
	 *
	 * @param configuration
	 * @param composite
	 */
	private void initDataBindings(ProcessingComponentConfiguration<?> configuration, Composite composite) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the configuration
	 */
	public ProcessingComponentConfiguration<?> getConfiguration() {
		return configuration;
	}

}

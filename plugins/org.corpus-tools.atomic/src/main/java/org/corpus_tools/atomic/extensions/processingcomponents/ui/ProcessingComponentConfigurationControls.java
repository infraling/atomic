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
package org.corpus_tools.atomic.extensions.processingcomponents.ui;

import org.corpus_tools.atomic.extensions.ProcessingComponent;
import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * Implementations of this class provide the SWT and/or JFace 
 * controls needed to configure a {@link ProcessingComponent}, i.e.,
 * set values of an implementation of {@link ProcessingComponentConfiguration}.
 * <p>
 * Clients need to add the respective controls/widgets in the
 * {@link #addControls()} method. SWT {@link Layout}s must be
 * set for this class to set up the controls properly.
 * 
 * @see <a href="https://www.eclipse.org/swt/">https://www.eclipse.org/swt/</a>
 * @see <a href="https://wiki.eclipse.org/JFace">https://wiki.eclipse.org/JFace</a>
 * @see <a href="https://www.eclipse.org/swt/widgets/">https://www.eclipse.org/swt/widgets/</a>
 * @see <a href="http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm">http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm</a> 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class ProcessingComponentConfigurationControls extends Composite {

	/**
	 * @param parent
	 * @param style
	 */
	public ProcessingComponentConfigurationControls(Composite parent, int style) {
		super(parent, style);
		addControls();
	}

	/**
	 * This is where SWT widgets/JFace viewers are added.
	 *
	 */
	public abstract void addControls();

}

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Implementations of this class provide the SWT and/or JFace 
 * controls needed to configure a {@link ProcessingComponent}, i.e.,
 * set values of an implementation of {@link ProcessingComponentConfiguration}.
 * <p>
 * Clients need to add the respective controls/widgets in the
 * {@link #addControls()} method. The container's default layout is
 * a four-column {@link GridLayout}, but clients can change this by
 * calling <code>parent.setLayout()</code> in {@link #addControls(Composite, int)}.
 * <p>
 * All controls added in {@link #addControls(Composite, int)} which are to be bound
 * to the respective configuration must be set a data object via 
 * {@link Control#setData(Object)}. This data object must be a {@link String} representing
 * the name of the configuration property. The String must not be set
 * directly, but must be a reference to an externalized String. This
 * is because the same String will also be used to set up databinding.
 * 
 * @see <a href="https://www.eclipse.org/swt/">https://www.eclipse.org/swt/</a>
 * @see <a href="https://wiki.eclipse.org/JFace">https://wiki.eclipse.org/JFace</a>
 * @see <a href="https://www.eclipse.org/swt/widgets/">https://www.eclipse.org/swt/widgets/</a>
 * @see <a href="http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm">http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm</a>
 * @see <a href="http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fref-wizard-externalize-strings.htm">http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fref-wizard-externalize-strings.htm</a> 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class ProcessingComponentConfigurationControls {
	
	private ProcessingComponentConfiguration<?> configuration = null;

	/**
	 * This is where SWT widgets/JFace viewers are added.
	 * FIXME: Complete JavaDoc:
	 * - Note that initDataBindings must be implemented!
	 * 
	 * @param none 
	 * @param composite 
	 *
	 */
	public abstract void addControls(Composite parent, int style);

	/**
	 * Sets up the container composite with processing cpomponent-specific controls.
	 *
	 * @param parent The parent {@link Composite}
	 * @param style An {@link SWT} style bit
	 */
	public void execute(Composite parent, int style) {
		Composite composite = new Composite(parent, style);
		composite.setLayout(new GridLayout(4, false));
		addControls(composite, SWT.NONE);
	}
	
	/**
	 * Helper method to set property data on a control.
	 *
	 * @param control
	 * @param property
	 */
	public void setProperty(Control control, String property) {
		control.setData("property", property);
	}

	/**
	 * @return the configuration
	 */
	public ProcessingComponentConfiguration<?> getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(ProcessingComponentConfiguration<?> configuration) {
		this.configuration = configuration;
	}

}

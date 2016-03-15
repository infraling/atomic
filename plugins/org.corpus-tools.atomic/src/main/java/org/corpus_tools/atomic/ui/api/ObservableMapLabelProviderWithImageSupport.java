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
package org.corpus_tools.atomic.ui.api;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Abstract class providing image and advanced text support to {@link ObservableMapLabelProvider}.
 * Clients can extend to specify custom text and images for elements.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public abstract class ObservableMapLabelProviderWithImageSupport extends ObservableMapLabelProvider {

	/**
	 * @param attributeMaps
	 */
	public ObservableMapLabelProviderWithImageSupport(IObservableMap[] attributeMaps) {
		super(attributeMaps);
	}
	
	/* 
	 * @copydoc @see org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public abstract Image getColumnImage(Object element, int columnIndex);

}

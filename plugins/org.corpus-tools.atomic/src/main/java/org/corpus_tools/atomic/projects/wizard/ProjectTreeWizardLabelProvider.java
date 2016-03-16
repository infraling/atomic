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
package org.corpus_tools.atomic.projects.wizard;

import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.ui.ObservableMapLabelProviderWithImageSupport;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * An {@link ObservableMapLabelProviderWithImageSupport} that provides images for
 * {@link Corpus} and {@link Document} objects in the tree viewer.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectTreeWizardLabelProvider extends ObservableMapLabelProviderWithImageSupport {
	
	/**
	 * @param attributeMaps
	 */
	public ProjectTreeWizardLabelProvider(IObservableMap[] attributeMaps) {
		super(attributeMaps);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.ui.api.ObservableMapLabelProviderWithImageSupport#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof Corpus) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		else if (element instanceof Document) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}

}

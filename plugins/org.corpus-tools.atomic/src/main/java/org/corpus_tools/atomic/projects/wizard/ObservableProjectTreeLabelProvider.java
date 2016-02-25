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
import org.corpus_tools.atomic.projects.ProjectData;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ObservableProjectTreeLabelProvider extends ObservableMapLabelProvider {

	/**
	 * @param iObservableSet
	 */
	public ObservableProjectTreeLabelProvider(IObservableSet knownElements) {
		super(Properties.observeEach(knownElements, BeanProperties.values(new String[] { "name" })));
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ProjectNode) {
			return ((ProjectNode) element).getName();
		}
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof Corpus) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		else if (element instanceof Document) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}

}

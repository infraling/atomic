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

import org.corpus_tools.atomic.internal.projects.DefaultProjectData;
import org.corpus_tools.atomic.projects.Corpus;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectTreeObservableFactory implements IObservableFactory {

	private DefaultProjectData model;

	/**
	 * @param model
	 */
	public ProjectTreeObservableFactory(DefaultProjectData model) {
		this.model = model;
	}

	/* 
	 * @copydoc @see org.eclipse.core.databinding.observable.masterdetail.IObservableFactory#createObservable(java.lang.Object)
	 */
	@Override
	public IObservable createObservable(Object target) {
		if (target instanceof DefaultProjectData) {
			return BeanProperties.list("corpora").observe(target);
		}
		if (target instanceof Corpus) {
			return BeanProperties.list("children").observe(target);
		}
		return null;
	}

}

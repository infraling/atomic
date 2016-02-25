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

import java.util.TreeMap;
import java.util.TreeSet;

import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.ProjectData;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ProjectTreeContentProvider implements ITreeContentProvider {

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object project) {
		return getChildren(project);
	}

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ProjectData) {
			ProjectData project = (ProjectData) parentElement;
			TreeSet<Corpus> sortedCorpora = new TreeSet<>(project.getCorpora());
			return sortedCorpora.toArray();
		}
		else if (parentElement instanceof Corpus) {
			Corpus corpus = (Corpus) parentElement;
			return corpus.getChildren().toArray();
		}
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ProjectData) {
			ProjectData project = (ProjectData) element;
			return project.getCorpora().size() > 0;
		}
		else if (element instanceof Corpus) {
			Corpus corpus = (Corpus) element;
			return corpus.getChildren().size() > 0;
		}
		return false;
	}

}

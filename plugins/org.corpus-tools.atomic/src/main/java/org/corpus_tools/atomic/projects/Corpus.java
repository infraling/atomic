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
package org.corpus_tools.atomic.projects;

import java.util.LinkedHashMap;

import org.corpus_tools.atomic.models.AbstractBean;
import org.eclipse.core.runtime.Assert;

/**
 * JavaBean definition of a corpus, i.e., a node in the
 * corpus structure tree. A corpus can be a root corpus,
 * i.e., the topmost structural element in a project.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Corpus extends AbstractBean implements ProjectNode {
	
	/**
	 * Property <code>name</name>, readable and writable.
	 */
	private String name = null;

	/**
	 * Property <code>children</name>, readable and writable.
	 */
	private LinkedHashMap<String, ProjectNode> children = null;
	
	/**
	 * Property <code>parent</name>, readable and writable.
	 */
	private Object parent = null;
	
	/**
	 * Default no-arg constructor (JavaBean compliance). 
	 */
	public Corpus() {
		children = new LinkedHashMap<>();
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#setName(java.lang.String)
	 */
	public void setName(final String name) {
		final String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getChildren()
	 */
	@Override
	public LinkedHashMap<String, ProjectNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	private void setChildren(final LinkedHashMap<String, ProjectNode> children) {
		final LinkedHashMap<String, ProjectNode> oldChildren = this.children;
		this.children = children;
		firePropertyChange("children", oldChildren, this.children);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#addChild(org.corpus_tools.atomic.projects.ProjectNode)
	 */
	@Override
	public ProjectNode addChild(final ProjectNode child) {
		Assert.isNotNull(child);
		child.setParent(this);
		final LinkedHashMap<String, ProjectNode> newChildren = getChildren();
		newChildren.put(child.getName(), child);
		setChildren(newChildren);
		return child;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#removeChild(java.lang.String)
	 */
	@Override
	public ProjectNode removeChild(final String childName) {
		Assert.isNotNull(childName);
		final LinkedHashMap<String, ProjectNode> newChildren = getChildren();
		ProjectNode removedChild = newChildren.remove(childName);
		setChildren(newChildren);
		return removedChild;
	}

	/**
	 * @return the parent
	 */
	public Object getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Object parent) {
		this.parent = parent;
	}

}

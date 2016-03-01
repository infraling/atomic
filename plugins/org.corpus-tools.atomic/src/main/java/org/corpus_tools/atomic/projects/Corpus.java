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

import java.util.ArrayList;
import java.util.List;

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
/**
 * TODO Description
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
	private List<ProjectNode> children = null;
	
	/**
	 * Default no-arg constructor (JavaBean compliance). 
	 */
	public Corpus() {
		children = new ArrayList<>();
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
	
	/**
	 * Returns the child nodes of this corpus.
	 *
	 * @return the corpus' children
	 */
	public List<ProjectNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	private void setChildren(final List<ProjectNode> children) {
		final List<ProjectNode> oldChildren = this.children;
		this.children = children;
		firePropertyChange("children", oldChildren, this.children);
	}

	/**
	 * Adds a child, i.e., a {@link ProjectNode}, to this
	 * {@link Corpus}. Assert that this 
	 * is not null, and throw a {@link RuntimeException}
	 * if it is. The new child is added to the
	 * {@link List} of children. Returns the added
	 * child {@link ProjectNode}.
	 *
	 * @param the child to add (must not be null)
	 * @return the added child
	 * @throws RuntimeException if child is null
	 */
	public ProjectNode addChild(final ProjectNode child) {
		Assert.isNotNull(child);
		final List<ProjectNode> newChildren = getChildren();
		newChildren.add(child);
		setChildren(newChildren);
		return child;
	}

	/**
	 * Removes an element from the corpus. The argument is
	 * the name of the {@link ProjectNode} to remove. Must 
	 * assert that this is not null, and throw a {@link RuntimeException}
	 * if it is. 
	 * <p>
	 * Returns the previous node associated with this name
	 * or null if there was no node of this name.
	 *
	 * @param the name of the child to remove (must not be null)
	 * @return the removed node or null
	 * @throws RuntimeException if childName is null
	 */
	public ProjectNode removeChild(final ProjectNode child) {
		Assert.isNotNull(child);
		final List<ProjectNode> newChildren = getChildren();
		int indexOfChildToBeRemoved = newChildren.indexOf(child);
		ProjectNode removedChild = newChildren.remove(indexOfChildToBeRemoved );
		setChildren(newChildren);
		return removedChild;
	}

}

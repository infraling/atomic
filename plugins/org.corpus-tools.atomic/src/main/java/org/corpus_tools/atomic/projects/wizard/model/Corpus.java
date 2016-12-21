/**
 * 
 */
package org.corpus_tools.atomic.projects.wizard.model;

import org.corpus_tools.atomic.models.AbstractBean;

/**
 * JavaBean definition of a named object, i.e., something that
 * has a name.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Corpus extends AbstractBean {
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_PATH = "path";
	/**
	 * Property <code>name</name>, readable and writable.
	 */
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		final String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
	}
	
	/**
	 * Property <code>path</name>, readable and writable.
	 */
	private String path = null;

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		final String oldPath = this.path;
		this.path = path;
		firePropertyChange("path", oldPath, this.path);
	}

}
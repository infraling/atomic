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
package org.corpus_tools.atomic.extensions.processingcomponents;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * A simple bean-like class holding meta-data about a
 * processing component. This class needs to have
 * all the fields available in the extension point schema
 * for 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class ProcessingComponentMetaData {
	
	/**
	 * Fields for the {@link String}-based meta data.
	 */
	private String id = null, name = null, category = null, description = null, creator = null;
	private boolean configurable = false;
	
	/**
	 * Default no-args construtor.
	 */
	public ProcessingComponentMetaData() {}
	
	/**
	 * Utility method taking an {@link IConfigurationElement}, and
	 * completes all fields in bulk. Returns the completed
	 * {@link ProcessingComponentMetaData} object for the
	 * sake of convenience.
	 *
	 * @param component The component whose metadata this 
	 * {@link ProcessingComponentMetaData} object should hold.
	 * @return this {@link ProcessingComponentMetaData} 
	 */
	public ProcessingComponentMetaData bulkCompleteFields(IConfigurationElement component) {
		setId(component.getAttribute("id"));
		setName(component.getAttribute("name"));
		setCategory(component.getAttribute("category"));
		setDescription(component.getAttribute("description"));
		setCreator(component.getAttribute("creator"));
		setConfigurable(component.getAttribute("wizardPage") != null);
		return this;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the configurable
	 */
	public boolean isConfigurable() {
		return configurable;
	}

	/**
	 * @param configurable the configurable to set
	 */
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}
	

}

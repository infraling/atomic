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
package org.corpus_tools.atomic.extensions;

import org.corpus_tools.atomic.models.AbstractBean;

/**
 * Implementations of this interface provide a link between
 * {@link ProcessingComponent}s and user interfaces.
 * They must be public and configurable, which is why via
 * the extension point for processing components they must
 * also extend {@link AbstractBean}.
 * <p>
 * Please note that implementing an {@link AbstractBean}
 * also places constraints on the way that implementations
 * can be structured. Two points especially are to be adhered to.
 * <p>
 * <b>The implementation must have a no-arg constructor!</b><br/>
 * Initialization tasks, such as creating new collection objects
 * will take place in this constructor, e.g.
 * <code><pre>
 * private List<String> list = null;
 * 
 * public MyProcessingComponentConfiguration() {
 *     list = new ArrayList<>();
 * }
 * </pre></code>
 * <p>
 * <b>All fields must be readable and writable <em>properties</em>
 * in Java Bean terminology</b><br/>
 * They must be private fields initialized as <code>null</code>. 
 * Their getters and setters must be public, and setters must call 
 * {@link AbstractBean#firePropertyChange(String, Object, Object)}
 * and should be implemented along the following lines.
 * <code><pre>
 * private String myProperty = null;
 * 
 * public void setMyProperty(final String myProperty) {
 *     final String oldMyProperty = this.myProperty;
 *     this.myProperty = myProperty;
 *     firePropertyChange("myProperty", oldMyProperty, this.myProperty);
 * }
 * </pre></code>
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public interface ProcessingComponentConfiguration<ConfiguredProcessingComponent> {
	
	/**
	 * Return the {@link ProcessingComponent} object the implementation
	 * of this interface configures.
	 *
	 * @return the configured {@link ProcessingComponent} object.
	 */
	public ConfiguredProcessingComponent getConfiguredComponent();
	
	/**
	 * Sets the configured processing component type. 
	 *
	 * @param component
	 */
	public void setConfiguredComponent(ConfiguredProcessingComponent component);
	
}

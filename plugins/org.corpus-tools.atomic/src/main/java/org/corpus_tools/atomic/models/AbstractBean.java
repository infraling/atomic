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
package org.corpus_tools.atomic.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.Assert;

/**
 * A base class for JavaBeans type beans to be used in conjunction with Eclipse (JFace) databinding. It provides {@link PropertyChangeSupport} and handles the setup of {@link PropertyChangeListener}s.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public abstract class AbstractBean {

	/**
	 * Add {@link PropertyChangeSupport} to all extensions of this class. Transient because the support does not need to be serialized.
	 */
	private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Adds a {@link PropertyChangeListener} for all properties.
	 * 
	 * @see {@link PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)}.
	 * 
	 * @param listener The property change listener to add (must not be null!)
	 * @throws RuntimeException if listener is null
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		Assert.isNotNull(listener);
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a {@link PropertyChangeListener}.
	 * 
	 * @param listener The property change listener to remove (must not be null!)
	 * @throws RuntimeException if listener is null
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		Assert.isNotNull(listener);
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Passes a {@link PropertyChangeEvent} to the {@link PropertyChangeSupport}.
	 * 
	 * @param propertyName Name of the property
	 * @param oldValue Old value of the property
	 * @param newValue New value of the property
	 */
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}

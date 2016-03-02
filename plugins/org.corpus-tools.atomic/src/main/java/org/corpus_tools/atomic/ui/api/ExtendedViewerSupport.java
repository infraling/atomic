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
package org.corpus_tools.atomic.ui.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.viewers.AbstractTreeViewer;

/**
 * Extended helper methods for binding observables to an {@link AbstractTreeViewer}.
 * In contrast to {@link ViewerSupport}, the methods in this class allow for setting 
 * a custom label provider, e.g., to add images to the viewer.
 * <p>
 * This class is based on {@link ViewerSupport}, copyright 2009 Matthew Hall and others.
 * Original comments below.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 * @see {@link org.eclipse.jface.databinding.viewers.ViewerSupport}.
 * 
 * ------------------------------------------
 *  Original comment from {@link ViewerSupport}
 *  Copyright (c) 2009 Matthew Hall and others.
 *  
 *  Contributors:
 *     Matthew Hall - initial API and implementation (bug 260337)
 *     Matthew Hall - bug 283428
 *     
 * Helper methods for binding observables to a {@link StructuredViewer} or
 * {@link AbstractTableViewer}.
 */
public class ExtendedViewerSupport {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "ExtendedViewerSupport".
	 */
	private static final Logger log = LogManager.getLogger(ExtendedViewerSupport.class);
	
	/**
	 * Binds the viewer to the specified input, using the specified children property to generate 
	 * child nodes, the specified label property to generate labels, and the specified label
	 * provider to generate label contents (text, image).
	 *
	 * @param viewer the tree viewer to set up
	 * @param input the input to set on the viewer
	 * @param childrenProperty the property to use as the children of an element
	 * @param labelProperty the property to use for labels
	 * @param clazz the class of the label provider to use for generating label contents
	 */
	public static void bind(AbstractTreeViewer viewer, Object input, IListProperty childrenProperty, IValueProperty labelProperty, Class<? extends ObservableMapLabelProviderWithImageSupport> clazz) {
		bind(viewer, input, childrenProperty, new IValueProperty[] { labelProperty }, clazz);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children property to 
	 * generate child nodes, the specified label property to generate labels, and the specified label
	 * provider to generate label contents (text, image).
	 *
	 * @param viewer the tree viewer to set up
	 * @param input the input to set on the viewer
	 * @param childrenProperty the property to use as the children of an element
	 * @param labelProperty the property to use for labels
	 * @param labelProviderClazz the class of the label provider to use for generating label contents
	 */
	public static void bind(AbstractTreeViewer viewer, Object input, IListProperty childrenProperty, IValueProperty[] labelProperties, Class<? extends ObservableMapLabelProviderWithImageSupport> labelProviderClazz) throws RuntimeException {
		Realm realm = DisplayRealm.getRealm(viewer.getControl().getDisplay());
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(childrenProperty.listFactory(realm), null);
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		// Creating an instance of the passed-in label provider and pass it the relevant arguments
		Constructor<? extends ObservableMapLabelProviderWithImageSupport> ctor = null;
		try {
			ctor = labelProviderClazz.getConstructor(IObservableMap[].class);
		}
		catch (NoSuchMethodException | SecurityException e) {
			log.error("Creating a constructor object for the label provider threw an error.", e);
		}
		Assert.isNotNull(ctor);
		Object labelProvider = null;
		try {
			labelProvider = ctor.newInstance(new Object[] { Properties.observeEach(contentProvider.getKnownElements(), labelProperties) });
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("Creating a new instance of the label provider threw an error.", e);
		}
		Assert.isNotNull(labelProvider);
		viewer.setLabelProvider((ObservableMapLabelProviderWithImageSupport) labelProvider);
		if (input != null)
			viewer.setInput(input);
	}

}

/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uni_jena.iaa.linktype.atomic.model.salt.editor;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_jena.iaa.linktype.atomic.model.salt.editor"; //$NON-NLS-1$
	public static final String DIRECTED_EDGE_ICON = "directed edge icon";
	public static final String UNDIRECTED_EDGE_ICON = "undirected edge icon";
	public static final String NODE_ICON = "node icon";
	public static final String SPAN_ICON = "span icon";
	public static final String SPANNINGREL_ICON = "spanningrel icon";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        ImageDescriptor directed = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/directed.gif"), null));
        ImageDescriptor undirected = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/undirected.gif"), null));
        ImageDescriptor node = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/node.gif"), null));
        ImageDescriptor span = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/span.gif"), null));
        ImageDescriptor spanningrel = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/spanningrel.gif"), null));
        registry.put(DIRECTED_EDGE_ICON, directed);
        registry.put(UNDIRECTED_EDGE_ICON, undirected);
        registry.put(NODE_ICON, node);
        registry.put(SPAN_ICON, span);
        registry.put(SPANNINGREL_ICON, spanningrel);
    }

}

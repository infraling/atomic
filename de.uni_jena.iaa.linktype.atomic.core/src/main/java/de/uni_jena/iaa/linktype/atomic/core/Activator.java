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
package de.uni_jena.iaa.linktype.atomic.core;

import org.eclipse.swt.widgets.Display; 
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

import de.uni_jena.iaa.linktype.atomic.core.workspace.AtomicWorkspacePicker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_jena.iaa.linktype.atomic.core"; //$NON-NLS-1$

	// The shared instance
		private static Activator plugin;
		
		// XXX Shared instance of bundle context
		static BundleContext bundleContext;
		
		// XXX services for starting bundles
		private static PackageAdmin packageAdmin = null;
		private static ServiceReference packageAdminRef = null;

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
			bundleContext = context;
			
			packageAdminRef = bundleContext.getServiceReference(PackageAdmin.class.getName());
			packageAdmin = (PackageAdmin) bundleContext.getService(packageAdminRef);
			
			Display display = Display.getCurrent();
			if (display == null) {
//		    	display = Display.getDefault();
//		    	if (display == null) {
		    		display = PlatformUI.createDisplay();	
//		    	}
		    }
			System.err.println("Activator display: " + display);
		    AtomicWorkspacePicker.pickWorkspace(display);
		}
		
		private static Bundle getBundle(String symbolicName) {
			if (packageAdmin == null)
				return null;
			Bundle[] bundles = packageAdmin.getBundles(symbolicName, null);
			if (bundles == null)
				return null;
			// Return the first bundle that is not installed or uninstalled
			for (int i = 0; i < bundles.length; i++) {
				if ((bundles[i].getState() & (Bundle.INSTALLED | Bundle.UNINSTALLED)) == 0) {
					return bundles[i];
				}
			}
			return null;
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

// TODO Remove unused code found by UCDetector
// 		/**
// 		 * Returns an image descriptor for the image file at the given
// 		 * plug-in relative path
// 		 *
// 		 * @param path the path
// 		 * @return the image descriptor
// 		 */
// 		public static ImageDescriptor getImageDescriptor(String path) {
// 			return imageDescriptorFromPlugin(PLUGIN_ID, path);
// 		}
}

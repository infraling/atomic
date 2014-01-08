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

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.uni_jena.iaa.linktype.atomic.core.workspace.PickWorkspaceDialog;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_jena.iaa.linktype.atomic.core"; //$NON-NLS-1$

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
		
		super.start(context);

	    // Bug fix: Avoid SWTException (invalid thread) on Mac OS X
	    Display display = null;
	    if (Display.getCurrent() != null) {
	    	display = Display.getCurrent();
	    }
	    else 
	    	display = PlatformUI.createDisplay();
	    try
	    {
	      // Get current instance location
	      Location instanceLoc = Platform.getInstanceLocation();

	      // get what the user last said about remembering the workspace location
	      boolean remember = PickWorkspaceDialog.isRememberWorkspace();

	      // get the last used workspace location
	      String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();

	      // if we have a "remember" but no last used workspace, it's not much to
	      // remember
	      if (remember && (lastUsedWs == null || lastUsedWs.length() == 0))
	      {
	        remember = false;
	      }

	      // check to ensure the workspace location is still OK
	      if (remember)
	      {
	        // if there's any problem whatsoever with the workspace, force a dialog
	        // which in its turn will tell them what's bad
	        String ret = PickWorkspaceDialog.checkWorkspaceDirectory(display.getActiveShell(), lastUsedWs, false, false);
	        if (ret != null)
	        {
	          remember = false;
	        }
	      }

	      // if we don't remember the workspace, show the dialog
	      if (!remember)
	      {
	        PickWorkspaceDialog pwd = new PickWorkspaceDialog(display.getActiveShell(), false);
	        int pick = pwd.open();

	        // if the user cancelled, we can't do anything as we need a workspace,
	        // so in this case, we tell them and exit
	        if (pick == Window.CANCEL)
	        {
	          if (pwd.getSelectedWorkspaceLocation() == null)
	          {
	            MessageDialog.openError(display.getActiveShell(), "Error",
	                "The application can not start without a workspace root and will now exit.");
	            try
	            {
	              if (PlatformUI.isWorkbenchRunning())
	              {
	                PlatformUI.getWorkbench().close();
	              }
	            }
	            catch (Exception e)
	            {
	              e.printStackTrace();
	            }
	            System.exit(0);
	          }
	        }

	        else
	        {
	          // tell Eclipse what the selected location was and continue
	          instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false);
	        }
	      }
	      else
	      {
	        // set the last used location and continue
	        instanceLoc.set(new URL("file", null, lastUsedWs), false);
	      }
	    }
	    finally
	    {
	      display.dispose();
	    }
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

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}

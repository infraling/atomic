/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.workspace;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicWorkspacePicker {

	public static void pickWorkspace(Display display) throws IllegalStateException, MalformedURLException, IOException {
	    try {
	      Location instanceLoc = Platform.getInstanceLocation();
	      boolean isRememberWorkspace = PickWorkspaceDialog.isRememberWorkspace();
	      String lastUsedWorkspace = PickWorkspaceDialog.getLastSetWorkspaceDirectory();

	      if (isRememberWorkspace && (lastUsedWorkspace == null || lastUsedWorkspace.length() == 0))
	    	  isRememberWorkspace = false;

	      if (isRememberWorkspace) {
	    	  String ret = PickWorkspaceDialog.checkWorkspaceDirectory(display.getActiveShell(), lastUsedWorkspace, false, false);
	    	  if (ret != null) {
	    		  isRememberWorkspace = false;
	    	  }
	      }

	      if (!isRememberWorkspace) {
	    	  PickWorkspaceDialog pwd = new PickWorkspaceDialog(display.getActiveShell(), false);
	    	  int pick = pwd.open();
	    	  
	    	  if (pick == Window.CANCEL) {
	    		  if (pwd.getSelectedWorkspaceLocation() == null) {
	    			  MessageDialog.openError(display.getActiveShell(), "Error", "The application can not start without a workspace root and will now exit.");
	    			  try {
	    				  if (PlatformUI.isWorkbenchRunning()) 
	    					  PlatformUI.getWorkbench().close();
	    			  }
	    			  catch (Exception e) {
	    				  e.printStackTrace();
	    			  }
	    			  System.exit(0);
	    		  }
	    	  }
	    	  else
	    		  instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false);
	      }
	      else
	    	  instanceLoc.set(new URL("file", null, lastUsedWorkspace), false);
	    }
	    finally {
	    	display.dispose();
	    }
	}
}

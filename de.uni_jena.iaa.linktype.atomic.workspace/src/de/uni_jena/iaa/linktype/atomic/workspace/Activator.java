package de.uni_jena.iaa.linktype.atomic.workspace;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

  // The plug-in ID
  public static final String PLUGIN_ID = "de.uni_jena.iaa.linktype.atomic.workspace"; //$NON-NLS-1$

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);

    Display display = PlatformUI.createDisplay();
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
}

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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;

import de.uni_jena.iaa.linktype.atomic.core.workspace.PickWorkspaceDialog;

/**
 * This class controls all aspects of the application's execution
 */
public class Atomic implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		
		Display display = PlatformUI.createDisplay();
		// TODO: Custom Workspace code! FIXME: Clean this up!
		
			// Get current instance location
			Location instanceLoc = Platform.getInstanceLocation();  
				
			// get what the user last said about remembering the workspace location 
			boolean remember = PickWorkspaceDialog.isRememberWorkspace(); 
			 
			// get the last used workspace location 
			String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory(); 
			 
			// if we have a "remember" but no last used workspace, it's not much to remember 
			if (remember && (lastUsedWs == null || lastUsedWs.length() == 0)) { 
				remember = false; 
			} 
			 
			// check to ensure the workspace location is still OK 
			if (remember) { 
				// if there's any problem whatsoever with the workspace, force a dialog which in its turn will tell them what's bad 
			    String ret = PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs, false, false); 
			    if (ret != null) { 
			    	remember = false; 
			    } 
			} 
			 
			// if we don't remember the workspace, show the dialog 
			if (!remember) { 
				PickWorkspaceDialog pwd = new PickWorkspaceDialog(false); 
			    int pick = pwd.open(); 
			 
			    // if the user cancelled, we can't do anything as we need a workspace, so in this case, we tell them and exit 
			    if (pick == Window.CANCEL) { 
			    	if (pwd.getSelectedWorkspaceLocation()  == null) { 
			                MessageDialog.openError(display.getActiveShell(), "Error","The application can not start without a workspace root and will now exit."); 
			                try { 
			                	PlatformUI.getWorkbench().close(); 
			                } 
			                catch (Exception e) {
			                	e.printStackTrace();
			                } 
			                System.exit(0); 
			                return IApplication.EXIT_OK; 
			    	} 
			    } 
			    
			    else { 
			    	// tell Eclipse what the selected location was and continue 
			        instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false); 
			    } 
			} 
			else { 
				// set the last used location and continue 
			    instanceLoc.set(new URL("file", null, lastUsedWs), false); 
			}     
			
			// TODO: ### END ### Custom workspace code!
			        
			        // FIXME: May take up too much RAM! Alternatives?
//			        IProject[] projectsArray = ResourcesPlugin.getWorkspace().getRoot().getProjects(); // All IProjects in workspace
//			        ArrayList<IProject> projectList = new ArrayList<IProject>(); // empty ArrayList
//			        Activator.getDefault().getSaltProjectProviderService().setOpenIProjectsArrayList(projectList); // globalize empty ArrayList
//			        ArrayList<SaltProject> saltProjectsArrayList = new ArrayList<SaltProject>(); // empty ArrayList
//			        Activator.getDefault().getSaltProjectProviderService().setExistingSaltProjectsArrayList(saltProjectsArrayList); // Globalize empty ArrayList
//			        for (int i = 0; i < projectsArray.length; i++) {
//			        	IProject project = projectsArray[i];
//						if (project.isOpen() && project.getFile("saltProject.salt").exists()) {
//							SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
//							File file = project.getLocation().toFile();
//							URI uri = URI.createFileURI(file.getAbsolutePath());
//							saltProject.loadSaltProject(uri);
//							Activator.getDefault().getSaltProjectProviderService().getOpenIProjectsArrayList().add(projectsArray[i]); // Add only open IProjects to global ArrayList
//					        Activator.getDefault().getSaltProjectProviderService().getExistingSaltProjectsArrayList().add(saltProject); // Add only loaded SaltProjects to ArrayList
//						}
//					}


			try {
				int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
					if (returnCode == PlatformUI.RETURN_RESTART) return IApplication.EXIT_RESTART;
					else return IApplication.EXIT_OK;
			} 
			finally {
				display.dispose();
			}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}

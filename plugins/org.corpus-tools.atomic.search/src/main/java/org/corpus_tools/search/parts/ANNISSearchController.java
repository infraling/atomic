package org.corpus_tools.search.parts;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.osgi.service.log.LogService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Creatable
public class ANNISSearchController {

	@FXML
	private Button reIndexButton;
	
	@Inject
	private UISynchronize uiSync;
	
	@Inject 
	private LogService log;
	
	public void reindex() {
		log.log(LogService.LOG_INFO, "Attempting re-index");
		Job job = new Job("Re-indexing corpus search") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
	            IWorkspaceRoot root = workspace.getRoot();
	            
	            for(IProject p : root.getProjects()) {
	            	
	            }
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	
}

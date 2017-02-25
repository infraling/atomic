package org.corpus_tools.search.service;

import org.corpus_tools.graphannis.API;
import org.corpus_tools.graphannis.API.CorpusStorageManager;
import org.corpus_tools.graphannis.API.CorpusStorageManager.CorpusInfo;
import org.corpus_tools.graphannis.API.GraphUpdate;
import org.corpus_tools.graphannis.API.StringVector;
import org.corpus_tools.graphannis.QueryToJSON;
import org.corpus_tools.graphannis.SaltImport;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.common.util.URI;

@Creatable
public class SearchService {
	
	private final CorpusStorageManager corpusManager;
	
	public SearchService() {
		// TODO: find a directory inside the workspace
		this.corpusManager  = new CorpusStorageManager("/tmp/atomic-annis");
	}
	
	private void handleResource(IResource res, String projectName, IProgressMonitor monitor) throws CoreException {
		if(res instanceof IFile) {
			IFile file = (IFile) res;
			if("salt".equals(file.getFileExtension()) && !"saltProject.salt".equals(file.getName())) {
				URI location = URI.createURI(file.getLocationURI().toASCIIString());

				monitor.setTaskName("Importing document " + location.lastSegment());
				SDocumentGraph docGraph = SaltUtil.loadDocumentGraph(location);
				
				addDocument(projectName, docGraph);
			} 
		} else if (res instanceof IContainer ){
			for (IResource child : ((IContainer) res).members()) {
				handleResource(child, projectName, monitor);
			}
		}
	}
	
	public void reindexAllDocuments() {
		Job job = new Job("Re-indexing documents") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				
				// get all documents of workspace
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				for(IProject p : root.getProjects()) {
					// delete all old documents first
					monitor.setTaskName("Deleting corpus " + p.getName());
					
					deleteCorpus(p.getName());

					try {
						handleResource(p, p.getName(), monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public void deleteCorpus(String corpusName) {
		// find all nodes of the corpus and delete them
		StringVector nodes = corpusManager.find(new StringVector(corpusName), QueryToJSON.aqlToJSON("node"));
		GraphUpdate update = new GraphUpdate();
		for(long i=0; i < nodes.size(); i++) {
			URI nodeURI = URI.createURI(nodes.get(i).getString());
			update.deleteNode(nodeURI.fragment());
		}
		update.finish();
		corpusManager.applyUpdate(corpusName, update);
		
	}
	
	public void addDocument(String corpusName, SDocumentGraph docGraph) {
		CorpusInfo info =  corpusManager.info("pcc2");
		System.out.print(info.memoryUsageInBytes());
		SaltImport saltImport = new SaltImport();
		saltImport.map(docGraph);
		
		API.GraphUpdate updateList = saltImport.finish();
		updateList.finish();
		
		corpusManager.applyUpdate(corpusName, updateList);
		
		
	}
	
}

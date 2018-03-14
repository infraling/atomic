package org.corpus_tools.search.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.graphannis.API;
import org.corpus_tools.graphannis.API.CorpusStorageManager;
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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import annis.service.objects.Match;
import annis.service.objects.MatchGroup;

@Creatable
public class SearchService {
	
	public static final String IDX_FOLDER = ".idx-graphannis";
	
	private static final Logger log = LogManager.getLogger(SearchService.class);
	
	private final CorpusStorageManager corpusManager;

	private File corpusIndexLocation;
	
	
	public SearchService() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		corpusIndexLocation  = new File(workspace.getRoot().getLocation().toOSString(), IDX_FOLDER);
		this.corpusManager  = new CorpusStorageManager(corpusIndexLocation.getAbsolutePath());
	}
	
	private void findDocumentsRecursive(IResource res, String projectName, 
			IProgressMonitor monitor, Collection<URI> docList) throws CoreException {
		if(monitor.isCanceled()) {
			return;
		}
		
		if(res instanceof IFile) {
			IFile file = (IFile) res;
			if("salt".equals(file.getFileExtension()) && !"saltProject.salt".equals(file.getName())) {
				URI location = URI.createURI(file.getLocationURI().toASCIIString());
				docList.add(location);
			} 
		} else if (res instanceof IContainer ) {
			for (IResource child : ((IContainer) res).members()) {
				findDocumentsRecursive(child, projectName, monitor, docList);
			}
		}
	}
	
	public void reindexAllDocuments(boolean blockUI) {
		
		/* 
		 * First of all, delete the old index folder, as a) it has no
		 * further use anyway, and b) can break stuff in Windows... 
		 */
		if (corpusIndexLocation.exists()) {
			Path path = Paths.get(corpusIndexLocation.getAbsolutePath());
			try {
				Files.walk(path)
				  .sorted(Comparator.reverseOrder())
				  .map(Path::toFile)
				  .forEach(File::delete);
			}
			catch (IOException e) {
				log.error("Error deleting ANNIS index folder!", e);
			}
		    assert Files.exists(path) == false;
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page != null) {
			page.saveAllEditors(true);
		}
		
		Job job = new Job("Re-indexing documents") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				// get all documents of workspace
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				
				Multimap<String, URI> docList = LinkedHashMultimap.create();
				
				SubMonitor monitorDelete = SubMonitor.convert(monitor, root.getProjects().length);
				monitorDelete.setTaskName("Deleting old corpora from index");
				int indexProjects = 0;
				for(IProject p : root.getProjects()) {
					// delete all old documents first					
					deleteCorpus(p.getName());

					try {
						log.trace("Finding documents for project {}", p.getName());
						findDocumentsRecursive(p, p.getName(), monitor, docList.get(p.getName()));
					} catch (CoreException ex) {
						log.error("Could not get find documents for project {}", p.getName(), ex);
					}
					monitorDelete.worked(indexProjects++);
				}
				
				SubMonitor monitorImport = SubMonitor.convert(monitor, docList.size());
				monitorImport.setTaskName("Indexing documents");
				// index each document
				int indexDocs = 0;
				for(Map.Entry<String, URI> e : docList.entries()) {
					SDocumentGraph docGraph = SaltUtil.loadDocumentGraph(e.getValue());
					
					addDocument(e.getKey(), docGraph);
					
					monitorImport.worked(indexDocs++);
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setUser(blockUI);
		job.schedule();
	}
	
	public void deleteCorpus(String corpusName) {
		
		log.trace("Deleting all nodes from corpus {}", corpusName);
		
		// find all nodes of the corpus and delete them
		StringVector nodes = corpusManager.find(new StringVector(corpusName), QueryToJSON.aqlToJSON("node"));
		GraphUpdate update = new GraphUpdate();
		for(long i=0; i < nodes.size(); i++) {
			update.deleteNode(nodes.get(i).getString().replaceFirst("^salt:/", ""));
		}
		update.finish();
		corpusManager.applyUpdate(corpusName, update);
		
	}
	
	public void addDocument(String corpusName, SDocumentGraph docGraph) {
		SaltImport saltImport = new SaltImport();
		saltImport.map(docGraph);
		
		API.GraphUpdate updateList = saltImport.finish();
		updateList.finish();
		
		corpusManager.applyUpdate(corpusName, updateList);
	}
	
	private StringVector createAllCorporaList() {
		ArrayList<String> corpora = new ArrayList<>();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		for(IProject p : root.getProjects()) {
			corpora.add(p.getName());
		}
		return new StringVector(corpora.toArray(new String[0]));
	}
	
	public long count(String query) {
		return corpusManager.count(createAllCorporaList(), QueryToJSON.aqlToJSON(query));
	}
	
	public MatchGroup find(String query) {
		
		
		ArrayList<Match> result = new ArrayList<>();
		
		StringVector resultRaw = corpusManager.find(createAllCorporaList(), QueryToJSON.aqlToJSON(query));
		result.ensureCapacity((int) resultRaw.size());
		
		for(long i=0; i < resultRaw.size(); i++) {
			result.add(Match.parseFromString(resultRaw.get(i).getString()));
		}
		
		// TODO: sort the result
		return  new MatchGroup(result);

	}
	
	/**
	 * Finds matches *only* in the corpus/corpora of
	 * the specified {@link IProject}s.
	 * 
	 * @param query
	 * @param projectName The name of the {@link IProject} containing the corpus that should be searched.
	 * @return
	 */
	public MatchGroup findInProject(String query, String projectName) {
		ArrayList<Match> result = new ArrayList<>();
		
		/*
		 * Build StringVector for just the one project
		 */
		StringVector vector = new StringVector(new String[] {projectName});
		
		StringVector resultRaw = corpusManager.find(vector, QueryToJSON.aqlToJSON(query));
		result.ensureCapacity((int) resultRaw.size());
		
		for(long i=0; i < resultRaw.size(); i++) {
			result.add(Match.parseFromString(resultRaw.get(i).getString()));
		}
		
		// TODO: sort the result
		return  new MatchGroup(result);
	}
	
}

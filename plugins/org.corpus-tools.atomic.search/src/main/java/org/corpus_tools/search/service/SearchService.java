package org.corpus_tools.search.service;

import org.corpus_tools.graphannis.API.CorpusStorageManager;
import org.corpus_tools.graphannis.API.CorpusStorageManager.CorpusInfo;
import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
public class SearchService {
	
	private final CorpusStorageManager corpusManager;
	
	public SearchService() {
		// manually load needed libraries from our own copy
		//System.loadLibrary("jniAnnisApiInfo");
		//System.setProperty("org.bytedeco.javacpp.loadlibraries", "false");
		
		// TODO: find a directory inside the workspace
		this.corpusManager  = new CorpusStorageManager("/tmp/atomic-annis");
	}
	
	public void addDocument() {
		CorpusInfo info =  corpusManager.info("pcc2");
		System.out.print(info.memoryUsageInBytes());
	}
	
}

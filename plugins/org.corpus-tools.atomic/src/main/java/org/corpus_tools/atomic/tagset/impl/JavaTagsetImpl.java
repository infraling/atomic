/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.emf.common.util.URI;

/**
 * A simple implementation of a {@link Tagset}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class JavaTagsetImpl implements Tagset {
	
	private static final Logger log = LogManager.getLogger(JavaTagsetImpl.class);
	
	private final List<TagsetEntry> entries = new ArrayList<>();
	private String name;

	private SDocumentGraph graph;

	/**
	 * Unique serial version identifier for version 1L.
	 * @see Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor taking and setting the name of the tagset.
	 * 
	 * @param graph The document graph the tagset is for 
	 * @param name The name of the tagset
	 */
	public JavaTagsetImpl(SDocumentGraph graph, String name) {
		setName(name);
		setDocumentGraph(graph);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#addEntry(org.corpus_tools.atomic.tagset.TagsetEntry)
	 */
	@Override
	public boolean addEntry(TagsetEntry entry) {
		return entries.add(entry);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#removeEntry(org.corpus_tools.atomic.tagset.TagsetEntry)
	 */
	@Override
	public boolean removeEntry(TagsetEntry entry) {
		return entries.remove(entry);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getEntries()
	 */
	@Override
	public List<TagsetEntry> getEntries() {
		return entries;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#load(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Tagset load(URI uri) {
		Tagset tagset = null;
		String path = uri.toFileString();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(path));
		}
		catch (FileNotFoundException e) {
			log.error("No tagset file found at {}!", path);
		}
		try (ObjectInputStream ois = new ObjectInputStream(fis)) {
			tagset = (Tagset) ois.readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			log.error("Error reading the tagset from tagset file {}!", path, e);
		}
		return tagset;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#save(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public boolean save(URI uri) {
		FileOutputStream fos = null;
		String path = uri.toFileString();
		try {
			fos = new FileOutputStream(new File(path));
		}
		catch (FileNotFoundException e) {
			log.error("Error while trying to write the tagset file to location {}!", path, e);
			return false;
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(this);
		}
		catch (IOException e) {
			log.error("Error while creating the tagset file to be saved at {}!", path, e);
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#setDocumentGraph(org.corpus_tools.salt.common.SDocumentGraph)
	 */
	@Override
	public void setDocumentGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getDocumentGraph()
	 */
	@Override
	public SDocumentGraph getDocumentGraph() {
		return graph;
	}

}

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

	/**
	 * Unique serial version identifier for version 1L.
	 * @see Serializable
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean addEntry(TagsetEntry entry) {
		return entries.add(entry);
	}

	@Override
	public boolean removeEntry(TagsetEntry entry) {
		return entries.remove(entry);
	}

	@Override
	public List<TagsetEntry> getEntries() {
		return entries;
	}

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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}

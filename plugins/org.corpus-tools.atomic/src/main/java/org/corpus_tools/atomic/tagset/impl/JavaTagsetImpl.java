/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.io.File; 
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.models.AbstractBean;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.emf.common.util.URI;

/**
 * A simple implementation of a {@link Tagset}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class JavaTagsetImpl extends AbstractBean implements Tagset {
	
	private static final Logger log = LogManager.getLogger(JavaTagsetImpl.class);
	
	private String name;
	private List<TagsetValue> values;

	private SCorpus corpus;

	/**
	 * Unique serial version identifier for version 1L.
	 * @see Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor taking and setting the name of the tagset.
	 * 
	 * @param corpus The corpus the tagset is for 
	 * @param name The name of the tagset
	 */
	public JavaTagsetImpl(SCorpus corpus, String name) {
		setName(name);
		setCorpus(corpus);
		this.values = new ArrayList<>();	
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
	 * @see org.corpus_tools.atomic.tagset.Tagset#setCorpus(org.corpus_tools.salt.common.SCorpus)
	 */
	@Override
	public void setCorpus(SCorpus corpus) {
		this.corpus = corpus;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getCorpus()
	 */
	@Override
	public SCorpus getCorpus() {
		return corpus;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#addValue(org.corpus_tools.atomic.tagset.TagsetValue)
	 */
	@Override
	public boolean addValue(TagsetValue value) {
		List<TagsetValue> newValues = this.getValues();
		boolean isNewlyAdded = newValues.add(value);
		setValues(newValues);
		return isNewlyAdded;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#removeValue(org.corpus_tools.atomic.tagset.TagsetValue)
	 */
	@Override
	public boolean removeValue(TagsetValue value) {
		List<TagsetValue> newValues = this.getValues();
		boolean hadContainedValue = newValues.remove(value);
		setValues(newValues);
		return hadContainedValue;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#setValues(java.util.Set)
	 */
	@Override
	public void setValues(List<TagsetValue> values) {
		List<TagsetValue> oldValues = this.values;
		this.values = values;
		firePropertyChange("values", oldValues, this.values);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getValues()
	 */
	@Override
	public List<TagsetValue> getValues() {
		return values;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getValuesForParameters(java.lang.String, org.corpus_tools.salt.SALT_TYPE, java.lang.String, java.lang.String)
	 */
	@Override
	public Set<TagsetValue> getValuesForParameters(String layer, SALT_TYPE elementType, String namespace, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(e -> (Objects.equals(e.getLayer(), layer) 
				&& Objects.equals(e.getElementType(), elementType)
				&& Objects.equals(e.getNamespace(), namespace) 
				&& Objects.equals(e.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}

}

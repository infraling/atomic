/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.io.File; 
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.models.AbstractBean;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A simple implementation of a {@link Tagset}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
@JsonDeserialize(as = JavaTagsetImpl.class)
public class JavaTagsetImpl extends AbstractBean implements Tagset {
	
	/**
	 * Default constructor, needed to adhere to Java Bean specs.
	 */
	public JavaTagsetImpl() {
		// Do nothing, needed for serialization/deserialization
	}
	
	private static final Logger log = LogManager.getLogger(JavaTagsetImpl.class);
	
	private String name;
	private List<TagsetValue> values;

	private String corpus;

	/**
	 * Unique serial version identifier for version 1L.
	 * @see Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor taking and setting the name of the tagset.
	 * 
	 * @param corpusId The {@link Identifier} id of the {@link SCorpus} the tagset is for 
	 * @param name The name of the tagset
	 */
	public JavaTagsetImpl(String corpusId, String name) {
		setName(name);
		setCorpusId(corpusId);
		this.values = new ArrayList<>();	
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#save(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public boolean save(URI uri) {
		ObjectMapper mapper = new ObjectMapper();
		String path = uri.toFileString();
		try {
			mapper.writeValue(new File(path), this);
		}
		catch (IOException e1) {
			log.error("Error writing JSON file from tagset {}!", getName(), e1);
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
	public void setCorpusId(String corpus) {
		this.corpus = corpus;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#getCorpus()
	 */
	@Override
	public String getCorpusId() {
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
	 * @see org.corpus_tools.atomic.tagset.Tagset#addValue(int, org.corpus_tools.atomic.tagset.TagsetValue)
	 */
	public boolean addValue(int index, TagsetValue value) {
		List<TagsetValue> newValues = this.getValues();
		int oldSize = newValues.size();
		newValues.add(index, value);
		setValues(newValues);
		return newValues.size() > oldSize;
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


	/** 
	 * Implementation determining which parameters should be
	 * taken into account when querying the valid values.
	 * 
	 * As annotations in Salt **always** have a name, the
	 * parameters which determine the set are *layer*,
	 * *elementType* and *namespace*. Thus, there are 8
	 * possible combinations of `null`/non-`null` values:
	 * 
	 * ````
	 * L E N Case
	 * ----------
	 * 1 1 1 1 
	 * 0 0 0 2
	 * 1 0 0 3
	 * 1 1 0 4
	 * 1 0 1 5
	 * 0 1 0 6
	 * 0 1 1 7
	 * 0 0 1 8
	 * ````
	 * 
	 * Depending on the combination of parameter values,
	 * another method is called to return the correct value set.
	 * 
	 * @see org.corpus_tools.atomic.tagset.Tagset#getValuesForParameters(java.lang.String, org.corpus_tools.salt.SALT_TYPE, java.lang.String, java.lang.String)
	 */
	@Override
	public Set<TagsetValue> getValuesForParameters(String layer, SALT_TYPE elementType, String namespace, String name) {
		if (layer == null) {
			if (elementType == null) {
				if (namespace == null) { // Case 2
					return getValuesForParameters(name);
				}
				else { // Case 8
					return getValuesForParameters(namespace, false, name);
				}
			}
			else {
				if (namespace == null) { // Case 6
					return getValuesForParameters(elementType, name);
				}
				else { // Case 7
					return getValuesForParameters(elementType, namespace, name);
				}
			}
		}
		else {
			if (elementType == null) {
				if (namespace == null) { // Case 3
					return getValuesForParameters(layer, true, name);
				}
				else { // Case 5
					return getValuesForParameters(layer, namespace, name);
				}
			}
			else {
				if (namespace == null) { // Case 4 
					return getValuesForParameters(layer, elementType, name);
				}
				else { // Case 1
					Set<TagsetValue> validValues = new HashSet<>();
					Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
							Objects.equals(value.getLayer(), layer) 
							&& Objects.equals(value.getElementType(), elementType)
							&& Objects.equals(value.getNamespace(), namespace) 
							&& Objects.equals(value.getName(), name)));
					allValidEntries.forEach(e -> validValues.add(e));
					return validValues;
				}
			}
		}
	}
	
	private Set<TagsetValue> getValuesForParameters(String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (Objects.equals(value.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}

	private Set<TagsetValue> getValuesForParameters(String layerOrNamespace, boolean isFirstParameterLayerString, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		if (isFirstParameterLayerString) {
			Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
					Objects.equals(value.getLayer(), layerOrNamespace) 
					&& Objects.equals(value.getName(), name)));
			allValidEntries.forEach(e -> validValues.add(e));
		}
		else {
			Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
					Objects.equals(value.getNamespace(), layerOrNamespace) 
					&& Objects.equals(value.getName(), name)));
			allValidEntries.forEach(e -> validValues.add(e));
		}
		return validValues;
	}
	
	private Set<TagsetValue> getValuesForParameters(String layer, SALT_TYPE elementType, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
				Objects.equals(value.getLayer(), layer) 
				&& Objects.equals(value.getElementType(), elementType)
				&& Objects.equals(value.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}
	
	private Set<TagsetValue> getValuesForParameters(String layer, String namespace, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
				Objects.equals(value.getLayer(), layer) 
				&& Objects.equals(value.getNamespace(), namespace) 
				&& Objects.equals(value.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}
	
	private Set<TagsetValue> getValuesForParameters(SALT_TYPE elementType, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
				Objects.equals(value.getElementType(), elementType)
				&& Objects.equals(value.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}
	
	private Set<TagsetValue> getValuesForParameters(SALT_TYPE elementType, String namespace, String name) {
		Set<TagsetValue> validValues = new HashSet<>();
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
				Objects.equals(value.getElementType(), elementType)
				&& Objects.equals(value.getNamespace(), namespace) 
				&& Objects.equals(value.getName(), name)));
		allValidEntries.forEach(e -> validValues.add(e));
		return validValues;
	}
	
	@Override
	public Set<String> getAnnotationNamesForParameters(String layer, SALT_TYPE elementType, String namespace) {
		Stream<TagsetValue> allValidEntries = getValues().stream().filter(value -> (
				(Objects.equals(value.getLayer(), layer) || value.getLayer() == null) 
				&& (Objects.equals(value.getElementType(), elementType) || value.getElementType() == null)
				&& (Objects.equals(value.getNamespace(), namespace) || value.getNamespace() == null)));
		return allValidEntries.map(TagsetValue::getName).distinct().collect(Collectors.toCollection(HashSet<String>::new));
	}

}

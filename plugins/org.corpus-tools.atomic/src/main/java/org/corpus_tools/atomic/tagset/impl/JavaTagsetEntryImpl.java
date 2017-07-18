/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.util.List;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;

/**
 * A simple implementation of a {@link TagsetEntry}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class JavaTagsetEntryImpl implements TagsetEntry {

	private List<String> layers;
	private List<SALT_TYPE> elementTypes;
	private List<String> namespaces;
	private List<String> names;
	private List<TagsetValue> values;
	private Tagset tagset;

	/**
	 * Constructor taking all the necessary parameters for
	 * configuring a {@link TagsetEntry} validly.
	 * 
	 * @param layers The list of layers for this tagset entry
	 * @param elementTypes The list of element types for this tagset entry
	 * @param namespaces The list of namespaces for this tagset entry
	 * @param names The list of names for this tagset entry
	 * @param values The list of values for this tagset entry
	 * @param tagset The tagset containing this tagset entry
	 */
	public JavaTagsetEntryImpl(List<String> layers, List<SALT_TYPE> elementTypes, List<String> namespaces,
			List<String> names, List<TagsetValue> values, Tagset tagset) {
		setLayers(layers);
		setElementTypes(elementTypes);
		setNamespaces(namespaces);
		setNames(names);
		setValidValues(values);
		setTagset(tagset);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setLayers(java.util.List)
	 */
	@Override
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getLayers()
	 */
	@Override
	public List<String> getLayers() {
		return layers;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setElementTypes(java.util.List)
	 */
	@Override
	public void setElementTypes(List<SALT_TYPE> elementTypes) {
		this.elementTypes = elementTypes;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getElementTypes()
	 */
	@Override
	public List<SALT_TYPE> getElementTypes() {
		return elementTypes;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setNamespaces(java.util.List)
	 */
	@Override
	public void setNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getNamespaces()
	 */
	@Override
	public List<String> getNamespaces() {
		return namespaces;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setNames(java.util.List)
	 */
	@Override
	public void setNames(List<String> names) {
		this.names = names;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getNames()
	 */
	@Override
	public List<String> getNames() {
		return names;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setValidValues(java.util.List)
	 */
	@Override
	public void setValidValues(List<TagsetValue> values) {
		this.values = values;

	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getValidValues()
	 */
	@Override
	public List<TagsetValue> getValidValues() {
		return values;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setTagset(org.corpus_tools.atomic.tagset.Tagset)
	 */
	@Override
	public void setTagset(Tagset tagset) {
		this.tagset = tagset;
		
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getTagset()
	 */
	@Override
	public Tagset getTagset() {
		return tagset;
	}

}

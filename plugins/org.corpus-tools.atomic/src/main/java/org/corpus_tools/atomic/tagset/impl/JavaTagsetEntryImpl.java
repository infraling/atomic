/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	private String layer;
	private SALT_TYPE elementType;
	private String namespace;
	private String name;
	private Set<TagsetValue> values;
	private Tagset tagset;
	
	/**
	 * Constructor taking all the necessary parameters for
	 * configuring a {@link TagsetEntry} validly.
	 * 
	 * @param tagset The tagset containing this entry
	 * @param layer The layer for this tagset entry
	 * @param elementType The element type for this tagset entry
	 * @param namespace The annotation namespace for this tagset entry
	 * @param name The annotation name for this tagset entry
	 * @param values The valid annotation values for this tagset entry
	 */
	public JavaTagsetEntryImpl(Tagset tagset, String layer, SALT_TYPE elementType, String namespace, String name, TagsetValue... values) {
		setTagset(tagset);
		setLayer(layer);
		setElementType(elementType);
		setNamespace(namespace);
		setName(name);
		setValidValues(values);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setLayer(java.lang.String)
	 */
	@Override
	public void setLayer(String layer) {
		this.layer = layer;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getLayer()
	 */
	@Override
	public String getLayer() {
		return layer;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setElementType(org.corpus_tools.salt.SALT_TYPE)
	 */
	@Override
	public void setElementType(SALT_TYPE elementType) {
		this.elementType = elementType;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getElementType()
	 */
	@Override
	public SALT_TYPE getElementType() {
		return elementType;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setNamespace(java.lang.String)
	 */
	@Override
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#setValidValues(org.corpus_tools.atomic.tagset.TagsetValue[])
	 */
	@Override
	public void setValidValues(TagsetValue... values) {
		this.values = new HashSet<>(Arrays.asList(values));
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetEntry#getValidValues()
	 */
	@Override
	public Set<TagsetValue> getValidValues() {
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

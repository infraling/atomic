/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.core.runtime.Assert;

/**
 * A factory providing static methods for creating tagset elements.
 * 
 * For the highly configurable type {@link TagsetEntry}, two
 * creation options exist: 
 * 
 * 1. passing values traditionally to a
 * full-argument method {@link #createTagsetEntry(Tagset, String, SALT_TYPE, String, String, TagsetValue...)};
 * 2. creating the entry fluently, with {@link #newTagsetEntry(Tagset, TagsetValue...)}
 * as the entry point configuring the required minimum *values*, several
 * `withX()` methods for further configuration, and {@link #build()} for
 * the creation of the actual {@link TagsetEntry} object.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 */
public class TagsetFactory {
	
	private static Tagset tagset;
	private String layer = null;
	private SALT_TYPE elementType = null;
	private String namespace = null;
	private String name = null;
	private TagsetValue[] values = null;
	
	private static final ITagsetFactory factory = new JavaTagsetFactoryImpl();
	
	/**
	 * Delegates the creation of a {@link Tagset}
	 * to the factory implementation.
	 * 
	 * @param corpus The corpus this tagset is for 
	 * @param name The tagset name 
	 * 
	 * @return the tagset built by the factory implementation.
	 */
	public static Tagset createTagset(SCorpus corpus, String name) {
		return (tagset = factory.createTagset(corpus, name));
	}
	
	/**
	 * Traditional implementation of tagset entry creation.
	 * For a fluent builder implementation, see 
	 * {@link #newTagsetEntry(Tagset, TagsetValue...)}.
	 * 
	 * Delegates the creation of a {@link TagsetEntry}
	 * to the factory implementation.
	 * 
	 * @param tagset The tagset containing this tagset entry
	 * @param layer The layer of the entry
	 * @param elementType The element type of the entry
	 * @param namespace The annotation namespace of the entry
	 * @param name The annotation name of the entry
	 * @param values The valid annotation values of the entry
	 * 
	 * @return the tagset entry built by the factory implementation.
	 */
	public static TagsetEntry createTagsetEntry(Tagset tagset, String layer, SALT_TYPE elementType, String namespace, String name, TagsetValue... values) {
		return factory.createTagsetEntry(tagset, layer, elementType, namespace, name, values);
	}
	
	/**
	 * Delegates the creation of a {@link TagsetValue}
	 * to the factory implementation.
	 * 
	 * @param value The actual annotation value 
	 * @param description A description of the annotation value
	 * 
	 * @return the tagset value built by the factory implementation.
	 */
	public static TagsetValue createTagsetValue(String value, String description) {
		return factory.createTagsetValue(value, description);
	}

	/**
	 * Entry method for a fluent builder implementation
	 * of tagset entry creation.
	 * 
	 * Delegates the creation of a {@link TagsetValue}
	 * to the factory implementation.
	 * 
	 * @param tagset The tagset this entry is being assigned to 
	 * @param values The list of annotation values for this entry 
	 * 
	 * @return a tagset entry configures with the passed values 
	 */
	public static TagsetFactory newTagsetEntry(Tagset tagset, TagsetValue... values) {
		TagsetFactory self = new TagsetFactory();
		self.values = values;
		return self;
	}
	
	/**
	 * Configures the factory with a layer for the entry.
	 * 
	 * @param layer The layer to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withLayer(String layer) {
		this.layer = layer;
		return this;
	}
	
	/**
	 * Configures the factory with an element type for the entry.
	 * 
	 * @param elementType The element type to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withElementType(SALT_TYPE elementType) {
		this.elementType = elementType;
		return this;
	}
	
	/**
	 * Configures the factory with a namespace for the entry.
	 * 
	 * @param namespace The namespace to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}
	
	/**
	 * Configures the factory with an annotation name for the entry.
	 * 
	 * @param name The name to include in the tagset entry build
	 * @return the configures {@link TagsetFactory}
	 */
	public TagsetFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * Builds a validated tagset entry configured as per the
	 * called `withX()` methods, i.e., from the
	 * fields of the factory.
	 * 
	 * @return the configured tagset entry
	 */
	public TagsetEntry build() {
		validate();
		return factory.createTagsetEntry(tagset, layer, elementType, namespace, name, values);
	}

	/**
	 * Validates the factory inasmuch as it asserts
	 * that it has at least values configured.
	 */
	private void validate() {
		Assert.isNotNull(values, "A tagset entry needs to have at least one value!");
		Assert.isNotNull(tagset, "A tagset entry needs to be assigned to a tagset!");
	}

}

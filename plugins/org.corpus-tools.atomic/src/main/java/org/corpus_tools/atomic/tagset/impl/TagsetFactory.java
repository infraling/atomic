/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.util.Arrays;
import java.util.List;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.core.runtime.Assert;

/**
 * A factory providing static methods for creating tagset elements.
 * 
 * For the highly configurable type {@link TagsetEntry}, two
 * creation options exist: 
 * 
 * 1. passing values traditionally to a
 * full-argument method {@link #createTagsetEntry(List, List, List, List, List, Tagset)};
 * 2. creating the entry fluently, with {@link #newTagsetEntry(List)}
 * as the entry point configuring the required minimum *values*, several
 * `withX()` methods for further configuration, and {@link #build()} for
 * the creation of the actual {@link TagsetEntry} object.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 */
public class TagsetFactory {
	
	private static Tagset tagset;
	private String[] layers = null;
	private SALT_TYPE[] elementTypes = null;
	private String[] namespaces = null;
	private String[] names = null;
	private TagsetValue[] values = null;
	
	private static final ITagsetFactory factory = new JavaTagsetFactoryImpl();
	
	/**
	 * Delegates the creation of a {@link Tagset}
	 * to the factory implementation.
	 * 
	 * @param graph The document graph this tagset is for 
	 * @param name The tagset name 
	 * 
	 * @return the tagset built by the factory implementation.
	 */
	public static Tagset createTagset(SDocumentGraph graph, String name) {
		return (tagset = factory.createTagset(graph, name));
	}
	
	/**
	 * Traditional implementation of tagset entry creation.
	 * For a fluent builder implementation, see 
	 * {@link #newTagsetEntry(List)}.
	 * 
	 * Delegates the creation of a {@link TagsetEntry}
	 * to the factory implementation.
	 * 
	 * @param layers The list of layers of the entry
	 * @param elementTypes The list of element types of the entry
	 * @param namespaces The list of namespaces of the entry
	 * @param names The list of annotation names of the entry
	 * @param values The list of annotation values of the entry
	 * @param tagset The tagset containing this tagset entry
	 * 
	 * @return the tagset entry built by the factory implementation.
	 */
	public static TagsetEntry createTagsetEntry(List<String> layers, List<SALT_TYPE> elementTypes, List<String> namespaces, List<String> names, List<TagsetValue> values, Tagset tagset) {
		return factory.createTagsetEntry(layers, elementTypes, namespaces, names, values, tagset);
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
	public static TagsetFactory newTagsetEntry(Tagset tagset, TagsetValue ... values) {
		TagsetFactory self = new TagsetFactory();
		self.values = values;
		return self;
	}
	
	/**
	 * Configures the factory with layers for the entry.
	 * 
	 * @param layers The layers to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withLayers(String ... layers) {
		this.layers = layers;
		return this;
	}
	
	/**
	 * Configures the factory with element types for the entry.
	 * 
	 * @param elementTypes The element types to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withElementTypes(SALT_TYPE ... elementTypes) {
		this.elementTypes = elementTypes;
		return this;
	}
	
	/**
	 * Configures the factory with namespaces for the entry.
	 * 
	 * @param namespaces The namespaces to include in the tagset entry build
	 * @return the configured {@link TagsetFactory}
	 */
	public TagsetFactory withNamespaces(String ... namespaces) {
		this.namespaces = namespaces;
		return this;
	}
	
	/**
	 * Configures the factory with names for the entry.
	 * 
	 * @param names The names to include in the tagset entry build
	 * @return the configures {@link TagsetFactory}
	 */
	public TagsetFactory withNames(String ... names) {
		this.names = names;
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
		return factory.createTagsetEntry(layers == null ? null : Arrays.asList(layers),
				elementTypes == null ? null : Arrays.asList(elementTypes),
				namespaces == null ? null : Arrays.asList(namespaces), 
				names == null ? null : Arrays.asList(names),
				values == null ? null : Arrays.asList(values), 
				tagset);
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

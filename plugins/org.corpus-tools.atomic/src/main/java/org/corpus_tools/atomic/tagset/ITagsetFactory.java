/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.util.List;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;

/**
 * A factory for creating tagset-related objects.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface ITagsetFactory {
	
	/**
	 * Creates a new instance of an implementation of {@link Tagset}
	 * for a specific {@link SCorpus}.
	 * 
	 * @param corpus The corpus this tagset is for
	 * @param name The name of the tagset
	 * 
	 * @return the created tagset
	 */
	public Tagset createTagset(SCorpus corpus, String name);
	
	/**
	 * Creates a new instance of an implementation of {@link TagsetEntry}.
	 * 
	 * @param layers The list of layers for this tagset entry 
	 * @param elementTypes The list of Salt model element types for this tagset entry
	 * @param namespaces The list of namespaces for this tagset entry
	 * @param names The list of annotation names for this tagset entry 
	 * @param values The list of valid values and their descriptions for this tagset entry
	 * @param tagset The tagset containing this tagset entry
	 * 
	 * @return the created tagset entry
	 */
	public TagsetEntry createTagsetEntry(List<String> layers, List<SALT_TYPE> elementTypes, List<String> namespaces, List<String> names, List<TagsetValue> values, Tagset tagset);
	
	/**
	 * Creates a new instance of an implementation of {@link TagsetValue}.
	 * 
	 * @param value The actual annotation value for this tagset value 
	 * @param description A description for the tagset value 
	 * 
	 * @return the created tagset value
	 */
	public TagsetValue createTagsetValue(String value, String description);

}

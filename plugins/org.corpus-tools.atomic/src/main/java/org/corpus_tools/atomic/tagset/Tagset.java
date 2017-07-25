/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.io.Serializable;
import java.util.Set;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.emf.common.util.URI;

/**
 * A set of valid {@link TagsetValue}s for a given corpus.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface Tagset extends Serializable {

	boolean addValue(TagsetValue value);
	
	boolean removeValue(TagsetValue value);
	
	void setValues(Set<TagsetValue> values);
	
	/**
	 * // TODO Add description
	 * 
	 * @return the set of **valid** values for this tagset's corpus
	 */
	Set<TagsetValue> getValues();
	
	Set<TagsetValue> getValuesForParameters(String layer, SALT_TYPE elementType, String namespace, String name);
	
	/**
	 * Saves the tagset at the given URI
	 * 
	 * @param uri The URI where the tagset should be saved
	 * @return `true` if the tagset has been successfully save at the given URI
	 */
	boolean save(URI uri);
	
	/**
	 * @return the name of the tagset
	 */
	String getName();
	
	/**
	 * Sets the name of the tagset
	 * 
	 * @param name The tagset's name
	 */
	void setName(String name);
	
	/**
	 * Sets the {@link SCorpus} for which this
	 * tagset provides valid annotation values.
	 * 
	 * @param corpus The corpus this tagset is for
	 */
	void setCorpus(SCorpus corpus);
	
	/**
	 * @return the {@link SCorpus} this tagset is for
	 */
	SCorpus getCorpus();
	
}

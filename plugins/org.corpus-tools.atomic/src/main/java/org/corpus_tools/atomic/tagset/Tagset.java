/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.corpus_tools.salt.SALT_TYPE;
import org.eclipse.emf.common.util.URI;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A set of valid {@link TagsetValue}s for a given corpus.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
@JsonDeserialize(using = TagsetDeserializer.class)
public interface Tagset extends Serializable {

	boolean addValue(TagsetValue value);
	
	boolean removeValue(TagsetValue value);
	
	void setValues(List<TagsetValue> values);
	
	/**
	 * // TODO Add description
	 * 
	 * @return the set of **valid** values for this tagset's corpus
	 */
	List<TagsetValue> getValues();
	
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
	 * Sets the identifier id of the corpus for which this
	 * tagset provides valid annotation values.
	 * 
	 * @param corpusId The identifier id of corpus this tagset is for
	 */
	void setCorpusId(String corpusId);
	
	/**
	 * @return the identifier id of the corpus this tagset is for
	 */
	String getCorpusId();
	
}

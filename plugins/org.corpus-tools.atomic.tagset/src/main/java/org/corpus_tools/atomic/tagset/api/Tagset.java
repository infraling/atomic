/**
 * 
 */
package org.corpus_tools.atomic.tagset.api;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.tagset.TagsetDeserializer;
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
	
	boolean addValue(int index, TagsetValue value);
	
	boolean removeValue(TagsetValue value);
	
	void setValues(List<TagsetValue> values);
	
	/**
	 * // TODO Add description
	 * 
	 * @return the list of **valid** values for this tagset's corpus
	 */
	List<TagsetValue> getValues();
	
	/**
	 * Gets the complete set of **valid** values for the given parameters,
	 * i.e., all annotation values for which 
	 * 
	 * - the tagset layer name equals the given layer name (i.e., the name of 
	 * one of the layers the annotation's container is added to, and those
	 * for which the tagset layer name is `null`, i.e., not restricted
	 * - the tagset element type equals the given element type (i.e.,
	 * the {@link SALT_TYPE} of the annotation's container, and those for
	 * which the tagset element type is `null`, i.e., not restricted
	 * - the tagset annotation namespace equals the namespace of the
	 * annotation, and those for which the namespace is `null`, i.e., 
	 * not restricted
	 * - for which the tagset annotation name equals the annotations's
	 * name.  
	 * 
	 * @param layer The name of one of the layers the annotation's container is added to
	 * @param elementType The {@link SALT_TYPE} of the annotation's container
	 * @param namespace The annotation's namespace
	 * @param name The annotation's name
	 * @return the complete set of **valid** values for the combination of given parameters 
	 */
	Set<TagsetValue> getValuesForParameters(String layer, SALT_TYPE elementType, String namespace, String name);
	
	/**
	 * Gets a complete set of **valid** annotation names for the given
	 * parameters.
	 * 
	 * @param layer
	 * @param elementType
	 * @param namespace
	 * @return the complete set of **valid** annotation names for the given parameters
	 */
	Set<String> getAnnotationNamesForParameters(String layer, SALT_TYPE elementType, String namespace);
	
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

/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.eclipse.emf.common.util.URI;

/**
 * A tagset defining valid annotation values for combinations of layers, Salt
 * element types, annotation namespaces, and annotation names, where
 * 
 * - layers are lists of {@link SLayer}s defined as per
 * {@link SLayer#getName()}, or regular expression {@link String}s filtering
 * layer names. Layers can be `null` if the defined valid values should be
 * available for annotatables across all layers; 
 * - Salt elements types are lists of model element types as defined
 * per {@link SALT_TYPE}. Salt element types can be `null` if the defined 
 * valid values should be available for all element types; 
 * - namespaces are lists of {@link SAnnotation}
 * namespaces defined as per {@link SAnnotation#getNamespace()}, or regular
 * expression {@link String}s filtering namespace names. Namespaces can be 
 * `null` if the defined valid values should be available for all namespaces, 
 * including `null` namespaces; 
 * - names are lists of {@link SAnnotation} names defined as per
 * {@link SAnnotation#getName()}, or regular expression {@link String}s 
 * filtering annotation names. Names can be `null` if the defined 
 * valid values should be available
 * for all annotation names; 
 * - values are lists of valid {@link SAnnotation}
 * values for the defined combination of layers, Salt element types, namespaces
 * and names. Values are defined as pairs of an
 * {@link SAnnotation#getValue_STEXT()} and a {@link String} providing a
 * description of the value (can be `null`), or pairs of regular expression 
 * {@link String}s filtering valid annotation values against 
 * {@link SAnnotation#getValue()} and a {@link String} providing a 
 * description of the regular expression.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface Tagset extends Serializable {

	/**
	 * Adds a tagset entry to the tagset.
	 * 
	 * @param entry The {@link TagsetEntry} to be added
	 * @return `true` if the tagset has changed as a result of this call
	 */
	boolean addEntry(TagsetEntry entry);
	
	/**
	 * Removes a tagset entry from the tagset.
	 * 
	 * @param entry The {@link TagsetEntry} to be removed
	 * @return `true` if the entry has been removed as a result of this call
	 */
	boolean removeEntry(TagsetEntry entry);
	
	/**
	 * Removes all tagset entries matching the given parameters from the tagset. 
	 * 
	 * @param layer The layer for the entry to remove
	 * @param elementType The element type for the entry to remove
	 * @param namespace The namespace for the entry to remove
	 * @param name The name for the entry to remove
	 * @return `true` if the entry has been removed as a result of this call
	 */
	boolean removeEntry(String layer, SALT_TYPE elementType, String namespace, String name);
	
	/**
	 * Provides a list of all entries in the tagset.
	 * 
	 * @return a {@link List} of all {@link TagsetEntry}s currently in the tagset
	 */
	Set<TagsetEntry> getEntries();
	
	/**
	 * Retrieves the {@link TagsetEntry} with the given parameters.
	 * 
	 * @param layer The layer name of the {@link TagsetEntry} to retrieve
	 * @param elementType The element type of the {@link TagsetEntry} to retrieve
	 * @param namespace The annotation namespace of the {@link TagsetEntry} to retrieve
	 * @param name The annotation name of the {@link TagsetEntry} to retrieve
	 * @return The {@link TagsetEntry} with the given parameters, or `null` if no such entry exists.
	 */
	TagsetEntry getEntryWithParameters(String layer, SALT_TYPE elementType, String namespace, String name);
	
	/**
	 * Retrieves the valid values for the given combination of layer,
	 * element type, namespace and name.
	 * 
	 * @param layer The layer name for which the returned values are valid
	 * @param elementType The element type for which the returned values are valid
	 * @param namespace The annotation namespace for which the returned values are valid
	 * @param name The annotation name for which the returned values are valid
	 * @return The valid values for the given combination of parameters as a {@link Set}
	 */
	Set<TagsetValue> getValidValues(String layer, SALT_TYPE elementType, String namespace, String name);
	
	/**
	 * Loads a tagset from the provided URI.
	 * 
	 * @param uri The {@link URI} from which to load the tagset
	 * @return The tagset which has been serialized at the given URI, or `null` if no tagset could be loaded
	 */
	Tagset load(URI uri);
	
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

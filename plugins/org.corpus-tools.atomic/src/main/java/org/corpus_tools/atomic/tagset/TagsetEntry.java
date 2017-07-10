/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.util.List;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.core.SLayer;

/**
 * An entry in a {@link Tagset}.
 * 
 * A {@link TagsetEntry} provides valid values of type 
 * {@link TagsetValue} for specific constraint which is
 * a combination of layers, Salt element types, 
 * annotation namespaces and annotation names for
 * which a list of annotation values is valid.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
interface TagsetEntry {
	
	/**
	 * @param layers The list of layers for which the entry's values are valid
	 */
	void setLayers(List<String> layers);
	
	/**
	 * @return the list of layers for which the entry's values are valid
	 */
	List<SLayer> getLayers();
	
	/**
	 * @param elementTypes The list of Salt element types for which the entry's values are valid
	 */
	void setElementTypes(List<SALT_TYPE> elementTypes);
	
	/**
	 * @return the list of Salt element types for which the entry's values are valid
	 */
	List<SALT_TYPE> getElementTypes();
	
	/**
	 * @param namespaces The list of annotation namespaces for which the entry's values are valid
	 */
	void setNamespaces(List<String> namespaces);
	
	/**
	 * @return the list of annotation namespaces for which the entry's values are valid
	 */
	List<String> getNamespaces();
	
	/**
	 * @param names The list of annotation names for which the entry's values are valid
	 */
	void setNames(List<String> names);
	
	/**
	 * @return the list of annotation names for which the entry's values are valid
	 */
	List<String> getNames();
	
	/**
	 * @param values The list of values which are valid while honouring the constraints of this entry
	 */
	void setValidValues(List<TagsetValue> values);
	
	/**
	 * @return the list of values which are valid while honouring the constraints of this entry
	 */
	List<TagsetValue> getValidValues();

}

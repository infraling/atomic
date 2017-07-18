/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.util.Set;

import org.corpus_tools.salt.SALT_TYPE;

/**
 * An entry in a {@link Tagset}.
 * 
 * A {@link TagsetEntry} provides valid values of type 
 * {@link TagsetValue} for a specific constraint which is
 * a combination of layer name, Salt element type, 
 * annotation namespace and annotation name for
 * which a set of annotation values is valid.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface TagsetEntry {
	
	/**
	 * @param layer The name of the layer for which the entry's values are valid
	 */
	void setLayer(String layer);
	
	/**
	 * @return the name of the layer for which the entry's values are valid
	 */
	String getLayer();
	
	/**
	 * @param elementType The Salt element type for which the entry's values are valid
	 */
	void setElementType(SALT_TYPE elementType);
	
	/**
	 * @return the Salt element type for which the entry's values are valid
	 */
	SALT_TYPE getElementType();
	
	/**
	 * @param namespace The annotation namespace for which the entry's values are valid
	 */
	void setNamespace(String namespace);
	
	/**
	 * @return the annotation namespace for which the entry's values are valid
	 */
	String getNamespace();
	
	/**
	 * @param name The annotation name for which the entry's values are valid
	 */
	void setName(String name);
	
	/**
	 * @return the annotation name for which the entry's values are valid
	 */
	String getName();
	
	/**
	 * @param values The values which are valid while honouring the constraints of this entry
	 */
	void setValidValues(TagsetValue... values);
	
	/**
	 * @return the values which are valid while honouring the constraints of this entry
	 */
	Set<TagsetValue> getValidValues();
	
	/**
	 * @param tagset The tagset which contains this tagset entry
	 */
	void setTagset(Tagset tagset);
	
	/**
	 * @return the tagset which contains this tagset entry
	 */
	Tagset getTagset();

}

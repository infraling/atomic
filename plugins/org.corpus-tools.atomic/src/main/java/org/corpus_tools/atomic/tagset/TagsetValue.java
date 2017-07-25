/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.core.SLayer;

/**
 * A valid value in a {@link Tagset}.
 * 
 * A {@link TagsetValue} consists of
 * 
 * - a value ({@link String}), which represents the actual value; this
 * value can be a fixed string or a regular expression
 * - a description ({@link String}), which describes the domain properties of the value
 * - a layer name ({@link String}), which represents the name of the list of
 * {@link SLayer}s for which the value is valid
 * - an element type ({@link SALT_TYPE}) for which the value is valid
 * - an annotation namespace ({@link String}), for which the value is valid
 * - an annotation name ({@link String}), for which the value is valid.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public abstract interface TagsetValue {
	
	/**
	 * @param value The value to set
	 */
	void setValue(String value);
	
	/**
	 * @return the value
	 */
	String getValue();
	
	/**
	 * @return whether the value of this tagset value is a regular expression
	 */
	boolean isRegularExpression();
	
	/**
	 * @param isRegularExpression Whether the value of this tagset value is a regular expression
	 */
	void setRegularExpression(boolean regularExpression);

	/**
	 * @param description The description of the value
	 */
	void setDescription(String description);
	
	/**
	 * @return the value description
	 */
	String getDescription();
	
	void setLayer(String layerName);
	
	String getLayer();
	
	void setElementType(SALT_TYPE elementType);
	
	SALT_TYPE getElementType();
	
	void setNamespace(String annotationNamespace);
	
	String getNamespace();
	
	void setName(String annotationName);
	
	String getName();
}

/**
 * 
 */
package org.corpus_tools.atomic.tagset;

/**
 * A tagset value which consists of a value and a 
 * description of the value.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
interface TagsetValue {
	
	/**
	 * @param value The value to set
	 */
	void setValue(String value);
	
	/**
	 * @return the value
	 */
	String getValue();
	
	/**
	 * @param description The description of the value
	 */
	void setDescription(String description);
	
	/**
	 * @return the value description
	 */
	String getDescription();
	
}

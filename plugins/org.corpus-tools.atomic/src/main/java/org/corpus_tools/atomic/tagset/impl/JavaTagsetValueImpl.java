/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import org.corpus_tools.atomic.tagset.TagsetValue;

/**
 * A simple implementation of a {@link TagsetValue}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class JavaTagsetValueImpl implements TagsetValue {

	private String value;
	private String description;
	private boolean regexValue;

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.value = value;

	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#isRegexValue()
	 */
	@Override
	public boolean isRegexValue() {
		return regexValue;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#setRegexValue(boolean)
	 */
	@Override
	public void setRegexValue(boolean isRegexValue) {
		this.regexValue = isRegexValue;
	}

}

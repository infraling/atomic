/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class BooleanParameterValueConverter extends AbstractParameterValueConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToObject(java.lang.String)
	 */
	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
		return Boolean.parseBoolean(parameterValue);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException {
		return Boolean.toString((Boolean) parameterValue);
	}

}

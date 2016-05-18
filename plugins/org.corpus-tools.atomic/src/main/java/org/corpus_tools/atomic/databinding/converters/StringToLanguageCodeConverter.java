/*******************************************************************************
 * Copyright 2016 Stephan Druskat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.databinding.converters;

import org.eclipse.core.databinding.conversion.Converter;

import com.neovisionaries.i18n.LanguageCode;

/**
 * A converter for mapping a String to a {@link LanguageCode}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class StringToLanguageCodeConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public StringToLanguageCodeConverter(Object fromType, Object toType) {
		super(String.class, LanguageCode.class);
	}

	/* 
	 * @copydoc @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof String) {
			return LanguageCode.getByCode((String) fromObject);
		}
		return null;
	}

}

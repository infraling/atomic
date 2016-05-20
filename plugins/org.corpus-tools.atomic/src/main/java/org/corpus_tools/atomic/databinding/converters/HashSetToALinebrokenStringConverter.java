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

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * A converter for mapping the entries of a {@link HashSet} to an
 * alphabetically ordered String, with one value from the {@link HashSet}
 * per line.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class HashSetToALinebrokenStringConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public HashSetToALinebrokenStringConverter(Object fromType, Object toType) {
		super(HashSet.class, String.class);
	}

	/* 
	 * @copydoc @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof HashSet) {
			HashSet<String> set = (HashSet<String>) fromObject;
			StringBuilder builder = new StringBuilder();
			Iterator iterator = set.iterator();
			while (iterator.hasNext()) {
				Object object = (Object) iterator.next();
				builder.append(object.toString() + "\n");
			}
			String string = builder.toString();
			return string.substring(string.length() - 2, string.length() - 1).trim().equals("\n") ? string.substring(0, string.length() - 2).trim() : string.trim(); 
		}
		return null;
	}

}

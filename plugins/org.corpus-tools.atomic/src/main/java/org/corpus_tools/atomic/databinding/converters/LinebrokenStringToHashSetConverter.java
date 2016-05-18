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
import java.util.StringTokenizer;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * A converter for mapping an String with line breaks to
 * a {@link HashSet}, with one trimmed line sans the line break going to
 * one HashSet entry. 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class LinebrokenStringToHashSetConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public LinebrokenStringToHashSetConverter(Object fromType, Object toType) {
		super(String.class, HashSet.class);
	}

	/* 
	 * @copydoc @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof String) {
			String string = (String) fromObject;
			HashSet<String> set = new HashSet<>(countLines(string));
			StringTokenizer tokenizer = new StringTokenizer(string, "\n");
			while(tokenizer.hasMoreTokens()) {
			   set.add(tokenizer.nextToken());
			}
			return set;
		}
		return null;
	}

	/**
	 * Counts the number of "\n"-delimited lines in a String.
	 *
	 * @param string
	 * @return number of lines in parameter
	 */
	private int countLines(String string) {

		if (string == null || string.isEmpty()) {
			return 0;
		}
		int lines = 1;
		int pos = 0;
		while ((pos = string.indexOf("\n", pos) + 1) != 0) {
			lines++;
		}
		return lines;
	}

}

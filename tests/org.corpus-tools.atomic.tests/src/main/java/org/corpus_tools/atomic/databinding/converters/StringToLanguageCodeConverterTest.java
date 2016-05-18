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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

/**
 * Unit tests for {@link StringToLanguageCodeConverter}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class StringToLanguageCodeConverterTest {
	
	StringToLanguageCodeConverter fixture = null;

	/**
	 * Sets up fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new StringToLanguageCodeConverter(String.class, LanguageCode.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.databinding.converters.StringToLanguageCodeConverter#convert(java.lang.Object)}.
	 */
	@Test
	public void testConvert() {
		Object afar = getFixture().convert("Afar");
		Object maori = getFixture().convert("Māori");
		Object westFrisian = getFixture().convert("West Frisian");
		assertEquals(LanguageCode.aa.getAlpha3().getAlpha2(), afar);
		assertEquals(LanguageCode.mi.getAlpha3().getAlpha2(), maori);
		assertEquals(LanguageCode.fy.getAlpha3().getAlpha2(), westFrisian);
	}

	/**
	 * @return the fixture
	 */
	private StringToLanguageCodeConverter getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(StringToLanguageCodeConverter fixture) {
		this.fixture = fixture;
	}

}

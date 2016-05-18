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
 * Unit tests for {@link LanguageCodeToStringConverter}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class LanguageCodeToStringConverterTest {
	
	LanguageCodeToStringConverter fixture = null;

	/**
	 * Sets up fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new LanguageCodeToStringConverter(LanguageCode.class, String.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.databinding.converters.LanguageCodeToStringConverter#convert(java.lang.Object)}.
	 */
	@Test
	public void testConvert() {
		Object afar = getFixture().convert(LanguageCode.aa);
		Object maori = getFixture().convert(LanguageCode.mi);
		Object westernFrisian = getFixture().convert(LanguageCode.fy);
		assertEquals("Afar", afar);
		assertEquals("Māori", maori);
		assertEquals("West Frisian", westernFrisian);
	}

	/**
	 * @return the fixture
	 */
	private LanguageCodeToStringConverter getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(LanguageCodeToStringConverter fixture) {
		this.fixture = fixture;
	}

}

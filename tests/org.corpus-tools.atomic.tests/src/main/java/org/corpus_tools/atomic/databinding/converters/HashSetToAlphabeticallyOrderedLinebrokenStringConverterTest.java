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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link HashSetToALinebrokenStringConverter}
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class HashSetToAlphabeticallyOrderedLinebrokenStringConverterTest {
	
	HashSetToALinebrokenStringConverter fixture = null;

	/**
	 * Sets up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new HashSetToALinebrokenStringConverter(HashSet.class, String.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.databinding.converters.HashSetToALinebrokenStringConverter#convert(java.lang.Object)}.
	 */
	@Test
	public void testConvert() {
		HashSet<String> set = new LinkedHashSet<>(Arrays.asList(new String[]{"One", "Two", "Three"}));
		Object convertedObject = getFixture().convert(set);
		assertNotNull(convertedObject);
		assertTrue(convertedObject instanceof String);
		assertEquals("One\nTwo\nThree", convertedObject);
	}

	/**
	 * @return the fixture
	 */
	private HashSetToALinebrokenStringConverter getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(HashSetToALinebrokenStringConverter fixture) {
		this.fixture = fixture;
	}

}

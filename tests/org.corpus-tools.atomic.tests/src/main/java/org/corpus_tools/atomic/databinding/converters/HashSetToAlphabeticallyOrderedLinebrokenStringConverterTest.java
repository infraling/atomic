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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link HashSetToAlphabeticallyOrderedLinebrokenStringConverter}
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class HashSetToAlphabeticallyOrderedLinebrokenStringConverterTest {
	
	HashSetToAlphabeticallyOrderedLinebrokenStringConverter fixture = null;

	/**
	 * Sets up the fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new HashSetToAlphabeticallyOrderedLinebrokenStringConverter(HashSet.class, String.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.databinding.converters.HashSetToAlphabeticallyOrderedLinebrokenStringConverter#convert(java.lang.Object)}.
	 */
	@Test
	public void testConvert() {
		HashSet<String> set = new HashSet<>(Arrays.asList(new String[]{"One", "Two", "Three"}));
		Object convertedObject = getFixture().convert(set);
		assertNotNull(convertedObject);
		assertTrue(convertedObject instanceof String);
		assertEquals("One\nThree\nTwo", convertedObject);
	}

	/**
	 * @return the fixture
	 */
	private HashSetToAlphabeticallyOrderedLinebrokenStringConverter getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(HashSetToAlphabeticallyOrderedLinebrokenStringConverter fixture) {
		this.fixture = fixture;
	}

}

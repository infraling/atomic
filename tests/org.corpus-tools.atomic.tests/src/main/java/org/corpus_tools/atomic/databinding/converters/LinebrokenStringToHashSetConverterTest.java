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
 * Unit tests for {@link LinebrokenStringToHashSetConverter}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class LinebrokenStringToHashSetConverterTest {
	
	LinebrokenStringToHashSetConverter fixture = null;

	/**
	 * Sets up the fixture
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new LinebrokenStringToHashSetConverter(String.class, HashSet.class));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.databinding.converters.LinebrokenStringToHashSetConverter#convert(java.lang.Object)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testConvert() {
		Object convertedObject = getFixture().convert("One\nTwo\nThree\n");
		assertNotNull(convertedObject);
		assertTrue(convertedObject instanceof HashSet);
		HashSet<String> set = (HashSet<String>) convertedObject;
		assertEquals(3, set.size());
		assertTrue(set.containsAll(Arrays.asList(new String[]{"One", "Two", "Three"})));
	}

	/**
	 * @return the fixture
	 */
	private LinebrokenStringToHashSetConverter getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(LinebrokenStringToHashSetConverter fixture) {
		this.fixture = fixture;
	}

}

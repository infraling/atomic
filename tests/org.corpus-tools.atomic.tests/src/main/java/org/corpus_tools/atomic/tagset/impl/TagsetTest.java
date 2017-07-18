/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

//import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.emf.common.util.URI;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Tagset}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetTest {
	
	private Tagset fixture = null;
	
	/**
	 * Sets up the fixture.
	 */
	@Before
	public void setUp() {
		SCorpus corpus = SaltFactory.createSCorpus();
		this.setFixture(TagsetFactory.createTagset(corpus, "test"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#addEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testFluentEntries() {
		getFixture().addEntry(TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2")).withLayer("layer1").withElementType(SALT_TYPE.STOKEN).withNamespace("namespace1").withName("name1").build());
		assertEquals(1, getFixture().getEntries().size());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#addEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testTradEntries() {
		TagsetEntry entry1 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2")).withLayer("layer1").withElementType(SALT_TYPE.STOKEN).withNamespace("namespace1").withName("name1").build();
		getFixture().addEntry(entry1);
		assertEquals(1, getFixture().getEntries().size());
		TagsetEntry entry2 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value3", "Value 3"), TagsetFactory.createTagsetValue("value4", "Value 4")).withLayer("layer1").withElementType(SALT_TYPE.SSPAN).withNamespace("namespace2").withName("name2").build();
		getFixture().addEntry(entry2);
		assertEquals(2, getFixture().getEntries().size());
		Set<TagsetValue> retrievedValues1 = getFixture().getValidValues("layer1", SALT_TYPE.STOKEN, "namespace1", "name1");
		assertThat(retrievedValues1, containsInAnyOrder(hasProperty("value", is("value1")), hasProperty("value", is("value2"))));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#removeEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testRemoveEntry() {
		// FIXME fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#load(org.eclipse.emf.common.util.URI)}.
	 */
	@Test
	public final void testSaveAndLoad() {
		String userHome = System.getProperty("user.home");
		String path = userHome + "/atomic-tagset.ats";
		getFixture().save(URI.createFileURI(path));
		Tagset success = getFixture().load(URI.createFileURI(path));
		assertThat(success, instanceOf(Tagset.class));
		try {
			Files.delete(Paths.get(path));
		}
		catch (

		NoSuchFileException x) {
			fail(path + ": no such file or directory");
		}
		catch (DirectoryNotEmptyException x) {
			fail(path + " not empty");
		}
		catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#save(org.eclipse.emf.common.util.URI)}.
	 */
	@Test
	public final void testSave() {
		String userHome = System.getProperty("user.home");
		String path = userHome + "/atomic-tagset.ats";
		boolean success = getFixture().save(URI.createFileURI(path));
		assertTrue(Files.exists(Paths.get(path)));
		assertTrue(success);
		try {
			Files.delete(Paths.get(path));
		}
		catch (

		NoSuchFileException x) {
			fail(path + ": no such file or directory");
		}
		catch (DirectoryNotEmptyException x) {
			fail(path + " not empty");
		}
		catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals(null, getFixture().getName());
		getFixture().setName("name");
		assertEquals("name", getFixture().getName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		getFixture().setName("name");
		assertEquals("name", getFixture().getName());
	}

	/**
	 * @return the fixture
	 */
	private final Tagset getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private final void setFixture(Tagset fixture) {
		this.fixture = fixture;
	}

}

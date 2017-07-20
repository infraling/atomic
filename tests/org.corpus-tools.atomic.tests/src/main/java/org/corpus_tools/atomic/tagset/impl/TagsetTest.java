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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.emf.common.util.URI;
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
	@SuppressWarnings("unchecked")
	@Test
	public final void testEntries() {
		TagsetEntry entry1 = TagsetFactory.createTagsetEntry(getFixture(), "layer1", SALT_TYPE.STOKEN, "namespace1", "name1", TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2"));
		getFixture().addEntry(entry1);
		assertEquals(1, getFixture().getEntries().size());
		assertThat(entry1.getValidValues(), containsInAnyOrder(hasProperty("value", is("value1")), hasProperty("value", is("value2"))));
		TagsetEntry entry1_1 = TagsetFactory.createTagsetEntry(getFixture(), "layer1", SALT_TYPE.STOKEN, "namespace1", "name1", TagsetFactory.createTagsetValue("value3", "Value 3"), TagsetFactory.createTagsetValue("value4", "Value 4"));
		getFixture().addEntry(entry1_1);
		TagsetEntry entry2 = TagsetFactory.createTagsetEntry(getFixture(), "layer1", SALT_TYPE.SSPAN, "namespace2", "name2", TagsetFactory.createTagsetValue("value3", "Value 3"), TagsetFactory.createTagsetValue("value4", "Value 4"));
		assertEquals(1, getFixture().getEntries().size());
		assertThat(entry1.getValidValues(), containsInAnyOrder(hasProperty("value", is("value1")), hasProperty("value", is("value2")), hasProperty("value", is("value3")), hasProperty("value", is("value4"))));
		assertEquals(4, getFixture().getEntries().iterator().next().getValidValues().size());
		getFixture().addEntry(entry2);
		assertEquals(2, getFixture().getEntries().size());
		TagsetEntry nullEntry = TagsetFactory.createTagsetEntry(getFixture(), null, null, null, "name3", TagsetFactory.createTagsetValue("value5", "Value 5"), TagsetFactory.createTagsetValue("value6", "Value 6"));
		getFixture().addEntry(nullEntry);
		assertEquals(3, getFixture().getEntries().size());
		Set<TagsetValue> retrievedValues1 = getFixture().getValidValues("layer1", SALT_TYPE.STOKEN, "namespace1", "name1");
		assertThat(retrievedValues1, containsInAnyOrder(hasProperty("value", is("value1")), hasProperty("value", is("value2")), hasProperty("value", is("value3")), hasProperty("value", is("value4"))));
		Set<TagsetValue> retrievedValues2 = getFixture().getValidValues("layer1", SALT_TYPE.SSPAN, "namespace2", "name2");
		assertThat(retrievedValues2, containsInAnyOrder(hasProperty("value", is("value3")), hasProperty("value", is("value4"))));
		Set<TagsetValue> retrievedValues3 = getFixture().getValidValues(null, null, null, "name3");
		assertThat(retrievedValues3, containsInAnyOrder(hasProperty("value", is("value5")), hasProperty("value", is("value6"))));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#addEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testFluentEntries() {
		TagsetEntry entry1 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2")).withLayer("layer1").withElementType(SALT_TYPE.STOKEN).withNamespace("namespace1").withName("name1").build();
		getFixture().addEntry(entry1);
		assertEquals(1, getFixture().getEntries().size());
		TagsetEntry entry2 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value3", "Value 3"), TagsetFactory.createTagsetValue("value4", "Value 4")).withLayer("layer1").withElementType(SALT_TYPE.SSPAN).withNamespace("namespace2").withName("name2").build();
		getFixture().addEntry(entry2);
		assertEquals(2, getFixture().getEntries().size());
		TagsetEntry nullEntry = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value5", "Value 5"), TagsetFactory.createTagsetValue("value6", "Value 6")).withLayer(null).withElementType(null).withNamespace(null).withName("name3").build();
		getFixture().addEntry(nullEntry);
		assertEquals(3, getFixture().getEntries().size());
		Set<TagsetValue> retrievedValues1 = getFixture().getValidValues("layer1", SALT_TYPE.STOKEN, "namespace1", "name1");
		assertThat(retrievedValues1, containsInAnyOrder(hasProperty("value", is("value1")), hasProperty("value", is("value2"))));
		Set<TagsetValue> retrievedValues2 = getFixture().getValidValues("layer1", SALT_TYPE.SSPAN, "namespace2", "name2");
		assertThat(retrievedValues2, containsInAnyOrder(hasProperty("value", is("value3")), hasProperty("value", is("value4"))));
		Set<TagsetValue> retrievedValues3 = getFixture().getValidValues(null, null, null, "name3");
		assertThat(retrievedValues3, containsInAnyOrder(hasProperty("value", is("value5")), hasProperty("value", is("value6"))));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#removeEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testRemoveEntry() {
		TagsetEntry entry1 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2")).withLayer("layer1").withElementType(SALT_TYPE.STOKEN).withNamespace("namespace1").withName("name1").build();
		getFixture().addEntry(entry1);
		assertEquals(1, getFixture().getEntries().size());
		getFixture().removeEntry(entry1);
		assertTrue(getFixture().getEntries().isEmpty());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#removeEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testRemoveEntryViaParameters() {
		TagsetEntry entry1 = TagsetFactory.newTagsetEntry(getFixture(), TagsetFactory.createTagsetValue("value1", "Value 1"), TagsetFactory.createTagsetValue("value2", "Value 2")).withLayer("layer1").withElementType(SALT_TYPE.STOKEN).withNamespace("namespace1").withName("name1").build();
		getFixture().addEntry(entry1);
		assertEquals(1, getFixture().getEntries().size());
		getFixture().removeEntry("layer1", SALT_TYPE.STOKEN, "namespace1", "name1");
		assertTrue(getFixture().getEntries().isEmpty());
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

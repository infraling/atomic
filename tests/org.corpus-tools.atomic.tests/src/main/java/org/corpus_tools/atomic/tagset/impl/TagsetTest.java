/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl;
import org.eclipse.emf.common.util.URI;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link JavaTagsetImplTest}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class JavaTagsetImplTest {
	
	private JavaTagsetImpl fixture = null;
	
	/**
	 * Sets up the fixture.
	 */
	@Before
	public void setUp() {
		
		this.setFixture(TagsetFactory.createTagset(graph, "test"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#addEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testAddEntry() {
		// FIXME fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#removeEntry(org.corpus_tools.atomic.tagset.TagsetEntry)}.
	 */
	@Test
	public final void testRemoveEntry() {
		// FIXME fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl#getEntries()}.
	 */
	@Test
	public final void testGetEntries() {
		assertTrue(getFixture().getEntries().isEmpty());
//		getFixture().addEntry(ITagsetFactory)
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
		assertThat(success, IsInstanceOf.instanceOf(Tagset.class));
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
	private final JavaTagsetImpl getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private final void setFixture(JavaTagsetImpl fixture) {
		this.fixture = fixture;
	}

}

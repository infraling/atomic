/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.api.Tagset;
import org.corpus_tools.atomic.tagset.api.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A simple implementation of a {@link ITagsetFactory}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
final class JavaTagsetFactoryImpl implements ITagsetFactory {
	
	private static final Logger log = LogManager.getLogger(JavaTagsetFactoryImpl.class);
	private static final String TAGSET_FILE_ENDING = "ats";

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.ITagsetFactory#createTagset(org.corpus_tools.salt.common.SCorpus, java.lang.String)
	 */
	@Override
	public Tagset createTagset(String corpus, String name) {
		return new JavaTagsetImpl(corpus, name);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.Tagset#load(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Tagset load(URI uri) {
		JavaTagsetImpl tagset = null;
		String path = uri.toFileString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tagset = mapper.readValue(new File(path), JavaTagsetImpl.class);
		}
		catch (IOException e) {
			log.error("An error occurred while reading the tagset from the tagset file {}!", path, e);
		}
		return tagset;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.ITagsetFactory#createTagsetValue(java.lang.String, org.corpus_tools.salt.SALT_TYPE, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public TagsetValue createTagsetValue(String layer, SALT_TYPE elementType, String namespace, String name,
			String value, boolean isRegularExpression, String description) {
		return new JavaTagsetValueImpl(layer, elementType, namespace, name, value, isRegularExpression, description);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.ITagsetFactory#getTagsetFileName(java.lang.String)
	 */
	@Override
	public String getTagsetFileName(String projectName) {
		Assert.isNotNull(projectName, "Cannot parse null project names.");
		return projectName.replace(" ", "-").concat(".").concat(TAGSET_FILE_ENDING);
	}



}

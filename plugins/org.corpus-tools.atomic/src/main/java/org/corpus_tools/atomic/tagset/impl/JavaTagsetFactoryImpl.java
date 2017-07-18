/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;

/**
 * A simple implementation of a {@link ITagsetFactory}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
final class JavaTagsetFactoryImpl implements ITagsetFactory {

	@Override
	public Tagset createTagset(SCorpus corpus, String name) {
		return new JavaTagsetImpl(corpus, name);
	}

	@Override
	public TagsetValue createTagsetValue(String value, String description) {
		return new JavaTagsetValueImpl();
	}

	@Override
	public TagsetEntry createTagsetEntry(Tagset tagset, String layer, SALT_TYPE elementType, String namespace,
			String name, TagsetValue... values) {
		return new JavaTagsetEntryImpl(tagset, layer, elementType, namespace, name, values);
	}

}

/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import java.util.List; 

import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetEntry;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;

/**
 * // TODO Add description
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
	public TagsetEntry createTagsetEntry(List<String> layers, List<SALT_TYPE> elementTypes, List<String> namespaces,
			List<String> names, List<TagsetValue> values, Tagset tagset) {
		return new JavaTagsetEntryImpl(layers, elementTypes, namespaces, names, values, tagset);
	}

}

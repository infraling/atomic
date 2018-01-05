/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import org.corpus_tools.atomic.tagset.ITagsetFactory;
import org.corpus_tools.atomic.tagset.api.Tagset;
import org.corpus_tools.atomic.tagset.api.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;

/**
 * A factory providing static methods for creating tagset elements.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 */
public class TagsetFactory {
	
	private static final ITagsetFactory factory = new JavaTagsetFactoryImpl();
	
	/**
	 * Delegates loading a {@link Tagset}
	 * to the factory implementation.
	 * 
	 * @param uri The {@link URI} from which to load the tagset
	 * @return the tagset loaded from the given URI
	 */
	public static Tagset load(URI uri) {
		return factory.load(uri);
	}
	
	/**
	 * Delegates the creation of a {@link Tagset}
	 * to the factory implementation.
	 * 
	 * @param corpusId The {@link Identifier} id of the {@link SCorpus} this tagset is for 
	 * @param name The tagset name 
	 * 
	 * @return the tagset built by the factory implementation.
	 */
	public static Tagset createTagset(String corpusId, String name) {
		return factory.createTagset(corpusId, name);
	}
	
	/**
	 * Delegates the creation of a {@link TagsetValue}
	 * to the factory implementation.
	 * 
	 * @param layer The layer for which this tagset value is valid 
	 * @param elementType The Salt model element type for which this tagset value is valid
	 * @param namespace The annotation namespace for which this tagset value is valid
	 * @param name The annotation name for which this tagset value is valid 
	 * @param value The actual annotation value
	 * @param isRegularExpression Whether the value is a regular expression
	 * @param description A decription of the domain properties of the value
	 * 
	 * @return the tagset entry built by the factory implementation.
	 */
	public static TagsetValue createTagsetValue(String layer, SALT_TYPE elementType, String namespace, String name, String value, boolean isRegularExpression, String description) {
		return factory.createTagsetValue(layer, elementType, namespace, name, value, isRegularExpression, description);
	}
	
	/**
	 * Delegates the creation of a valid tagset file name to
	 * the factory implementation. 
	 * 
	 * @param projectName The project name for which to get a valid file name
	 * @return a valid tagset file name
	 */
	public static String getTagsetFileName(String projectName) {
		return factory.getTagsetFileName(projectName);
	}
	
}

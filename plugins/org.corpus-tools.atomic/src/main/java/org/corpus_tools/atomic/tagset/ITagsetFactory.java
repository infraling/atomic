/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.eclipse.emf.common.util.URI;

/**
 * A factory for creating tagset-related objects.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface ITagsetFactory {
	
	/**
	 * Creates a new instance of an implementation of {@link Tagset}
	 * for a specific {@link SCorpus}.
	 * 
	 * @param corpus The corpus this tagset is for
	 * @param name The name of the tagset
	 * 
	 * @return the created tagset
	 */
	public Tagset createTagset(SCorpus corpus, String name);
	
	/**
	 * Creates a new instance of an implementation of {@link TagsetValue}.
	 * 
	 * @param layer The layer for which this tagset value is valid 
	 * @param elementType The Salt model element type for which this tagset value is valid
	 * @param namespace The annotation namespace for which this tagset value is valid
	 * @param name The annotation name for which this tagset value is valid 
	 * @param value The actual annotation value
	 * @param isRegularExpression Whether the value is a regular expression
	 * @param description A decription of the domain properties of the value
	 * 
	 * @return the created tagset value
	 */
	public TagsetValue createTagsetValue(String layer, SALT_TYPE elementType, String namespace,
			String name, String value, boolean isRegularExpression, String description);
	
	/**
	 * Loads a tagset from the provided URI.
	 * 
	 * @param uri The {@link URI} from which to load the tagset
	 * @return The tagset which has been serialized at the given URI, or `null` if no tagset could be loaded
	 */
	Tagset load(URI uri);
	
	/**
	 * Prepares a standardized tagset file name from an
	 * input String.
	 * 
	 * Implementations must make sure that the input
	 * projectName is stripped of path prefixes and
	 * file extensions.
	 * 
	 * @param projectName The project name for which to get a valid file name
	 * @return a valid tagset file name
	 */
	String getTagsetFileName(String projectName);
	
}

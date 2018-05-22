/**
 * 
 */
package org.corpus_tools.atomic.util;

import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SProcessingAnnotation;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class AnnotationUtil {
	
	private static final String NS_N_SEPARATOR = "::";

	public static boolean annotateProcessing(SAnnotationContainer element, String namespace, String name, Object value) {
		SProcessingAnnotation annotation = null;
		if ((annotation = element.getProcessingAnnotation(namespace + NS_N_SEPARATOR + name)) == null) {
			element.createProcessingAnnotation(namespace, name, value);
			return false;
		}
		else {
			annotation.setValue(value);
			return true;
		}
	}

}

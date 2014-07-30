/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.regex.Pattern;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationUtils {
	
	private static final String ANNOTATION_WITH_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:{2}(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ANNOTATION_WITHOUT_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ONE_OR_TWO_COLONS = "(?<!\\\\):{1,2}";

	public static boolean checkInputValidity(String input) {
		if (input.matches(ANNOTATION_WITHOUT_NAMESPACE_REGEX) || input.matches(ANNOTATION_WITH_NAMESPACE_REGEX)) {
			return true;
		}
		return false;
	}

	public static String[] segmentInput(String annotationInput) {
		Pattern oneOrTwoColons = Pattern.compile(ONE_OR_TWO_COLONS);
		return oneOrTwoColons.split(annotationInput);
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationUtils {
	
	private static final String ANNOTATION_WITH_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:{2}(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ANNOTATION_WITHOUT_NAMESPACE_REGEX = "(?:\\\\:|[^:])+:(?:\\\\:|[^:])+";
	private static final String ONE_OR_TWO_COLONS = "(?<!\\\\):{1,2}";
	private static final String XXO_MESSAGE = "An annotation with the key %s and the value %s already exists (in the namespace %s)!\nShould an additional annotation with this key and this value be created in the namespace %s nevertheless?";
	private static final String XOX_MESSAGE = "An annotation with the key %s already exists in the namespace %s!\nShould the current value of this annotation (%s) be overwritten with %s?";

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

	private static MessageDialog createFeedbackDialog(String dialogMessage, String[] dialogButtonLabels) {
		return new MessageDialog(Display.getCurrent().getActiveShell(), "Duplicate annotation!", null, dialogMessage, MessageDialog.QUESTION, dialogButtonLabels, 0);
	}

	public static  boolean checkAnnotationValuesAgainstExisting(SAnnotation model, EList<SAnnotation> existingAnnotations, String namespace, String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

}

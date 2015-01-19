/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

import de.uni_jena.iaa.linktype.atomic.core.Activator;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicAnnotationReservedWordsPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public AtomicAnnotationReservedWordsPreferencesPage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param style
	 * @wbp.parser.constructor
	 */
	public AtomicAnnotationReservedWordsPreferencesPage(int style) {
		super(style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param style
	 */
	public AtomicAnnotationReservedWordsPreferencesPage(String title, int style) {
		super(title, style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public AtomicAnnotationReservedWordsPreferencesPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Reserved words for annotation commands in Atomic: The reserved word is used as the key for certain annotations.\n"
				+ "I.e., if the reserved word for element type is set to \'t\', the respective command to annotate an element with a type would be \"t:<type>\".");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
		StringFieldEditor stringFieldEditor = new StringFieldEditor(PreferenceProvider.STYPE, "Element type", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
		addField(stringFieldEditor);
	}

}

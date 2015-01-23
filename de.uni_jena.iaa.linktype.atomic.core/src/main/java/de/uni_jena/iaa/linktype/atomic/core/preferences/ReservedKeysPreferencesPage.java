/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;

import de.uni_jena.iaa.linktype.atomic.core.Activator;
import de.uni_jena.iaa.linktype.atomic.core.projects.properties.FieldEditorOverlayPage;

/**
 * A field editor preference page for reserved annotations keys for annotation in Atomic.
 * Note that this class also acts as a property page for Atomic projects. In order
 * to do so, it extends {@link de.uni_jena.iaa.linktype.atomic.core.projects.properties.FieldEditorOverlayPage}.
 * 
 * @see de.uni_jena.iaa.linktype.atomic.core.projects.properties.FieldEditorOverlayPage
 * 
 * @author Stephan Druskat
 * 
 */
public class ReservedKeysPreferencesPage extends FieldEditorOverlayPage implements IWorkbenchPreferencePage {
	
	public ReservedKeysPreferencesPage() {
		super(GRID);
	}

	/**
	 * @param style
	 */
	public ReservedKeysPreferencesPage(int style) {
		super(style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param style
	 */
	public ReservedKeysPreferencesPage(String title, int style) {
		super(title, style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public ReservedKeysPreferencesPage(String title, ImageDescriptor image, int style) {
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
		setDescription("Reserved keys for annotation commands in Atomic: The reserved key is used as the key for certain annotations.\n"
				+ "I.e., if the reserved key for element type is set to \'t\', the respective command to annotate an element with a type would be \"t:<type>\".");
	}
	
	public IPreferenceStore doGetPreferenceStore() {
	    return Activator.getDefault().getPreferenceStore();
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
		Composite composite = getFieldEditorParent();
		StringFieldEditor stringFieldEditor = new StringFieldEditor(PreferenceSupplier.STYPE, "Element type", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, composite);
		addField(stringFieldEditor);
	}

	/* (non-Javadoc)
	 * @see de.uni_jena.iaa.linktype.atomic.core.projects.properties.FieldEditorOverlayPage#getPageId()
	 */
	@Override
	protected String getPageId() {
		return "de.uni_jena.iaa.linktype.atomic.core.reservedKeysPreferencePage";
	}

}

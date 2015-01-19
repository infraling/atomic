/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public AtomicPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 */
	public AtomicPreferencePage(String title) {
		super("Atomic Preferences");
	}

	/**
	 * @param title
	 * @param image
	 */
	public AtomicPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Label label = new Label(parent, SWT.BOLD);
		label.setText("Atomic Preferences\n\nChoose sub-category to edit preferences.");
		return label;
	}

}

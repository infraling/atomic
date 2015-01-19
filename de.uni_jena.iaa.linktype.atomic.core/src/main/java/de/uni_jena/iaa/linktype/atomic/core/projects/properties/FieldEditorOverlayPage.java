/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * A common superclass for preference pages that also want to behave as property
 * pages.
 * 
 * Cf. https://eclipse.org/articles/Article-Mutatis-mutandis/overlay-pages.html
 * 
 * @author Stephan Druskat
 * 
 */
public abstract class FieldEditorOverlayPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {
	
	private static final String FALSE = "false"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$

	public FieldEditorOverlayPage(int style) {
		super(style);
	}

	public FieldEditorOverlayPage(String title, int style) {
		super(title, style);
	}

	private ImageDescriptor image;

	public FieldEditorOverlayPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
		this.image = image;
	}

	private IAdaptable element;

	public void setElement(IAdaptable element) {
		this.element = element;
	}

	public IAdaptable getElement() {
		return element;
	}

	public boolean isPropertyPage() {
		return element != null;
	}

	public static final String USEPROJECTSETTINGS = "useProjectSettings";

	private Button useWorkspaceSettingsButton, useProjectSettingsButton,
			configureButton;

	protected Control createContents(Composite parent) {
		if (isPropertyPage())
			createSelectionGroup(parent);
		return super.createContents(parent);
	}

	private void createSelectionGroup(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite radioGroup = new Composite(comp, SWT.NONE);
		radioGroup.setLayout(new GridLayout());
		radioGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useWorkspaceSettingsButton = createRadioButton(radioGroup, "Use workspace settings");
		useProjectSettingsButton = createRadioButton(radioGroup, "Use project settings");
		configureButton = new Button(comp, SWT.PUSH);
		configureButton.setText("Configure Workspace Settings ...");
		configureButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configureWorkspaceSettings();
			}
		});
		try {
			String use = ((IResource) element).getPersistentProperty(new QualifiedName(pageId, USEPROJECTSETTINGS));
			if ("true".equals(use)) {
				useProjectSettingsButton.setSelection(true);
				configureButton.setEnabled(false);
			} else
				useWorkspaceSettingsButton.setSelection(true);
		} catch (CoreException e) {
			useWorkspaceSettingsButton.setSelection(true);
		}
	}

	private Button createRadioButton(Composite parent, String label) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(label);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configureButton.setEnabled(button == useWorkspaceSettingsButton);
				updateFieldEditors();
			}
		});
		return button;
	}

	private List editors = new ArrayList();

	protected void addField(FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}

	private void updateFieldEditors() {
		boolean enabled = useProjectSettingsButton.getSelection();
		updateFieldEditors(enabled);
	}

	protected void updateFieldEditors(boolean enabled) {
		Composite parent = getFieldEditorParent();
		Iterator it = editors.iterator();
		while (it.hasNext()) {
			FieldEditor editor = (FieldEditor) it.next();
			editor.setEnabled(enabled, parent);
		}
	}

	private IPreferenceStore overlayStore;

	private String pageId;

	public void createControl(Composite parent) {
		if (isPropertyPage()) {
			pageId = getPageId();
			overlayStore = new AtomicProjectPropertyStore((IResource) getElement(), super.getPreferenceStore(), pageId);
		}
		super.createControl(parent);
		if (isPropertyPage())
			updateFieldEditors();
	}

	protected abstract String getPageId();

	public IPreferenceStore getPreferenceStore() {
		if (isPropertyPage())
			return overlayStore;
		return super.getPreferenceStore();
	}

	public boolean performOk() {
		boolean result = super.performOk();
		if (result && isPropertyPage()) {
			IResource resource = (IResource) element;
			try {
				String value = (useProjectSettingsButton.getSelection()) ? TRUE : FALSE;
				resource.setPersistentProperty(new QualifiedName(pageId, USEPROJECTSETTINGS), value);
			} catch (CoreException e) {
			}
		}
		return result;
	}

	protected void performDefaults() {
		if (isPropertyPage()) {
			useWorkspaceSettingsButton.setSelection(true);
			useProjectSettingsButton.setSelection(false);
			configureButton.setEnabled(true);
			updateFieldEditors();
		}
		super.performDefaults();
	}

	protected void configureWorkspaceSettings() {
		try {
			IPreferencePage page = (IPreferencePage) this.getClass().newInstance();
			page.setTitle(getTitle());
			page.setImageDescriptor(image);
			showPreferencePage(pageId, page);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	protected void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);
		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(getControl().getShell(), manager);
		BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				dialog.open();
			}
		});
	}

}

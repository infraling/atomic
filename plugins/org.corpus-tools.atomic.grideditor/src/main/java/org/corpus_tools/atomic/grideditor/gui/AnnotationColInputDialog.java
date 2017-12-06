/**
 * 
 */
package org.corpus_tools.atomic.grideditor.gui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.corpus_tools.atomic.models.AbstractBean;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.databinding.dialog.ValidationMessageProvider;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class AnnotationColInputDialog extends TitleAreaDialog {

	private Text txtNamespace;
	private Text txtName;
	private static final String DEFAULT_MESSAGE = "Enter namespace (optional) and name of the annotations in the new column.";
	private static final String INVALID_MESSAGE = "Name/namespace must be a valid string with pattern [A-Za-z][A-Za-z0-9_]*!";
	private final Annotation annotation = new Annotation();

	/**
	 * // TODO Add description
	 * 
	 * @param parentShell
	 */
	public AnnotationColInputDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
    public void create() {
        super.create();
        setTitle("New annotation column");
        setMessage(DEFAULT_MESSAGE);
        if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        Label lblNamespace = new Label(container, SWT.NONE);
        lblNamespace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNamespace.setText("Namespace: ");
        
        txtNamespace = new Text(container, SWT.BORDER);
        txtNamespace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblNenameWLabel = new Label(container, SWT.NONE);
        lblNenameWLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNenameWLabel.setText("Name: ");
        
        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(container, SWT.NONE);
        
        Label lblAllowedValuesazazazaz = new Label(container, SWT.NONE);
        lblAllowedValuesazazazaz.setText("Allowed values: [A-Za-z][A-Za-z0-9_]*");
        
        initDataBindings();
        
        return area;
	}

	/**
	 * // TODO Add description
	 * 
	 * @return
	 */
	private DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		TitleAreaDialogSupport.create(this, bindingContext)
				.setValidationMessageProvider(new ValidationMessageProvider() {
					@Override
					public String getMessage(ValidationStatusProvider statusProvider) {
						if (statusProvider == null) {
							return DEFAULT_MESSAGE;
						}
						return super.getMessage(statusProvider);
					}

					@Override
					public int getMessageType(ValidationStatusProvider statusProvider) {
						int type = super.getMessageType(statusProvider);
						if (getButton(IDialogConstants.OK_ID) != null) {
							getButton(IDialogConstants.OK_ID).setEnabled(type != IMessageProvider.ERROR);
						}
						return type;
					}
				});
		//
		Binding namespaceBinding = bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(txtNamespace), BeanProperties.value(Annotation.class, Annotation.PROPERTY_NAMESPACE).observe(annotation), new UpdateValueStrategy().setBeforeSetValidator(new NamespaceValidator()), null);
		Binding nameBinding = bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(txtName), BeanProperties.value(Annotation.class, Annotation.PROPERTY_NAME).observe(annotation), new UpdateValueStrategy().setBeforeSetValidator(new NameValidator()), null);
		ControlDecorationSupport.create(namespaceBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(nameBinding, SWT.TOP | SWT.LEFT);
		//
		return bindingContext;
	}

	/**
	 * @return the namespace
	 */
	public final String getNamespace() {
		return annotation.getNamespace();
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return annotation.getName();
	}

	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	private class Annotation extends AbstractBean {
		
		public static final String PROPERTY_NAME = "name";
		public static final String PROPERTY_NAMESPACE = "namespace";
		/**
		 * Property <code>name</name>, readable and writable.
		 */
		private String name = null;

		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public void setName(final String name) {
			final String oldName = this.name;
			this.name = name;
			firePropertyChange("name", oldName, this.name);
		}
		
		/**
		 * Property <code>path</name>, readable and writable.
		 */
		private String namespace = null;

		public String getNamespace() {
			return namespace;
		}

		@SuppressWarnings("unused")
		public void setNamespace(final String namespace) {
			final String oldNamespace = this.namespace;
			this.namespace = namespace;
			firePropertyChange("namespace", oldNamespace, this.namespace);
		}

	}
	
	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class NameValidator implements IValidator {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
		 */
		@Override
		public IStatus validate(Object value) {
			if (value instanceof String) {
				String name = (String) value;
				if (value == null || name.isEmpty()) {
					return ValidationStatus.error("Name must not be empty!");
				}
				else if (name.matches("[A-Za-z][A-Za-z0-9_]*")) {
					return ValidationStatus.ok();
				}
				else {
					return ValidationStatus.error(INVALID_MESSAGE);
				}
			}
			return ValidationStatus.error(INVALID_MESSAGE);
		}
		
	}
	
	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class NamespaceValidator implements IValidator {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
		 */
		@Override
		public IStatus validate(Object value) {
			if (value instanceof String) {
				String name = (String) value;
				if (value == null || name.isEmpty()) {
					return ValidationStatus.ok();
				}
				else if (name.matches("[A-Za-z][A-Za-z0-9_]*")) {
					return ValidationStatus.ok();
				}
				else {
					return ValidationStatus.error(INVALID_MESSAGE);
				}
			}
			return ValidationStatus.error(INVALID_MESSAGE);
		}
		
	}
}

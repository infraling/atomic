/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.corpus_tools.atomic.projects.pepper.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Michael Grübsch
 */
public class PepperWizardPageProperties extends WizardPage implements IWizardPage {
	protected final AbstractPepperWizard pepperWizard;

	protected TableViewer tableViewer;

	/**
	 * Creates a new instance of type {@link PepperWizardPageProperties}.
	 * 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public PepperWizardPageProperties(AbstractPepperWizard pepperWizard, String pageName, String title, ImageDescriptor titleImage, String description) {
		super(pageName, title, titleImage);
		setPageComplete(false);
		setDescription(description);

		this.pepperWizard = pepperWizard;
	}

	/*
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		Composite tableComposite = new Composite(container, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);

		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		TableViewerColumn tableViewerColumn;
		TableColumn tableColumn;

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(((PepperModuleProperty<?>) element).getName());
			}
		});

		tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText("Property");

		tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(50));

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(((PepperModuleProperty<?>) element).getValue());
			}
		});

		tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText("Value");

		tableViewerColumn.setEditingSupport(new EditingSupport(tableViewer) {
			TextCellEditor textCellEditor = null;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (textCellEditor == null) {
					textCellEditor = new TextCellEditor((Composite) tableViewer.getControl());
				}
				return textCellEditor;
			}

			@Override
			protected void setValue(Object element, Object value) {
				((PepperModuleProperty<?>) element).setValueString(value != null ? value.toString() : null);
				tableViewer.refresh(element, true);
			}

			@Override
			protected Object getValue(Object element) {
				return ((PepperModuleProperty<?>) element).getValue();
			}
		});

		tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(50));

		ColumnViewerToolTipSupport.enableFor(tableViewer);

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		Table table = tableViewer.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

		Button addButton = new Button(composite, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TextInputDialog textInputDialog = new TextInputDialog(getShell(), "Set Property Name", "Enter the name of the new property. The name must be unique.", "Propery Name: ", "", new TextInputDialog.RequiredTextInputVerifier() {
					@Override
					public boolean verifyText(String text) {
						if (super.verifyText(text)) {
							return !pepperWizard.containsPepperModuleProperty(text.trim());
						}
						else {
							return false;
						}
					}
				});
				if (textInputDialog.open() == Window.OK) {
					PepperModuleProperty<String> property = new PepperModuleProperty<String>(textInputDialog.getInputText(), String.class, "", "");
					property.setValueString("<ENTER VALUE HERE>");
					pepperWizard.addPepperModuleProperty(property);
					tableViewer.add(property);
				}
			}
		});
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		addButton.setText("Add");

		final Button removeButton = new Button(composite, SWT.NONE);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = tableViewer.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					PepperModuleProperty<?> property = (PepperModuleProperty<?>) ((IStructuredSelection) selection).getFirstElement();
					if (property != null) {
						pepperWizard.removePepperModuleProperty(property.getName());
						tableViewer.remove(property);
					}
				}
			}
		});

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				boolean selected = !selection.isEmpty() && selection instanceof IStructuredSelection;
				removeButton.setEnabled(selected);
			}
		});
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			PepperModuleProperties pepperModuleProperties = pepperWizard.getPepperModuleProperties();
			if (pepperModuleProperties != null) {
				Collection<String> propertyNames = pepperModuleProperties.getPropertyNames();
				if (propertyNames != null) {
					List<PepperModuleProperty<?>> propertyList = new ArrayList<PepperModuleProperty<?>>(propertyNames.size());
					for (String propertyName : propertyNames) {
						propertyList.add(pepperModuleProperties.getProperty(propertyName));
					}

					tableViewer.setInput(propertyList);
				}
			}

			setPageComplete(true);
		}

		super.setVisible(visible);
	}
}

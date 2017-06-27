/*******************************************************************************
 * Copyright 2013, 2016 Friedrich Schiller University Jena 
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Michael Grübsch
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 */
public class PepperWizardPageProperties extends WizardPage implements IWizardPage {
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "PepperWizardPageProperties".
	 */
	private static final Logger log = LogManager.getLogger(PepperWizardPageProperties.class);
	
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
			CheckboxCellEditor boolCellEditor = null;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				Object value = getValue(element);
				if (value instanceof String) {
					if (textCellEditor == null) {
						textCellEditor = new TextCellEditor((Composite) tableViewer.getControl());
					}
					return textCellEditor;
				}
				else if (value instanceof Boolean) {
					if (boolCellEditor == null) {
						boolCellEditor = new CheckboxCellEditor((Composite) tableViewer.getControl(),
								SWT.CHECK | SWT.READ_ONLY);
					}
					return boolCellEditor;
				}
				else {
					if (textCellEditor == null) {
						textCellEditor = new TextCellEditor((Composite) tableViewer.getControl());
					}
					return textCellEditor;
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void setValue(Object element, Object value) {
				if (value instanceof String) {
					((PepperModuleProperty<?>) element).setValueString(value != null ? value.toString() : null);
				}
				else if (value instanceof Boolean) {
					((PepperModuleProperty<Boolean>) element).setValue(value != null ? (Boolean) value : null);
				}
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

	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			PepperModuleProperties moduleProperties = pepperWizard.getPepperModuleProperties();
			if (moduleProperties != null) {
				Collection<String> propertyNames = moduleProperties.getPropertyNames();// = pepperModuleProperties.getPropertyNames();
				if (propertyNames != null) {
					List<PepperModuleProperty<?>> propertyList = new ArrayList<>(propertyNames.size());
					for (String propertyName : propertyNames) {
						propertyList.add(moduleProperties.getProperty(propertyName));
					}
					Collections.sort(propertyList, new PropertyNameComparator());
					tableViewer.setInput(propertyList);
				}
			}

			setPageComplete(true);
		}

		super.setVisible(visible);
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class PropertyNameComparator implements Comparator<PepperModuleProperty<?>> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(PepperModuleProperty<?> property1, PepperModuleProperty<?> property2) {
			return property1.getName().compareToIgnoreCase(property2.getName());
		}

	}
}

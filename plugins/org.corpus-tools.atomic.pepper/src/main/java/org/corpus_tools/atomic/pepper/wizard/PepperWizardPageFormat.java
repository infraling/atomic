/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * Michael Gr�bsch
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

package org.corpus_tools.atomic.pepper.wizard;

import java.util.List;

import org.corpus_tools.pepper.common.FormatDesc;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 *
 * @author  Michael Gr�bsch
 * @version $Revision$, $Date$
 */
public class PepperWizardPageFormat extends WizardPage implements IWizardPage
{
  protected final AbstractPepperWizard pepperWizard;

  protected TableViewer tableViewer;

  /**
   * Legt eine neue Instanz des Typs PepperImportWizardPageImporter an.
   * @param pageName
   * @param title
   * @param titleImage
   */
  public PepperWizardPageFormat(AbstractPepperWizard pepperWizard, String pageName, String title, ImageDescriptor titleImage, String description)
  {
    super(pageName, title, titleImage);
    setPageComplete(false);
    setDescription(description);

    this.pepperWizard = pepperWizard;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(Composite parent)
  {
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
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return super.getText(((FormatDesc) element).getFormatName());
      }
    });

    tableColumn = tableViewerColumn.getColumn();
    tableColumn.setText("Format");

    tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(70));

    tableViewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return super.getText(((FormatDesc) element).getFormatVersion());
      }
    });

    tableColumn = tableViewerColumn.getColumn();
    tableColumn.setText("Version");

    tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(30));

    ColumnViewerToolTipSupport.enableFor(tableViewer);

    tableViewer.setContentProvider(ArrayContentProvider.getInstance());
  
    Table table = tableViewer.getTable();

    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        ISelection selection = event.getSelection();

        boolean selected = ! selection.isEmpty() && selection instanceof IStructuredSelection;
        setPageComplete(selected);
        pepperWizard.setFormatDesc(selected ? (FormatDesc) ((IStructuredSelection) selection).getFirstElement() : null);
      }
    });
    
    tableViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        PepperWizardPageFormat.this.pepperWizard.advance();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVisible(boolean visible)
  {
    if (visible)
    {
      List<FormatDesc> supportedFormats = pepperWizard.getSupportedFormats();
      tableViewer.setInput(supportedFormats);

      FormatDesc formatDefinition =
          supportedFormats.size() == 1
        ? supportedFormats.get(0)
        : pepperWizard.getFormatDesc();

      if (formatDefinition == null)
      {
        formatDefinition = pepperWizard.getPreviouslySelectedFormatDesc();
      }

      tableViewer.setSelection(formatDefinition != null ? new StructuredSelection(formatDefinition) : StructuredSelection.EMPTY);
    }

    super.setVisible(visible);
  }
}

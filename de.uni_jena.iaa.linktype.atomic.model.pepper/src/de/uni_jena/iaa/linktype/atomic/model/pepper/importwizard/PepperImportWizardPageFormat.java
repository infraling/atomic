/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * Michael Grübsch
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

package de.uni_jena.iaa.linktype.atomic.model.pepper.importwizard;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
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

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;

/**
 *
 * @author  Michael Grübsch
 * @version $Revision$, $Date$
 */
public class PepperImportWizardPageFormat extends WizardPage implements IWizardPage
{
  protected final PepperImportWizard pepperImportWizard;

  protected TableViewer tableViewer;

  /**
   * Legt eine neue Instanz des Typs PepperImportWizardPageImporter an.
   * @param pageName
   * @param title
   * @param titleImage
   */
  public PepperImportWizardPageFormat(PepperImportWizard pepperImportWizard, String pageName, String title, ImageDescriptor titleImage)
  {
    super(pageName, title, titleImage);
    setPageComplete(false);
    setDescription("Select the pepper import format.");

    this.pepperImportWizard = pepperImportWizard;
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
        return super.getText(((FormatDefinition) element).getFormatName());
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
        return super.getText(((FormatDefinition) element).getFormatVersion());
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
        setPageComplete( selected);
        pepperImportWizard.setFormatDefinition(selected ? (FormatDefinition) ((IStructuredSelection) selection).getFirstElement() : null);
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
      PepperImporter pepperImporter = pepperImportWizard.getPepperImporter();
      if (pepperImporter != null)
      {
        EList<FormatDefinition> supportedFormats = pepperImporter.getSupportedFormats();
        tableViewer.setInput(supportedFormats);

        FormatDefinition formatDefinition =
            supportedFormats.size() == 1
          ? supportedFormats.get(0)
          : pepperImportWizard.getFormatDefinition();

        tableViewer.setSelection(formatDefinition != null ? new StructuredSelection(formatDefinition) : StructuredSelection.EMPTY);
      }
    }

    super.setVisible(visible);
  }
}

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

package de.uni_jena.iaa.linktype.atomic.model.pepper.wizard;

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

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;

/**
 *
 * @author  Michael Gr�bsch
 * @version $Revision$, $Date$
 */
public class PepperWizardPageModule<P extends PepperModule> extends WizardPage implements IWizardPage
{
  protected final AbstractPepperWizard<P> pepperWizard;

  protected TableViewer tableViewer;

  /**
   * Legt eine neue Instanz des Typs PepperImportWizardPageImporter an.
   * @param pageName
   * @param title
   * @param titleImage
   */
  public PepperWizardPageModule
    ( AbstractPepperWizard<P> pepperWizard
    , String pageName
    , String title
    , ImageDescriptor titleImage
    , String description
    )
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
      @SuppressWarnings("unchecked")
      @Override
      public String getText(Object element)
      {
        return super.getText(((P) element).getName());
      }
    });

    tableColumn = tableViewerColumn.getColumn();
    tableColumn.setText("Module");

    tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(70));

    tableViewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @SuppressWarnings("unchecked")
      @Override
      public String getText(Object element)
      {
        return super.getText(((P) element).getVersion());
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
      @SuppressWarnings("unchecked")
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        ISelection selection = event.getSelection();
        
        boolean selected = ! selection.isEmpty() && selection instanceof IStructuredSelection;
        setPageComplete(selected);
        pepperWizard.setPepperModule(selected ? (P) ((IStructuredSelection) selection).getFirstElement() : null);
      }
    });

    tableViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        PepperWizardPageModule.this.pepperWizard.advance();
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
      P pepperModule = pepperWizard.getPepperModule();
      if (pepperModule == null)
      {
        pepperModule = pepperWizard.getPreferredPepperModule();
      }

      tableViewer.setInput(pepperWizard.getPepperModules());
      tableViewer.setSelection(pepperModule != null ? new StructuredSelection(pepperModule) : StructuredSelection.EMPTY);
    }

    super.setVisible(visible);
  }
}

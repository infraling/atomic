/* -----------------------------------------------------------------------------
 * Copyright (c) 2013 Friedrich Schiller University Jena, Germany
 * Towards a corpus-based typology of clause linkage - Research Project
 * 
 * This software component or part of a software system is the proprietary
 * information of Friedrich Schiller University Jena, Germany, and is subject 
 * to license terms provided along with this information.
 * 
 * Author: Michael Grübsch
 * 
 * -----------------------------------------------------------------------------
 */

package de.uni_jena.iaa.linktype.atomic.model.pepper.importwizard;

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

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;

/**
 *
 * @author  Michael Grübsch
 * @version $Revision$, $Date$
 */
public class PepperImportWizardPageImporter extends WizardPage implements IWizardPage
{
  protected final PepperImportWizard pepperImportWizard;

  protected TableViewer tableViewer;

  /**
   * Legt eine neue Instanz des Typs PepperImportWizardPageImporter an.
   * @param pageName
   * @param title
   * @param titleImage
   */
  public PepperImportWizardPageImporter(PepperImportWizard pepperImportWizard, String pageName, String title, ImageDescriptor titleImage)
  {
    super(pageName, title, titleImage);
    setPageComplete(false);
    setDescription("Select the pepper import module.");

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
        return super.getText(((PepperImporter) element).getName());
      }
    });

    tableColumn = tableViewerColumn.getColumn();
    tableColumn.setText("Module");

    tableColumnLayout.setColumnData(tableColumn, new ColumnWeightData(70));

    tableViewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return super.getText(((PepperImporter) element).getVersion());
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
        pepperImportWizard.setPepperImporter(selected ? (PepperImporter) ((IStructuredSelection) selection).getFirstElement() : null);
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

      tableViewer.setInput(pepperImportWizard.getPepperImportersModules());
      tableViewer.setSelection(pepperImporter != null ? new StructuredSelection(pepperImporter) : StructuredSelection.EMPTY);
    }

    super.setVisible(visible);
  }
}

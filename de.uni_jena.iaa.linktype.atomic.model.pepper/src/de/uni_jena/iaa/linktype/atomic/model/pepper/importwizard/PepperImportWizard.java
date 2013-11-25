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

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.ServiceReference;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperConverter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.util.PepperFWProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ExporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ImporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperJobParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParamsFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.saltXML.SaltXMLExporter;
import de.uni_jena.iaa.linktype.atomic.model.pepper.Activator;

public class PepperImportWizard extends Wizard implements IImportWizard
{
  // TODO konfigurierbar
  public static final String SALT_XML_FORMAT_NAME = "SaltXML";
  public static final String SALT_XML_FORMAT_VERSION = "1.0";

  protected static final ImageDescriptor DEFAULT_PAGE_IAMGE_DESCRIPTOR = 
    AbstractUIPlugin.imageDescriptorFromPlugin(Activator.getDefault().getBundle().getSymbolicName(), "/saltnpepper_logo-64x64.png");

  protected static final AtomicInteger PEPPER_JOB_ID = new AtomicInteger();

  static
  {
    // TODO extern konfigurieren
    System.setProperty("PepperModuleResolver.TemprorariesURI", System.getProperty("java.io.tmpdir"));
    System.setProperty("PepperModuleResolver.ResourcesURI", System.getProperty("java.io.tmpdir"));
  }

  protected ServiceReference<PepperConverter> reference;
  protected PepperConverter pepperConverter;
  protected List<PepperImporter> pepperImporters;
  protected PepperImporter pepperImporter;
  protected FormatDefinition formatDefinition;
  protected String importDirectory;

  public PepperImportWizard()
  {
    setWindowTitle("Import via Pepper");
    setNeedsProgressMonitor(true);
    setDefaultPageImageDescriptor(DEFAULT_PAGE_IAMGE_DESCRIPTOR);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    reference = Activator.getDefault().getBundle().getBundleContext().getServiceReference(PepperConverter.class);
    if (reference != null)
    {
      pepperConverter = Activator.getDefault().getBundle().getBundleContext().getService(reference);

      Properties properties = pepperConverter.getProperties();
      if (properties == null)
      {
        properties = new Properties();
      }
      properties.setProperty(PepperFWProperties.PROP_REMOVE_SDOCUMENTS_AFTER_PROCESSING, "true");
      pepperConverter.setProperties(properties);
    }
  }
  

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages()
  {
    addPage(new PepperImportWizardPageImporter(this, "selectImporter", "Select Import Module", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
    addPage(new PepperImportWizardPageFormat(this, "selectFormat", "Select Import Format", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
    addPage(new PepperImportWizardPageDirectory(this, "selectDirectory", "Select Import Directory", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
  }

  protected List<PepperImporter> getPepperImportersModules()
  {
    if (pepperImporters == null)
    {
      pepperImporters = pepperConverter.getPepperModuleResolver().getPepperImporters();
    }
    
    return pepperImporters;
  }

  public PepperImporter getPepperImporter()
  {
    return pepperImporter;
  }

  protected void setPepperImporter(PepperImporter pepperImporter)
  {
    this.pepperImporter = pepperImporter;
  }

  protected FormatDefinition getFormatDefinition()
  {
    return formatDefinition;
  }

  protected void setFormatDefinition(FormatDefinition formatDefinition)
  {
    this.formatDefinition = formatDefinition;
  }
  
  protected String getImportDirectory()
  {
    return importDirectory;
  }
  
  protected void setImportDirectory(String importDirectory)
  {
    this.importDirectory = importDirectory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    if (reference != null)
    {
      importDirectory = null;
      formatDefinition = null;
      pepperImporter = null;
      pepperImporters = null;
      pepperConverter = null;

      Activator.getDefault().getBundle().getBundleContext().ungetService(reference);
      reference = null;
    }

    super.dispose();
  }

  @Override
  public boolean performFinish()
  {
    if (pepperImporter != null && formatDefinition != null && importDirectory != null)
    {
      ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
      importerParams.setModuleName(pepperImporter.getName());
      importerParams.setFormatName(formatDefinition.getFormatName());
      importerParams.setFormatVersion(formatDefinition.getFormatVersion());
      importerParams.setSourcePath(URI.createFileURI(new File(importDirectory).getAbsolutePath()));
      
      ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
      SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
      exporterParams.setModuleName(saltXMLExporter.getName());
      exporterParams.setFormatName(SALT_XML_FORMAT_NAME);
      exporterParams.setFormatVersion(SALT_XML_FORMAT_VERSION);
      // TODO in den Workspace ausgeben
      exporterParams.setDestinationPath(URI.createFileURI(new File(System.getProperty("java.io.tmpdir")).getAbsolutePath()));
      
      PepperJobParams pepperJobParams = PepperParamsFactory.eINSTANCE.createPepperJobParams();
      pepperJobParams.setId(PEPPER_JOB_ID.incrementAndGet());
      pepperJobParams.getImporterParams().add(importerParams);
      pepperJobParams.getExporterParams().add(exporterParams);

      PepperParams pepperParams = PepperParamsFactory.eINSTANCE.createPepperParams();
      pepperParams.getPepperJobParams().add(pepperJobParams);
      pepperConverter.setPepperParams(pepperParams);

      // TODO bei Fehlern im Job (Paula: falsches Verzeichnis) blockiert Thread
      pepperConverter.start();

      return true;
    }
    else
    {
      return false;
    }
  }


}

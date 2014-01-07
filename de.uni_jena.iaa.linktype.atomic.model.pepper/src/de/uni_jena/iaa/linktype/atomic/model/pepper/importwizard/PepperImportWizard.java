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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.ServiceReference;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperConverter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperModuleResolver;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.util.PepperFWProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperty;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ExporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ImporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperJobParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParamsFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.saltXML.SaltXMLExporter;
import de.uni_jena.iaa.linktype.atomic.model.pepper.Activator;
import de.uni_jena.iaa.linktype.atomic.model.salt.project.AtomicProjectService;

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
  protected PepperModuleProperties pepperModuleProperties;
  protected FormatDefinition formatDefinition;
  protected String importDirectory;
  protected String projectName;

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

    pepperModuleProperties = new PepperModuleProperties();
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
    addPage(new PepperImportWizardPageProperties(this, "selectProperties", "Select Import Properties", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
    addPage(new PepperImportWizardPageProjectName(this, "selectProjectName", "Select Project Name", DEFAULT_PAGE_IAMGE_DESCRIPTOR));
  }

  protected List<PepperImporter> getPepperImportersModules()
  {
    if (pepperImporters == null)
    {
      List<PepperImporter> importers = null;
      if (pepperConverter != null)
      {
	      PepperModuleResolver pepperModuleResolver = pepperConverter.getPepperModuleResolver();
	      if (pepperModuleResolver != null)
	      {
	    	  importers = pepperModuleResolver.getPepperImporters();
	    	  if (importers != null)
	    	  {
			      Collections.sort(importers, new Comparator<PepperImporter>() {
					@Override
					public int compare(PepperImporter o1, PepperImporter o2) {
						return o1.getName().compareTo(o2.getName());
					}
			      });
	    	  }
	    	  else
	    	  	System.err.println("getPepperImporters returns null");
	      }
	      else
	    	  System.err.println("PepperModuleResolver = null");
      }

      pepperImporters = importers != null ? importers : Collections.<PepperImporter>emptyList();
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

  public String getProjectName()
  {
    return projectName;
  }

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  public PepperModuleProperties getPepperModuleProperties()
  {
    return pepperModuleProperties;
  }

  public void addPepperModuleProperty(PepperModuleProperty<?> pepperModuleProperty)
  {
    pepperModuleProperties.addProperty(pepperModuleProperty);
  }

  public boolean containsPepperModuleProperty(String propertyName)
  {
    PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
    return property != null;
  }

  public String getPepperModulePropertyValue(String propertyName)
  {
    PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
    Object value = property != null ? property.getValue() : null;

    return value != null ? value.toString() : null;
  }

  public void setPepperModulePropertyValue(String propertyName, String propertyValueString)
  {
    PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
    if (property != null)
    {
      property.setValueString(propertyValueString);
    }
  }

  public void removePepperModuleProperty(String propertyName)
  {
    // es gibt kein remove in PepperModuleProperties 
    PepperModuleProperty<?> property;
    property = pepperModuleProperties.getProperty(propertyName);
    if (property != null)
    {
      PepperModuleProperties properties = new PepperModuleProperties();
      for (String name : pepperModuleProperties.getPropertyNames())
      {
        if ( ! name.equals(propertyName))
        {
          properties.addProperty(pepperModuleProperties.getProperty(name));
        }
      }
      pepperModuleProperties = properties;
    }
  }

  protected void advance()
  {
    IWizardPage currentPage = getContainer().getCurrentPage();
    if (currentPage != null)
    {
      IWizardPage nextPage = getNextPage(currentPage);
      if (nextPage != null)
      {
        getContainer().showPage(nextPage);
      }
    }
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
    try
    {
      AtomicProjectService atomicProjectService = AtomicProjectService.getInstance();
      if ( pepperImporter != null 
        && formatDefinition != null 
        && importDirectory != null
        && projectName != null
        && ! atomicProjectService.isProjectExisting(projectName)
         )
      {
        IProject project = atomicProjectService.createIProject(projectName);

        boolean cancelable = false; // FIXME can't cancel forked pepper tasks 
        ImportRunnable importRunnable = new ImportRunnable(project, cancelable);
        PlatformUI.getWorkbench().getProgressService().run(false, cancelable, importRunnable);

        return importRunnable.get().booleanValue();
      }
      else
      {
        return false;
      }
    }
    catch (Exception X)
    {
      X.printStackTrace();
      return false;
    }
  }

  protected class ImportRunnable
    implements 
      IRunnableWithProgress
    , Future<Boolean>
  {
    protected final IProject project;
    protected final boolean cancelable;
    
    protected final Object cancelLock = new Object();

    protected Semaphore semaphore = new Semaphore(0);
    protected Boolean outcome = Boolean.FALSE;
    protected Throwable throwable = null;
    protected volatile boolean cancelled = false;
    protected volatile boolean done = false;
    protected volatile Thread importThread = null;
    
    public ImportRunnable(IProject project, boolean cancelable)
    {
      this.project = project;
      this.cancelable = cancelable;
    }

    protected void importProject() throws IOException, CoreException
    {
      ImporterParams importerParams = PepperParamsFactory.eINSTANCE.createImporterParams();
      importerParams.setModuleName(pepperImporter.getName());
      importerParams.setFormatName(formatDefinition.getFormatName());
      importerParams.setFormatVersion(formatDefinition.getFormatVersion());
      importerParams.setSourcePath(URI.createFileURI(new File(importDirectory).getAbsolutePath()));
      
      Properties properties = pepperModuleProperties.getProperties();
      if (0 < properties.size())
      {
        File tempFile = File.createTempFile("pepper", ".properties");
        tempFile.deleteOnExit();
        
        Writer writer = new FileWriter(tempFile);
        try
        {
          properties.store(writer, "Generated pepper properties");
          importerParams.setSpecialParams(URI.createFileURI(tempFile.getAbsolutePath()));
        }
        finally
        {
          writer.close();
        }
      }

      ExporterParams exporterParams = PepperParamsFactory.eINSTANCE.createExporterParams();
      SaltXMLExporter saltXMLExporter = new SaltXMLExporter();
      exporterParams.setModuleName(saltXMLExporter.getName());
      exporterParams.setFormatName(SALT_XML_FORMAT_NAME);
      exporterParams.setFormatVersion(SALT_XML_FORMAT_VERSION);
      exporterParams.setDestinationPath(URI.createURI(project.getLocationURI().toString()));
      
      PepperJobParams pepperJobParams = PepperParamsFactory.eINSTANCE.createPepperJobParams();
      pepperJobParams.setId(PEPPER_JOB_ID.incrementAndGet());
      pepperJobParams.getImporterParams().add(importerParams);
      pepperJobParams.getExporterParams().add(exporterParams);

      PepperParams pepperParams = PepperParamsFactory.eINSTANCE.createPepperParams();
      pepperParams.getPepperJobParams().add(pepperJobParams);
      pepperConverter.setPepperParams(pepperParams);

      // TODO bei Fehlern im Job (Paula: falsches Verzeichnis) blockiert Thread
      pepperConverter.start();

      project.refreshLocal(IResource.DEPTH_INFINITE, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
      try
      {
        synchronized (cancelLock)
        {
          // prüfen, ob Ausführung bereits vor dem Start abgebroche worden ist
          if (cancelled)
          {
            throw new InterruptedException();
          }
          else
          {
            // Thread in dem der Import ausgeführt wird und der bei Abbruch im
            // Progressmonitor unterbrochen werden soll
            importThread = Thread.currentThread();
          }
        }

        // Progressmonitor asynchron auf Abbruch überwachen
        ScheduledFuture<?> cancellationCheck;
        if (cancelable)
        {
          // Überwachungsthread
          cancellationCheck = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay
            ( new Runnable()
              {
                @Override
                public void run()
                {
                  if (monitor.isCanceled())
                  {
                    importThread.interrupt();
                  }
                }
              }
            , 500
            , 500
            , TimeUnit.MILLISECONDS
            );
        }
        else
        {
          cancellationCheck = null;
        }

        // Monitor starten
        monitor.beginTask("Import running ...", IProgressMonitor.UNKNOWN);
        outcome = Boolean.FALSE;
        try
        {
          // Import ausführen
          importProject();
          outcome = Boolean.TRUE;
        }
        finally
        {
          // Monitor beenden
          monitor.done();

          // Überwachungsthread stoppen
          if (cancellationCheck != null)
          {
            cancellationCheck.cancel(true);
          }

          // Abbruch signalisieren
          if (Thread.currentThread().isInterrupted())
          {
            throw new InterruptedException();
          }
        }
      }
      catch (InterruptedException X)
      {
        // Abbruchsignal empfangen
        cancelled = true;
        throw X;
      }
      catch (Throwable T)
      {
        throw new InvocationTargetException(throwable = T);
      }
      finally
      {
        importThread = null;
        done = true;
        semaphore.release(Integer.MAX_VALUE);
      }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
      if (cancelable)
      {
        synchronized (cancelLock)
        {
          Thread thread = importThread;
          if (thread != null)
          {
            if (mayInterruptIfRunning)
            {
              thread.interrupt();
              return true;
            }
            else
            {
              return false;
            }
          }
          else
          {
            cancelled = true;
            return true;
          }
        }
      }
      else
      {
        return false;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled()
    {
      return cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDone()
    {
      return done;
    }

    protected Boolean getOutcome() throws ExecutionException
    {
      if (throwable != null)
      {
        throw new ExecutionException(throwable);
      }
      else
      {
        return outcome;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean get() throws InterruptedException, CancellationException, ExecutionException
    {
      if (cancelled)
      {
        throw new CancellationException();
      }
      else
      {
        semaphore.acquire();
        try
        {
          return getOutcome();
        }
        finally
        {
          semaphore.release();
        }
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean get(long timeout, TimeUnit unit) 
      throws 
        InterruptedException
      , CancellationException
      , ExecutionException
      , TimeoutException
    {
      if (cancelled)
      {
        throw new CancellationException();
      }
      else
      {
        if (semaphore.tryAcquire(timeout, unit))
        {
          try
          {
            return getOutcome();
          }
          finally
          {
            semaphore.release();
          }
        }
        else
        {
          return null;
        }
      }
    }
  }
}

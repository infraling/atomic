/*******************************************************************************
 * Copyright 2014 Friedrich Schiller University Jena
 * Vivid Sky - Softwaremanufaktur, Michael Grübsch.
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperConverter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperDocumentController;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperJob;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperModuleController;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ExporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ImporterParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.ModuleParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperJobParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParams;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperParams.PepperParamsFactory;

public abstract class PepperModuleRunnable
  implements 
    IRunnableWithProgress
  , Future<Boolean>
{
  protected static final AtomicInteger threadCounter = new AtomicInteger();

  protected final AbstractPepperWizard<? extends PepperModule> pepperWizard;

  protected final IProject project;
  protected final boolean cancelable;
  
  protected final Object cancelLock = new Object();

  protected Semaphore semaphore = new Semaphore(0);
  protected Boolean outcome = Boolean.FALSE;
  protected Throwable throwable = null;
  protected volatile boolean cancelled = false;
  protected volatile boolean done = false;
  protected volatile Thread controlThread = null;
  protected volatile Thread moduleThread = null;
  
  public PepperModuleRunnable(AbstractPepperWizard<? extends PepperModule> pepperWizard, IProject project, boolean cancelable)
  {
    this.pepperWizard = pepperWizard;
    this.project = project;
    this.cancelable = cancelable;
  }

  protected abstract ImporterParams createImporterParams();

  protected abstract ExporterParams createExporterParams();

  protected void setSpecialParams(ModuleParams moduleParams) throws IOException
  {
    Properties properties = pepperWizard.getPepperModuleProperties().getProperties();
    if (0 < properties.size())
    {
      File tempFile = File.createTempFile("pepper", ".properties");
      tempFile.deleteOnExit();
      
      Writer writer = new FileWriter(tempFile);
      try
      {
        properties.store(writer, "Generated pepper properties");
        moduleParams.setSpecialParams(URI.createFileURI(tempFile.getAbsolutePath()));
      }
      finally
      {
        writer.close();
      }
    }
  }

  protected void runModule() throws IOException, CoreException
  {
    ImporterParams importerParams = createImporterParams();
    setSpecialParams(importerParams);

    ExporterParams exporterParams = createExporterParams();
    
    PepperJobParams pepperJobParams = PepperParamsFactory.eINSTANCE.createPepperJobParams();
    pepperJobParams.setId(AbstractPepperWizard.PEPPER_JOB_ID.incrementAndGet());
    pepperJobParams.getImporterParams().add(importerParams);
    pepperJobParams.getExporterParams().add(exporterParams);

    PepperParams pepperParams = PepperParamsFactory.eINSTANCE.createPepperParams();
    pepperParams.getPepperJobParams().add(pepperJobParams);
    
    PepperConverter pepperConverter = pepperWizard.getPepperConverter();
    pepperConverter.setPepperParams(pepperParams);
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
        // prüfen, ob Ausführung bereits vor dem Start abgebrochen worden ist
        if (cancelled)
        {
          throw new InterruptedException();
        }
        else
        {
          controlThread = Thread.currentThread();
          // Thread in dem der Vorgang ausgeführt wird und der bei Abbruch im
          // Progressmonitor unterbrochen werden soll
          moduleThread = new Thread("Pepper Module Thread #" + threadCounter.incrementAndGet())
          {
            @Override
            public void run()
            {
              try
              {
                runModule();
              }
              catch (IOException X)
              {
                throw new RuntimeException(X);
              }
              catch (CoreException X)
              {
                throw new RuntimeException(X);
              }
            }
          };
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
                  cancel(true);
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
      monitor.beginTask("Running ...", IProgressMonitor.UNKNOWN);
      outcome = Boolean.FALSE;
      try
      {
        // Modul ausführen
        moduleThread.start();

        Display display = Display.findDisplay(Thread.currentThread());
        if (display == null)
        {
          moduleThread.join();
        }
        else
        {
          // aktueller Thread ist der UI-Thread
          while ( ! isCancelled() && moduleThread.isAlive())
          {
            if (controlThread.isInterrupted())
            {
              throw new InterruptedException();
            }

            if ( ! display.readAndDispatch())
            {
              display.sleep();
            }
          }
        }

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
      try
      {
        finishModuleThread(500);
      }
      catch (Throwable T)
      {
        throw new InvocationTargetException(throwable = T);
      }
    }
    catch (Throwable T)
    {
      throw new InvocationTargetException(throwable = T);
    }
    finally
    {
      moduleThread = null;
      controlThread = null;
      done = true;
      semaphore.release(Integer.MAX_VALUE);
    }
  }

  /**
   * Pepper hangs if it encounters an error: release its monitors manually.
   */
  protected void finishModuleThread(long gracePeriodInMillis) throws NoSuchFieldException, SecurityException, IllegalAccessException
  {
    Thread thread = moduleThread;
    if (thread != null && thread.isAlive())
    {
      try
      {
        thread.join(gracePeriodInMillis);
      }
      catch (InterruptedException XX)
      {
        // siliently ignore
      }

      if (thread.isAlive())
      {
        PepperConverter pepperConverter = pepperWizard.getPepperConverter();
        for (PepperJob pepperJob : pepperConverter.getPepperJobs())
        {
          Field field = pepperJob.getClass().getDeclaredField("allModuleControlers");
          field.setAccessible(true);
          @SuppressWarnings("unchecked")

          EList<PepperModuleController> pepperModuleControllers = (EList<PepperModuleController>) field.get(pepperJob);
          for (PepperModuleController pepperModuleController : pepperModuleControllers)
          {
            pepperModuleController.getPepperM2JMonitor().finish();
          }
        }
      }
    }
  }
  
  /**
   * Collects status information. Unfortunately this status is not appropriated
   * to be displayed within the Eclipse progress monitor dialog.
   * 
   * @return status information
   */
  protected String getStatusString()
  {
    String status = null;
    Thread thread = moduleThread;
    if (thread != null && thread.isAlive())
    {
      PepperConverter pepperConverter = pepperWizard.getPepperConverter();
      if (pepperConverter != null)
      {
        for (PepperJob pepperJob : pepperConverter.getPepperJobs())
        {
          PepperDocumentController documentController = pepperJob.getPepperDocumentController();
          if (documentController != null)
          {
            String status4Print = documentController.getStatus4Print();
            if (status4Print != null)
            {
              status = status != null ? status + "\n" + status4Print : status4Print;
            }
          }
        }
      }
    }
    
    return status;
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
        if ( ! cancelled)
        {
          Thread thread = controlThread;
          if (thread != null)
          {
            if (mayInterruptIfRunning)
            {
              thread.interrupt();
              cancelled = true;
            }
          }
          else
          {
            cancelled = true;
          }
        }

        return cancelled;
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
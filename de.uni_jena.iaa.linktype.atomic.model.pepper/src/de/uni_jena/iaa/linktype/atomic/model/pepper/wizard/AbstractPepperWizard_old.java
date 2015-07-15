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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.ServiceReference;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.Pepper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.core.ModuleResolver;
import de.hu_berlin.german.korpling.saltnpepper.pepper.core.PepperImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;
import de.uni_jena.iaa.linktype.atomic.model.pepper.Activator;

public abstract class AbstractPepperWizard_old
  <P extends PepperModule>
  extends 
    Wizard 
{
  protected static final String DIALOG_SETTINGS_EXCHANGE_TARGET_PATH = "exchangeTargetPath";
  protected static final String DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE = "exchangeTargetType";
  protected static final String DIALOG_SETTINGS_MODULE = "pepperModule";
  protected static final String DIALOG_SETTINGS_FORMAT_NAME = "formatName";
  protected static final String DIALOG_SETTINGS_FORMAT_VERSION = "formatVersion";
  protected static final String DIALOG_SETTINGS_MODULE_PROPERTY_KEYS = "modulePropertyKeys";
  protected static final String DIALOG_SETTINGS_MODULE_PROPERTY_VALUES = "modulePropertyValues";

  @Deprecated
  protected static final String DIALOG_SETTINGS_EXCHANGE_DIRECTORY = "exchangeDirectory";

  public static final String SALT_XML_FORMAT_NAME = "SaltXML";
  public static final String SALT_XML_FORMAT_VERSION = "1.0";

  protected static final ImageDescriptor DEFAULT_PAGE_IAMGE_DESCRIPTOR = 
    AbstractUIPlugin.imageDescriptorFromPlugin(Activator.getDefault().getBundle().getSymbolicName(), "/saltnpepper_logo-64x64.png");

  protected static final AtomicInteger PEPPER_JOB_ID = new AtomicInteger();

  static
  {
    System.setProperty("PepperModuleResolver.TemprorariesURI", System.getProperty("java.io.tmpdir"));
    System.setProperty("PepperModuleResolver.ResourcesURI", System.getProperty("java.io.tmpdir"));
  }

  protected final WizardMode wizardMode;

  protected ServiceReference<Pepper> reference;
  protected Pepper pepper;
  protected List<P> pepperModuleList;
  protected P pepperModule;
  protected PepperModuleProperties pepperModuleProperties;
  protected FormatDesc formatDesc;
  protected String exchangeTargetPath;
  protected ExchangeTargetType exchangeTargetType = ExchangeTargetType.DIRECTORY;

  protected AbstractPepperWizard_old(String windowTitle, WizardMode wizardMode)
  {
    this.wizardMode = wizardMode;
    setWindowTitle(windowTitle);
    setNeedsProgressMonitor(true);
    setDefaultPageImageDescriptor(DEFAULT_PAGE_IAMGE_DESCRIPTOR);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IDialogSettings getDialogSettings()
  {
    IDialogSettings settings = super.getDialogSettings();
    if (settings == null)
    {
      settings = DialogSettings.getOrCreateSection(Activator.getDefault().getDialogSettings(), "pepperWizard:" + getClass().getName());
      setDialogSettings(settings);
    }

    return settings;
  }

  public void initialize()
  {
    reference = Activator.getDefault().getBundle().getBundleContext().getServiceReference(Pepper.class);
    if (reference != null)
    {
      pepper = Activator.getDefault().getBundle().getBundleContext().getService(reference);

      PepperConfiguration properties = pepper.getConfiguration();
      if (properties == null)
      {
        properties = new PepperConfiguration();
      }
      properties.setProperty(PepperConfiguration.PROP_REMOVE_SDOCUMENTS_AFTER_PROCESSING, "true");
      pepper.setConfiguration(properties);
    }

    pepperModuleProperties = new PepperModuleProperties();
    
    readDialogSettings();
  }

  public WizardMode getWizardMode()
  {
    return wizardMode;
  }

  protected abstract List<P> resolvePepperModules(ModuleResolver pepperModuleResolver);

  public List<P> getPepperModules()
  {
    if (pepperModuleList == null)
    {
      List<P> modules = null;
      if (pepper != null)
      {
        ModuleResolver pepperModuleResolver = ((PepperImpl)pepper).getModuleResolver();
        if (pepperModuleResolver != null)
        {
          modules = resolvePepperModules(pepperModuleResolver);
          if (modules != null)
          {
            Collections.sort
              ( modules
              , new Comparator<P>() 
                {
                  @Override
                  public int compare(P o1, P o2) 
                  {
                    return o1.getName().compareTo(o2.getName());
                  }
                });
          }
          else
          {
            new MessageDialog
              ( this.getShell()
              , "Error"
              , null
              , "Did not found any Pepper module!"
              , MessageDialog.ERROR
              , new String[]{ IDialogConstants.OK_LABEL }
              , 0).open();
          }
        }
        else
        {
          new MessageDialog
            ( this.getShell()
            , "Error"
            , null
            , "Did not found Pepper module resolver!"
            , MessageDialog.ERROR
            , new String[]{ IDialogConstants.OK_LABEL }
            , 0).open();
        }
      }

      pepperModuleList = modules != null ? modules : Collections.<P>emptyList();
    }
    
    return pepperModuleList;
  }

  public Pepper getPepper()
  {
    return pepper;
  }

  public P getPepperModule()
  {
    return pepperModule;
  }

  public P getPreferredPepperModule()
  {
    List<P> moduleList = getPepperModules();
    String moduleName = getDialogSettings().get(DIALOG_SETTINGS_MODULE);
    if (0 < moduleList.size() && moduleName != null)
    {
      for (P module : moduleList)
      {
        if (moduleName.equals(module.getName()))
        {
          return module;
        }
      }
    }

    return null;
  }

  public void setPepperModule(P pepperModule)
  {
    this.pepperModule = pepperModule;
  }

  public abstract List<FormatDesc> getSupportedFormats();

  public FormatDesc getFormatDesc()
  {
    return formatDesc;
  }

  public FormatDesc getPreferredFormatDesc()
  {
    P module = getPepperModule();
    String formatName = getDialogSettings().get(DIALOG_SETTINGS_FORMAT_NAME);
    String formatVersion = getDialogSettings().get(DIALOG_SETTINGS_FORMAT_VERSION);
    if (module != null && formatName != null && formatVersion != null)
    {
      for (FormatDesc fd : getSupportedFormats())
      {
        if (formatName.equals(fd.getFormatName()) && formatVersion.equals(fd.getFormatVersion()))
        {
          return fd;
        }
      }
    }

    return null;
  }

  public void setFormatDesc(FormatDesc formatDesc)
  {
    this.formatDesc = formatDesc;
  }
  
  public String getExchangeTargetPath()
  {
    return exchangeTargetPath;
  }
  
  public void setExchangeTargetPath(String exchangeTargetPath)
  {
    this.exchangeTargetPath = exchangeTargetPath;
  }

  public ExchangeTargetType getExchangeTargetType()
  {
    return exchangeTargetType;
  }

  public void setExchangeTargetType(ExchangeTargetType exchangeTargetType)
  {
    this.exchangeTargetType = exchangeTargetType;
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

  public void advance()
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
      exchangeTargetPath = null;
      exchangeTargetType = ExchangeTargetType.DIRECTORY;
      formatDesc = null;
      pepperModule = null;
      pepperModuleList = null;
      pepper = null;

      Activator.getDefault().getBundle().getBundleContext().ungetService(reference);
      reference = null;
    }

    super.dispose();
  }

  protected boolean canPerformFinish()
  {
    return 
        pepperModule != null 
     && formatDesc != null 
     && exchangeTargetPath != null;
  }

  protected abstract IProject getProject() throws CoreException;
  
  protected abstract PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable);

  @Override
  public boolean performFinish()
  {
    try
    {
      if (canPerformFinish())
      {
        IProject project = getProject();

        PepperModuleRunnable moduleRunnable = createModuleRunnable(project, true);
        PlatformUI.getWorkbench().getProgressService().run(false, true, moduleRunnable);

        boolean outcome = moduleRunnable.get().booleanValue();
        
        if (outcome)
        {
          writeDialogSettings();
        }

        return outcome;
      }
      else
      {
        return false;
      }
    }
    catch (CancellationException X)
    {
      return false;
    }
    catch (Exception X)
    {
      X.printStackTrace();
      return false;
    }
  }


  protected void readDialogSettings()
  {
    IDialogSettings settings = getDialogSettings();
    exchangeTargetPath = settings.get(DIALOG_SETTINGS_EXCHANGE_TARGET_PATH);
    if (exchangeTargetPath == null)
    {
      exchangeTargetPath = settings.get(DIALOG_SETTINGS_EXCHANGE_DIRECTORY);
    }

    String exchangeTargetTypeName = settings.get(DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE);
    exchangeTargetType =
        exchangeTargetTypeName != null
      ? ExchangeTargetType.valueOf(exchangeTargetTypeName)
      : ExchangeTargetType.DIRECTORY;

    String[] modulePropertyKeys = settings.getArray(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS);
    if (modulePropertyKeys != null && 0 < modulePropertyKeys.length)
    {
      String[] modulePropertyValues = settings.getArray(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES);
      if (modulePropertyValues != null && modulePropertyKeys.length == modulePropertyValues.length)
      {
        for (int i = 0; i < modulePropertyKeys.length; ++i)
        {
          PepperModuleProperty<String> pepperModuleProperty = new PepperModuleProperty<String>(modulePropertyKeys[i], String.class, "", "");
          pepperModuleProperty.setValueString(modulePropertyValues[i]);
          addPepperModuleProperty(pepperModuleProperty);
        }
      }
    }
  }

  protected void writeDialogSettings()
  {
    IDialogSettings settings = getDialogSettings();

    settings.put(DIALOG_SETTINGS_EXCHANGE_TARGET_PATH, exchangeTargetPath);
    settings.put(DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE, exchangeTargetType.name());

    P module = getPepperModule();
    settings.put(DIALOG_SETTINGS_MODULE, module != null ? module.getName() : null);

    FormatDesc fd = getFormatDesc();
    settings.put(DIALOG_SETTINGS_FORMAT_NAME, fd != null ? fd.getFormatName() : null);
    settings.put(DIALOG_SETTINGS_FORMAT_VERSION, fd != null ? fd.getFormatVersion() : null);
    
    PepperModuleProperties moduleProperties = getPepperModuleProperties();
    if (moduleProperties != null)
    {
      Collection<String> propertyNames = moduleProperties.getPropertyNames();
      if (propertyNames != null)
      {
        String[] modulePropertyKeys = new String[propertyNames.size()];
        String[] modulePropertyValues = new String[propertyNames.size()];
        int index = 0;
        for (String propertyName : propertyNames)
        {
          modulePropertyKeys[index] = propertyName;
          Object value = moduleProperties.getProperty(propertyName).getValue();
          modulePropertyValues[index] = value != null ? value.toString() : null;
          ++index;
        }

        settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, modulePropertyKeys);
        settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, modulePropertyValues);
      }
      else
      {
        settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, (String[]) null);
        settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, (String[]) null);
      }
    }
    else
    {
      settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, (String[]) null);
      settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, (String[]) null);
    }
  }

  public static enum ExchangeTargetType
  {
    FILE,
    DIRECTORY
  }
  
  public static enum WizardMode
  {
    IMPORT
  , EXPORT
  }
}

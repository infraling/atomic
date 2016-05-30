/*******************************************************************************
 * Copyright 2016 Stephan Druskat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Grübsch - initial API and implementation
 *     Stephan Druskat - update to Pepper 3.x API
 *******************************************************************************/
package org.corpus_tools.atomic.pepper.wizard;

import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;

import org.corpus_tools.atomic.pepper.Activator;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.common.PepperModuleDesc;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
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

/**
 * An abstract base class for Pepper Wizards, i.e., import and export wizards.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public abstract class AbstractPepperWizard
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





protected AbstractPepperWizard(String windowTitle, WizardMode wizardMode)
{
  this.wizardMode = wizardMode;
  setWindowTitle(windowTitle);
  setNeedsProgressMonitor(true);
  setDefaultPageImageDescriptor(DEFAULT_PAGE_IAMGE_DESCRIPTOR);
}

//=============================================> probably unnecessary  
protected static final AtomicInteger PEPPER_JOB_ID = new AtomicInteger();
static
{
  System.setProperty("PepperModuleResolver.TemprorariesURI", System.getProperty("java.io.tmpdir"));
  System.setProperty("PepperModuleResolver.ResourcesURI", System.getProperty("java.io.tmpdir"));
}
//protected List<P> pepperModuleList;
protected PepperModuleProperties pepperModuleProperties;
protected FormatDesc formatDesc;
public static final String SALT_XML_FORMAT_NAME = "SaltXML";
public static final String SALT_XML_FORMAT_VERSION = "1.0";


//========================================> target path  
protected String exchangeTargetPath;
protected ExchangeTargetType exchangeTargetType = ExchangeTargetType.DIRECTORY;
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
//========================================< target path

//=============================================> called by Eclipse
protected static final ImageDescriptor DEFAULT_PAGE_IAMGE_DESCRIPTOR = 
  AbstractUIPlugin.imageDescriptorFromPlugin(Activator.getDefault().getBundle().getSymbolicName(), "/saltnpepper_logo-64x64.png");

protected final WizardMode wizardMode;

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

/**
 * Is called by implementing classes in {@link Wizard#init()}
 */
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
//    properties.setProperty(PepperConfiguration.PROP_REMOVE_SDOCUMENTS_AFTER_PROCESSING, "true");
    pepper.setConfiguration(properties);
  }

  pepperModuleProperties = new PepperModuleProperties();
  
  readDialogSettings();
}

public WizardMode getWizardMode()
{
  return wizardMode;
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
//    pepperModuleList = null;
    pepper = null;

    Activator.getDefault().getBundle().getBundleContext().ungetService(reference);
    reference = null;
  }

  super.dispose();
}

protected boolean canPerformFinish()
{
  return 
      pepperModule != null &&
   formatDesc != null 
   && exchangeTargetPath != null;
}

/**
 * Returns the current Atomic project on which the conversion operation should be executed. This can be an existing project for export or a to-be-created project.
 * @return
 * @throws CoreException
 */
protected abstract IProject getProject() throws CoreException;
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

  PepperModuleDesc module = getPepperModule();
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
//=============================================< called by Eclipse

protected ServiceReference<Pepper> reference;
protected Pepper pepper;
public Pepper getPepper()
{
  return pepper;
}

/**
 * Returns a list of PepperModules according to the requested module type.
 * @return
 */
public abstract List<PepperModuleDesc> getPepperModules();

/**
 * Returns description objects for all {@link PepperModule}s provided by the current {@link Pepper} instance. The list either contains all importers (sorted by name), manipulators (sorted by name) or all exporters (sorted by name).
 * @param moduleType type of modules 
 * @return a sorted list containing all modules belonging to the passed type 
 */
protected List<PepperModuleDesc> getPepperModules(MODULE_TYPE moduleType){
	  if (moduleType== null){
		  throw new NullPointerException("The passed module type for getting all available Pepper modules is null. This might ba a bug. ");
	  }
	  List<PepperModuleDesc> retVal= null;
	  Collection<PepperModuleDesc> descs= getPepper().getRegisteredModules();
	  if ((descs!= null)&&(descs.size()!= 0)){
		  retVal= new ArrayList<PepperModuleDesc>();
		  for (PepperModuleDesc desc: descs){
			  if (moduleType.equals(desc.getModuleType())){
				  retVal.add(desc);
			  }
		  }
		  Collections.sort(retVal, new Comparator<PepperModuleDesc>(){
			@Override
			public int compare(PepperModuleDesc desc1, PepperModuleDesc desc2) {
				 return desc1.getName().compareTo(desc2.getName());
			}  
		  });
	  }else{
        new MessageDialog
        ( this.getShell()
        , "Error"
        , null
        , "Did not find any Pepper module!"
        , MessageDialog.ERROR
        , new String[]{ IDialogConstants.OK_LABEL }
        , 0).open();
    }
	  return(retVal);
}

//protected abstract List<P> resolvePepperModules(ModuleResolver pepperModuleResolver);


//public List<P> getPepperModules()
//{
//  if (pepperModuleList == null)
//  {
//    List<P> modules = null;
//    if (pepper != null)
//    {
//      ModuleResolver pepperModuleResolver = ((PepperImpl)pepper).getModuleResolver();
//      if (pepperModuleResolver != null)
//      {
//        modules = resolvePepperModules(pepperModuleResolver);
//        if (modules != null)
//        {
//          Collections.sort
//            ( modules
//            , new Comparator<P>() 
//              {
//                @Override
//                public int compare(P o1, P o2) 
//                {
//                  return o1.getName().compareTo(o2.getName());
//                }
//              });
//        }
//        else
//        {
//          new MessageDialog
//            ( this.getShell()
//            , "Error"
//            , null
//            , "Did not find any Pepper module!"
//            , MessageDialog.ERROR
//            , new String[]{ IDialogConstants.OK_LABEL }
//            , 0).open();
//        }
//      }
//      else
//      {
//        new MessageDialog
//          ( this.getShell()
//          , "Error"
//          , null
//          , "Did not found Pepper module resolver!"
//          , MessageDialog.ERROR
//          , new String[]{ IDialogConstants.OK_LABEL }
//          , 0).open();
//      }
//    }
//
//    pepperModuleList = modules != null ? modules : Collections.<P>emptyList();
//  }
//  
//  return pepperModuleList;
//}


/**
 * Gets the previously selected module name.
 * @return
 */
public PepperModuleDesc getPreviouslySelectedPepperModule()
{
	Collection<PepperModuleDesc> moduleList = getPepper().getRegisteredModules();
  String moduleName = getDialogSettings().get(DIALOG_SETTINGS_MODULE);
  if (0 < moduleList.size() && moduleName != null)
  {
    for (PepperModuleDesc module : moduleList)
    {
      if (moduleName.equals(module.getName()))
      {
        return module;
      }
    }
  }

  return null;
}
/**
 * Gets the previously selected module format description.
 * @return
 */
public FormatDesc getPreviouslySelectedFormatDesc()
{
	PepperModuleDesc module = getPepperModule();
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


protected PepperModuleDesc pepperModule;
/**
 * Returns the fingerprint of the currently selected {@link PepperModule}.
 * @return
 */
public PepperModuleDesc getPepperModule()
{
  return pepperModule;
}
/**
 * Sets the fingerprint of the <em>currently</em> selected {@link PepperModule}.
 * @param pepperModule
 */
public void setPepperModule(PepperModuleDesc pepperModule)
{
  this.pepperModule = pepperModule;
}

public abstract List<FormatDesc> getSupportedFormats();

/**
 * Returns the <em>currently</em> selected {@link FormatDesc}.
 * @param pepperModule
 */
public FormatDesc getFormatDesc()
{
  return formatDesc;
}

/**
 * Sets the <em>currently</em> selected {@link FormatDesc}.
 * @param pepperModule
 */
public void setFormatDesc(FormatDesc formatDesc)
{
  this.formatDesc = formatDesc;
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

protected abstract PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable);
}

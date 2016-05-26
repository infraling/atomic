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

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.pepper.Activator;
import org.corpus_tools.atomic.pepper.AtomicPepperConfiguration;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperModuleDesc;
import org.corpus_tools.pepper.common.StepDesc;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * An abstract base class for Pepper Wizards, i.e., import and export wizards.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public abstract class AbstractPepperWizard extends Wizard {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "AbstractPepperWizard".
	 */
	private static final Logger log = LogManager.getLogger(AbstractPepperWizard.class);
	

	protected static final String DIALOG_SETTINGS_EXCHANGE_TARGET_PATH = "exchangeTargetPath";
	protected static final String DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE = "exchangeTargetType";
	protected static final String DIALOG_SETTINGS_MODULE = "pepperModuleStepDesc";
	protected static final String DIALOG_SETTINGS_FORMAT_NAME = "formatName";
	protected static final String DIALOG_SETTINGS_FORMAT_VERSION = "formatVersion";
	protected static final String DIALOG_SETTINGS_MODULE_PROPERTY_KEYS = "modulePropertyKeys";
	protected static final String DIALOG_SETTINGS_MODULE_PROPERTY_VALUES = "modulePropertyValues";

	@Deprecated
	protected static final String DIALOG_SETTINGS_EXCHANGE_DIRECTORY = "exchangeDirectory";

	public static final String SALT_XML_FORMAT_NAME = "SaltXML";
	public static final String SALT_XML_FORMAT_VERSION = "1.0";

	protected static final ImageDescriptor DEFAULT_PAGE_IAMGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.getDefault().getBundle().getSymbolicName(), "/pepper64.png");

//	protected static final AtomicInteger PEPPER_JOB_ID = new AtomicInteger();

//	static {
//		System.setProperty("PepperModuleResolver.TemprorariesURI", System.getProperty("java.io.tmpdir"));
//		System.setProperty("PepperModuleResolver.ResourcesURI", System.getProperty("java.io.tmpdir"));
//	}

	protected final MODULE_TYPE wizardMode;

	protected Pepper pepper;
	protected List<PepperModuleDesc> pepperModuleDescriptions;
	protected StepDesc pepperModuleStepDesc;
	protected PepperModuleProperties pepperModuleProperties;
	protected String exchangeTargetPath;
	protected ExchangeTargetType exchangeTargetType = ExchangeTargetType.DIRECTORY;

	protected AbstractPepperWizard(String windowTitle, MODULE_TYPE wizardMode) {
		this.wizardMode = wizardMode;
		setWindowTitle(windowTitle);
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(DEFAULT_PAGE_IAMGE_DESCRIPTOR);
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#getDialogSettings()
	 */
	@Override
	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = super.getDialogSettings();
		if (settings == null) {
			settings = DialogSettings.getOrCreateSection(Activator.getDefault().getDialogSettings(), "pepperWizard:" + getClass().getName());
			setDialogSettings(settings);
		}
		return settings;
	}

	/**
	 * Initializes the wizard, sets up the Pepper instance
	 * and module properties object.
	 */
	public void initialize() {
		ServiceReference<Pepper> reference = Activator.getDefault().getBundle().getBundleContext().getServiceReference(Pepper.class);
		if (reference != null) {
			Pepper pepper = Activator.getDefault().getBundle().getBundleContext().getService(reference);
			pepper.setConfiguration(new AtomicPepperConfiguration());
			setPepper(pepper);
		}
		pepperModuleProperties = new PepperModuleProperties();
		readDialogSettings();
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	public MODULE_TYPE getWizardMode() {
		return wizardMode;
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	public List<PepperModuleDesc> getPepperModules() {
		if (pepperModuleDescriptions == null) {
			pepperModuleDescriptions = new ArrayList<>();
		}
		if (!pepperModuleDescriptions.isEmpty()) {
			return pepperModuleDescriptions;
		}
		else {
			// Load the configuration
			AtomicPepperConfiguration configuration = (AtomicPepperConfiguration) getPepper().getConfiguration();
			configuration.load();
			String path = configuration.getPlugInPath();
			// Find all JAR files in pepper-modules directory
			File[] fileLocations = new File(path).listFiles((FilenameFilter) new SuffixFileFilter(".jar"));
			List<Bundle> moduleBundles = new ArrayList<>();
			if (fileLocations != null) {
				// Install JARs as OSGi bundles
				for (File bundleJar : fileLocations) {
					if (bundleJar.isFile() && bundleJar.canRead()) {
						URI bundleURI = bundleJar.toURI();
						Bundle bundle = null;
						try {
							bundle = Activator.getDefault().getBundle().getBundleContext().installBundle(bundleURI.toString());
							moduleBundles.add(bundle);
						}
						catch (BundleException e) {
							log.debug("Could not install bundle {}!", bundleURI.toString());
						}
					}
				}
				// Start bundles
				for (Bundle bundle : moduleBundles) {
					try {
						bundle.start();
					}
					catch (BundleException e) {
						log.debug("Could not start bundle {}!", bundle.getSymbolicName());
					}
				}
			}
			// Compile list of module description for use in pages
			if (getPepper() != null) {
				Collection<PepperModuleDesc> allModuleDescs = getPepper().getRegisteredModules();
				if (allModuleDescs != null) {
					for (PepperModuleDesc desc : allModuleDescs) {
						if (desc.getModuleType() == wizardMode) {
							pepperModuleDescriptions.add(desc);
						}
					}
				}
			}
			if (pepperModuleDescriptions.isEmpty()) {
				new MessageDialog(this.getShell(), "Error", null, "Did not find any Pepper module of type " + wizardMode.name() + "!", MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0).open();
			}
			// Sort list of module descriptions
			Collections.sort(pepperModuleDescriptions, new Comparator<PepperModuleDesc>() {
				@Override
				public int compare(PepperModuleDesc o1, PepperModuleDesc o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			return pepperModuleDescriptions;
		}

	}

//	public PepperConverter getPepperConverter() {
//		return pepperConverter;
//	}

	public StepDesc getPreferredPepperModule() {
		List<PepperModuleDesc> moduleList = getPepperModules();
		String moduleName = getDialogSettings().get(DIALOG_SETTINGS_MODULE);
		if (0 < moduleList.size() && moduleName != null) {
			for (PepperModuleDesc moduleDesc : moduleList) {
				if (moduleName.equals(moduleDesc.getName())) {
					return new StepDesc().setName(moduleName).setModuleType(getWizardMode());
				}
			}
		}

		return null;
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	public abstract List<FormatDesc> getSupportedFormats();
//
//	public FormatDefinition getFormatDefinition() {
//		return formatDefinition;
//	}
//
//	public FormatDefinition getPreferredFormatDefinition() {
//		P module = getPepperModule();
//		String formatName = getDialogSettings().get(DIALOG_SETTINGS_FORMAT_NAME);
//		String formatVersion = getDialogSettings().get(DIALOG_SETTINGS_FORMAT_VERSION);
//		if (module != null && formatName != null && formatVersion != null) {
//			for (FormatDefinition fd : getSupportedFormats()) {
//				if (formatName.equals(fd.getFormatName()) && formatVersion.equals(fd.getFormatVersion())) {
//					return fd;
//				}
//			}
//		}
//
//		return null;
//	}
//
//	public void setFormatDefinition(FormatDefinition formatDefinition) {
//		this.formatDefinition = formatDefinition;
//	}

	public String getExchangeTargetPath() {
		return exchangeTargetPath;
	}

	public void setExchangeTargetPath(String exchangeTargetPath) {
		this.exchangeTargetPath = exchangeTargetPath;
	}

	public ExchangeTargetType getExchangeTargetType() {
		return exchangeTargetType;
	}

	public void setExchangeTargetType(ExchangeTargetType exchangeTargetType) {
		this.exchangeTargetType = exchangeTargetType;
	}

	public PepperModuleProperties getPepperModuleProperties() {
		return pepperModuleProperties;
	}

	public void addPepperModuleProperty(PepperModuleProperty<?> pepperModuleProperty) {
		pepperModuleProperties.addProperty(pepperModuleProperty);
	}

	public boolean containsPepperModuleProperty(String propertyName) {
		PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
		return property != null;
	}

	public String getPepperModulePropertyValue(String propertyName) {
		PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
		Object value = property != null ? property.getValue() : null;

		return value != null ? value.toString() : null;
	}

	public void setPepperModulePropertyValue(String propertyName, String propertyValueString) {
		PepperModuleProperty<?> property = pepperModuleProperties.getProperty(propertyName);
		if (property != null) {
			property.setValueString(propertyValueString);
		}
	}

	public void removePepperModuleProperty(String propertyName) {
		// es gibt kein remove in PepperModuleProperties
		PepperModuleProperty<?> property;
		property = pepperModuleProperties.getProperty(propertyName);
		if (property != null) {
			PepperModuleProperties properties = new PepperModuleProperties();
			for (String name : pepperModuleProperties.getPropertyNames()) {
				if (!name.equals(propertyName)) {
					properties.addProperty(pepperModuleProperties.getProperty(name));
				}
			}
			pepperModuleProperties = properties;
		}
	}

	public void advance() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage != null) {
			IWizardPage nextPage = getNextPage(currentPage);
			if (nextPage != null) {
				getContainer().showPage(nextPage);
			}
		}
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose() {
		if (pepper != null) {
			exchangeTargetPath = null;
			exchangeTargetType = ExchangeTargetType.DIRECTORY;
			pepperModuleStepDesc = null;
			pepperModuleDescriptions = null;
			pepper = null;
		}
		super.dispose();
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	protected boolean canPerformFinish() {
		return pepperModuleStepDesc != null /* && formatDefinition != null */ && exchangeTargetPath != null;
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 * @throws CoreException
	 */
	protected abstract IProject getProject() throws CoreException;

	/**
	 * TODO: Description
	 *
	 * @param project
	 * @param cancelable
	 * @return
	 */
	protected abstract PepperModuleRunnable createModuleRunnable(IProject project, boolean cancelable);

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			if (canPerformFinish()) {
				System.err.println("-----------------> " + getPepperModuleStepDesc().getName());
				return true;
//				IProject project = getProject();
//
//				PepperModuleRunnable moduleRunnable = createModuleRunnable(project, true);
//				PlatformUI.getWorkbench().getProgressService().run(false, true, moduleRunnable);
//
//				boolean outcome = moduleRunnable.get().booleanValue();
//
//				if (outcome) {
//					writeDialogSettings();
//				}
//
//				return outcome;
			}
			else {
				return false;
			}
		}
		catch (CancellationException e) {
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * TODO: Description
	 *
	 */
	protected void readDialogSettings() {
		IDialogSettings settings = getDialogSettings();
		exchangeTargetPath = settings.get(DIALOG_SETTINGS_EXCHANGE_TARGET_PATH);
		if (exchangeTargetPath == null) {
			exchangeTargetPath = settings.get(DIALOG_SETTINGS_EXCHANGE_DIRECTORY);
		}

		String exchangeTargetTypeName = settings.get(DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE);
		exchangeTargetType = exchangeTargetTypeName != null ? ExchangeTargetType.valueOf(exchangeTargetTypeName) : ExchangeTargetType.DIRECTORY;

		String[] modulePropertyKeys = settings.getArray(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS);
		if (modulePropertyKeys != null && 0 < modulePropertyKeys.length) {
			String[] modulePropertyValues = settings.getArray(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES);
			if (modulePropertyValues != null && modulePropertyKeys.length == modulePropertyValues.length) {
				for (int i = 0; i < modulePropertyKeys.length; ++i) {
					PepperModuleProperty<String> pepperModuleProperty = new PepperModuleProperty<String>(modulePropertyKeys[i], String.class, "", "");
					pepperModuleProperty.setValueString(modulePropertyValues[i]);
					addPepperModuleProperty(pepperModuleProperty);
				}
			}
		}
	}

	/**
	 * TODO: Description
	 *
	 */
	protected void writeDialogSettings() {
		IDialogSettings settings = getDialogSettings();

		settings.put(DIALOG_SETTINGS_EXCHANGE_TARGET_PATH, exchangeTargetPath);
		settings.put(DIALOG_SETTINGS_EXCHANGE_TARGET_TYPE, exchangeTargetType.name());

		StepDesc moduleStepDesc = getPepperModuleStepDesc();
		settings.put(DIALOG_SETTINGS_MODULE, moduleStepDesc != null ? moduleStepDesc.getName() : null);

//		FormatDefinition fd = getFormatDefinition();
//		settings.put(DIALOG_SETTINGS_FORMAT_NAME, fd != null ? fd.getFormatName() : null);
//		settings.put(DIALOG_SETTINGS_FORMAT_VERSION, fd != null ? fd.getFormatVersion() : null);

		PepperModuleProperties moduleProperties = getPepperModuleProperties();
		if (moduleProperties != null) {
			Collection<String> propertyNames = moduleProperties.getPropertyNames();
			if (propertyNames != null) {
				String[] modulePropertyKeys = new String[propertyNames.size()];
				String[] modulePropertyValues = new String[propertyNames.size()];
				int index = 0;
				for (String propertyName : propertyNames) {
					modulePropertyKeys[index] = propertyName;
					Object value = moduleProperties.getProperty(propertyName).getValue();
					modulePropertyValues[index] = value != null ? value.toString() : null;
					++index;
				}

				settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, modulePropertyKeys);
				settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, modulePropertyValues);
			}
			else {
				settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, (String[]) null);
				settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, (String[]) null);
			}
		}
		else {
			settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_KEYS, (String[]) null);
			settings.put(DIALOG_SETTINGS_MODULE_PROPERTY_VALUES, (String[]) null);
		}
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public static enum ExchangeTargetType {
		FILE, DIRECTORY
	}

	/**
	 * @return the pepper
	 */
	public Pepper getPepper() {
		return pepper;
	}
	
	/**
	 * @param pepper The {@link Pepper} to set
	 */
	public void setPepper(Pepper pepper) {
		this.pepper = pepper;
//		if (!getPepper().isInitialized()) {
//			getPepper().init();
//		}
	}

	/**
	 * @return the pepperModuleStepDesc
	 */
	public StepDesc getPepperModuleStepDesc() {
		return pepperModuleStepDesc;
	}

	/**
	 * @param pepperModuleStepDesc the pepperModuleStepDesc to set
	 */
	public void setPepperModuleStepDesc(StepDesc pepperModuleStepDesc) {
		this.pepperModuleStepDesc = pepperModuleStepDesc;
	}

//	/**
//	 * TODO: Description
//	 *h
//	 * @param firstElement
//	 */
//	public void setPepperModuleDesc(PepperModuleDesc moduleDesc) {
//		this.pepperModule
//	}


}

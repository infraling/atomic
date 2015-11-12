/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
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
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.pepper.update;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.apache.commons.io.filefilter.SuffixFileFilter;


import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.exceptions.PepperOSGiException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.exceptions.PepperPropertyException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.MavenAccessor;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector;
import de.hu_berlin.german.korpling.saltnpepper.pepper.core.PepperOSGiRunner;
import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperConfigurationException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperException;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class AtomicPepperOSGiConnector extends PepperOSGiConnector {
	
	/** 
	 * Defines a static log variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "AtomicPepperOSGiConnector".
	 */
	private static final Logger log = LogManager.getLogger(AtomicPepperOSGiConnector.class);
	
	private AtomicPepperConfiguration properties;

	/** Stores all bundle ids and the corresponding bundles. */
	private Map<Long, Bundle> bundleIdMap = new Hashtable<Long, Bundle>();

	/** this String contains the artifactId of pepper-framework. */
	private static final String ARTIFACT_ID_PEPPER_FRAMEWORK = "pepper-framework";
	
	/**
	 * contains the version of pepper framework. {@link #PEPPER_VERSION} is not
	 * used on purpose. This {@link String} contains the value of the
	 * pepper-framework OSGi {@link Bundle}.
	 */
	private String frameworkVersion = null;
	
	private MavenAccessor maven = null;
	
	/** Determines if this object has been initialized **/
	private boolean isInit = false;

	public boolean isInitialized() {
		return (isInit);
	}

	/**
	 * Starts the OSGi environment and installs and starts all bundles located
	 * in the plugin directory. <br/>
	 * Sets property {@link PepperOSGiRunner#PROP_TEST_DISABLED} to true.
	 */
	@Override
	public void init() {
		if (getPepperStarterConfiguration().getPlugInPath() == null) {
			throw new PepperPropertyException("Cannot start Pepper, because no plugin path is given for Pepper modules.");
		}
		File pluginPath = new File(getPepperStarterConfiguration().getPlugInPath());
		if (!pluginPath.exists()) {
			throw new PepperOSGiException("Cannot load any plugins, since the configured path for plugins '" + pluginPath.getAbsolutePath() + "' does not exist. Please check the entry '" + PepperStarterConfiguration.PROP_PLUGIN_PATH + "' in the Pepper configuration file at '" + getConfiguration().getConfFolder().getAbsolutePath() + "'. ");
		}

		try {
			// disable PepperOSGiRunner
			System.setProperty(PepperOSGiRunner.PROP_TEST_DISABLED, Boolean.TRUE.toString());

			setBundleContext(this.startEquinox());
		} catch (Exception e) {
			throw new PepperOSGiException("The OSGi environment could not have been started: " + e.getMessage(), e);
		}
		try {
			log.debug("plugin path:\t\t" + getPepperStarterConfiguration().getPlugInPath());

			log.debug("installing OSGI-bundles...");
			log.debug("-------------------- installing bundles --------------------");
			Collection<Bundle> bundles = null;

			// installing module-bundles
			List<URI> dropInURIs = null;
			List<String> dropInRawStrings = getPepperStarterConfiguration().getDropInPaths();
			if (dropInRawStrings != null) {
				dropInURIs = new ArrayList<>(dropInRawStrings.size());
				for (String path : dropInRawStrings) {
					dropInURIs.add(new File(path).toURI());
				}
			}
			log.debug("\tinstalling OSGI-bundles:");

			bundles = this.installBundles(new File(getPepperStarterConfiguration().getPlugInPath()).toURI(), dropInURIs);
			log.debug("----------------------------------------------------------");
			log.debug("installing OSGI-bundles...FINISHED");
			log.debug("starting OSGI-bundles...");
			log.debug("-------------------- starting bundles --------------------");
			if ((bundles == null) || (bundles.isEmpty())) {
				bundles = new ArrayList<>();
				bundleIdMap = new Hashtable<>();
				for (Bundle bundle : getBundleContext().getBundles()) {
					bundles.add(bundle);
					bundleIdMap.put(bundle.getBundleId(), bundle);
				}
			}

			this.startBundles(bundles);
			log.debug("----------------------------------------------------------");
			log.debug("starting OSGI-bundles...FINISHED");
		} catch (PepperException e) {
			throw e;
		} catch (Exception e) {
			throw new PepperOSGiException("An exception occured installing bundles for OSGi environment. ", e);
		}

		List<Bundle> bList = new ArrayList<Bundle>();
		bList.addAll(bundleIdMap.values());
		for (int i = 0; i < bList.size(); i++) {
			if (bList.get(i).getSymbolicName() != null && bList.get(i).getSymbolicName().contains(ARTIFACT_ID_PEPPER_FRAMEWORK)) {
				frameworkVersion = bList.get(i).getVersion().toString().replace(".SNAPSHOT", "-SNAPSHOT");
			}
		}
		maven = new MavenAccessor(this);

		isInit = true;
	}
	
	/**
	 * Starts the passed bundle
	 * 
	 * @param bundle
	 */
	public void start(Long bundleId) {
		Bundle bundle = bundleIdMap.get(bundleId);
		System.err.println(bundle);
		log.debug("\t\tstarting bundle: " + bundle.getSymbolicName() + "-" + bundle.getVersion());
		if (bundle.getState() != Bundle.ACTIVE) {
			try {
				bundle.start();
			} catch (BundleException e) {
				log.warn("The bundle '" + bundle.getSymbolicName() + "-" + bundle.getVersion() + "' wasn't started correctly. This could cause other problems. For more details turn on log mode to debug and see log file. ", e);
			}
		}
		if (bundle.getState() != Bundle.ACTIVE) {
			log.error("The bundle '" + bundle.getSymbolicName() + "-" + bundle.getVersion() + "' wasn't started correctly.");
		}
	}
	
	/**
	 * Must be overridden for use in Atomic as the original tried to start a new Equinox instance. 
	 * Installs and starts all bundles located
	 * in the plugin directory. <br/>
	 * Sets property {@link PepperOSGiRunner#PROP_TEST_DISABLED} to true.
	 */
//	@Override
//	public void init() {
//		if (getPepperStarterConfiguration().getPlugInPath() == null) {
//			throw new PepperPropertyException("Cannot start Pepper, because no plugin path is given for Pepper modules.");
//		}
//		File pluginPath = new File(getPepperStarterConfiguration().getPlugInPath());
//		if (!pluginPath.exists()) {
//			throw new PepperOSGiException("Cannot load any plugins, since the configured path for plugins '" + pluginPath.getAbsolutePath() + "' does not exist. Please check the entry '" + PepperStarterConfiguration.PROP_PLUGIN_PATH + "' in the Pepper configuration file at '" + getConfiguration().getConfFolder().getAbsolutePath() + "'. ");
//		}
//
//		try {
//			// disable PepperOSGiRunner
//			System.setProperty(PepperOSGiRunner.PROP_TEST_DISABLED, Boolean.TRUE.toString());
//
//			setBundleContext(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
//		} catch (Exception e) {
//			throw new PepperOSGiException("The OSGi environment could not have been started: " + e.getMessage(), e);
//		}
//		try {
//			log.debug("plugin path:\t\t" + getPepperStarterConfiguration().getPlugInPath());
//
//			log.debug("installing OSGI-bundles...");
//			log.debug("-------------------- installing bundles --------------------");
//			Collection<Bundle> bundles = null;
//
//			// installing module-bundles
//			List<URI> dropInURIs = null;
//			List<String> dropInRawStrings = getPepperStarterConfiguration().getDropInPaths();
//			if (dropInRawStrings != null) {
//				dropInURIs = new ArrayList<>(dropInRawStrings.size());
//				for (String path : dropInRawStrings) {
//					dropInURIs.add(new File(path).toURI());
//				}
//			}
//			log.debug("\tinstalling OSGI-bundles:");
//
//			bundles = this.installBundles(new File(getPepperStarterConfiguration().getPlugInPath()).toURI(), dropInURIs);
//			System.err.println("BUNDLE SIZE " + bundles.size());
//			log.debug("----------------------------------------------------------");
//			log.debug("installing OSGI-bundles...FINISHED");
//			log.debug("starting OSGI-bundles...");
//			log.debug("-------------------- starting bundles --------------------");
//			if ((bundles == null) || (bundles.isEmpty())) {
//				bundles = new ArrayList<>();
//				bundleIdMap = new Hashtable<>();
//				for (Bundle bundle : getBundleContext().getBundles()) {
//					bundles.add(bundle);
//					bundleIdMap.put(bundle.getBundleId(), bundle);
//				}
//			}
//
//			this.startBundles(bundles);
//			log.debug("----------------------------------------------------------");
//			log.debug("starting OSGI-bundles...FINISHED");
//		} catch (PepperException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new PepperOSGiException("An exception occured installing bundles for OSGi environment. ", e);
//		}
//
//		List<Bundle> bList = new ArrayList<Bundle>();
//		bList.addAll(bundleIdMap.values());
//		for (int i = 0; i < bList.size(); i++) {
//			System.err.println(bList.get(i).getSymbolicName());
//			if (bList.get(i).getSymbolicName() != null && bList.get(i).getSymbolicName().contains(ARTIFACT_ID_PEPPER_FRAMEWORK)) {
//				frameworkVersion = bList.get(i).getVersion().toString().replace(".SNAPSHOT", "-SNAPSHOT");
//			}
//		}
//		maven = new MavenAccessor(this);
//
//		isInit = true;
//	}
	
	/**
	 * Needs to override {@link PepperOSGiConnector#setConfiguration(PepperConfiguration)}, as the original implementation
	 * expects the argument to be of type {@link PepperStarterConfiguration}, otherwise its throwing an error.
	 *  
	 * @copydoc @see de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector#setConfiguration(de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration)
	 */
	@Override
	public void setConfiguration(PepperConfiguration configuration) {
		log.trace("Setting Pepper configuration for {}.", AtomicPepperOSGiConnector.class.getSimpleName());
		if (configuration instanceof AtomicPepperConfiguration) {
			log.trace("Setting properties to configuration: {}.", configuration);
			this.properties = (AtomicPepperConfiguration) configuration;
		} else {
			throw new PepperConfigurationException("Cannot set the given configuration, since it is not of type '" + AtomicPepperConfiguration.class.getSimpleName() + "'.");
		}
	}
	
	/**
	 * Historical name kept as it might be used from original class.
	 * @return configuration as {@link AtomicPepperConfiguration}
	 **/
	@Override
	public AtomicPepperConfiguration getPepperStarterConfiguration() {
		return properties;
	}
	
	/**
	 * Historical name kept as it might be used from original class.
	 * @return configuration as {@link AtomicPepperConfiguration}
	 **/
	@Override
	public AtomicPepperConfiguration getConfiguration() {
		return properties;
	}
	
	/**
	 * Tries to install all jar-files, of the given pluginPath. <br/>
	 * Each installed jar will be added to system property
	 * {@value #PROP_OSGI_BUNDLES} as reference:file:JAR_FILE.
	 * 
	 * @param pluginPath
	 *            path where the bundles are
	 * @param bundleAction
	 *            a flag, which shows if bundle has to be started or just
	 *            installed
	 * @param dropinPaths
	 *            A list of additionally paths to load bundles from
	 * 
	 * @throws BundleException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	protected Collection<Bundle> installBundles(URI pluginPath, List<URI> dropinPaths) throws BundleException, URISyntaxException, IOException {

		ArrayList<Bundle> bundles = new ArrayList<>();

		List<URI> loadLocations = new LinkedList<>();
		if (dropinPaths != null) {
			loadLocations.addAll(dropinPaths);
		}
		loadLocations.add(pluginPath);

		StringBuilder osgiBundlesProp = null;

		for (URI dropinLocation : loadLocations) {
			File[] fileLocations = new File(dropinLocation.getPath()).listFiles((FilenameFilter) new SuffixFileFilter(".jar"));
			if (fileLocations != null) {
				for (File bundleJar : fileLocations) {
					// check if file is file-object
					if (bundleJar.isFile() && bundleJar.canRead()) {
						// check if file is file jar
						URI bundleURI = bundleJar.toURI();
						Bundle bundle = install(bundleURI);
						if (bundle != null) {
							bundles.add(bundle);
							log.debug("\t\tinstalling bundle: " + bundle.getSymbolicName() + "-" + bundle.getVersion());

							// set system property for bundle pathes
							if (osgiBundlesProp == null) {
								osgiBundlesProp = new StringBuilder();
							}
							osgiBundlesProp.append("reference:");
							osgiBundlesProp.append(bundleURI);
							osgiBundlesProp.append(",");
						}
					}
				}
			}
		}
		if ((System.getProperty(PROP_OSGI_BUNDLES) == null) || (System.getProperty(PROP_OSGI_BUNDLES).isEmpty())) {
			System.setProperty(PROP_OSGI_BUNDLES, osgiBundlesProp.toString());
		}
		return (bundles);
	}
	
	
	private Map<String, String> frameworkProperties = null;
	/**
	 * Starts the OSGi Equinox environment.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected BundleContext startEquinox() throws Exception {
		BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
//
//		frameworkProperties = new HashMap<String, String>();
//		frameworkProperties.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, getSharedPackages());
//		frameworkProperties.put(EclipseStarter.PROP_CLEAN, "true");
//		frameworkProperties.put(EclipseStarter.PROP_CONSOLE, "true");
//		frameworkProperties.put(EclipseStarter.PROP_NOSHUTDOWN, "true");
//		frameworkProperties.put(EclipseStarter.PROP_INSTALL_AREA, getConfiguration().getTempPath().getCanonicalPath());
//
//		EquinoxFactory ff = ServiceLoader.load(EquinoxFactory.class).iterator().next();
//		// add some params to config ...
//		Framework fwk = ff.newFramework(frameworkProperties);
//		fwk.start();
////		EclipseStarter.setInitialProperties(frameworkProperties);
////		bc = EclipseStarter.startup(new String[] {}, null);
//		bc = fwk.getBundleContext();
//
		return bc;
	}

}

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
package org.corpus_tools.atomic.pepper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.exceptions.PepperOSGiException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.exceptions.PepperOSGiFrameworkPluginException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.exceptions.PepperPropertyException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector;
import de.hu_berlin.german.korpling.saltnpepper.pepper.core.PepperOSGiRunner;
import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperConfigurationException;

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
	
	
	private AtomicPepperConfiguration properties = null;
	
	/** Stores all bundle ids and the corresponding bundles. */
	private Map<Long, Bundle> bundleIdMap = new Hashtable<Long, Bundle>();
//	/** Stores all locations of bundles and the corresponding bundle ids **/
//	private Map<URI, Long> locationBundleIdMap = new Hashtable<URI, Long>();
	/**
	 * Contains the version of the Pepper framework. {@link #PEPPER_VERSION} is not
	 * used on purpose. This {@link String} contains the value of the
	 * pepper-framework OSGi {@link Bundle}.
	 */
	private String frameworkVersion = null;
	/** this String contains the artifactId of pepper-framework. */
	private static final String ARTIFACT_ID_PEPPER_FRAMEWORK = "pepper-framework";
	private AtomicMavenAccessor maven = null;
	/** Determines if this object has been initialized **/
	private boolean isInit = false;

	@Override
	public void init() {
		if (getAtomicPepperConfiguration().getPlugInPath() == null) {
			throw new PepperPropertyException("Cannot start Pepper, because no plugin path is given for Pepper modules.");
		}
		File pluginPath = new File(getAtomicPepperConfiguration().getPlugInPath());
		if (!pluginPath.exists()) {
			throw new PepperOSGiException("Cannot load any plugins, since the configured path for plugins '" + pluginPath.getAbsolutePath() + "' does not exist. Please check the entry '" + PepperStarterConfiguration.PROP_PLUGIN_PATH + "' in the Pepper configuration file at '" + getConfiguration().getConfFolder().getAbsolutePath() + "'.");
		}
		try {
			// Disable PepperOSGiRunner and set bundle context
			System.setProperty(PepperOSGiRunner.PROP_TEST_DISABLED, Boolean.TRUE.toString());
			setBundleContext(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
		} catch (Exception e) {
			throw new PepperOSGiException("The OSGi environment could not have been started: " + e.getMessage(), e);
		}
//		try {
//			log.debug("plugin path:\t\t" + getAtomicPepperConfiguration().getPlugInPath());
//
//			log.debug("installing OSGI-bundles...");
//			log.debug("-------------------- installing bundles --------------------");
//			Collection<Bundle> bundles = null;

			// installing module-bundles
//			List<URI> dropInURIs = null;
//			List<String> dropInRawStrings = getAtomicPepperConfiguration().getDropInPaths();
//			if (dropInRawStrings != null) {
//				dropInURIs = new ArrayList<>(dropInRawStrings.size());
//				for (String path : dropInRawStrings) {
//					dropInURIs.add(new File(path).toURI());
//				}
//			}
//			log.debug("\tinstalling OSGI-bundles:");

//			bundles = this.installBundles(new File(getAtomicPepperConfiguration().getPlugInPath()).toURI(), dropInURIs);
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
		Bundle[] bundles = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			String bundleName = bundle.getSymbolicName();
			if (bundleName != null && bundleName.contains(ARTIFACT_ID_PEPPER_FRAMEWORK)) {
				frameworkVersion = bundle.getVersion().toString().replace(".SNAPSHOT", "-SNAPSHOT");
			}
		}
		maven = new AtomicMavenAccessor(this);

		isInit = true;
	}
	
	@Override
	public void setConfiguration(PepperConfiguration configuration) {
		if (configuration instanceof AtomicPepperConfiguration) {
			this.properties = (AtomicPepperConfiguration) configuration;
		} else {
			throw new PepperConfigurationException("Cannot set the given configuration, since it is not of type '" + AtomicPepperConfiguration.class.getSimpleName() + "'.");
		}
	}
	
	/**
	 * Starts all bundle being contained in the given list of bundles.
	 * 
	 * @param bundles
	 *            a list of bundles to start
	 * @throws BundleException
	 */
	protected void startBundles(Collection<Bundle> bundles) throws BundleException {
		if (bundles != null) {
			Bundle pepperBundle = null;
			for (Bundle bundle : bundles) {
				// TODO this is a workaround, to fix that module resolver is
				// loaded as last bundle, otherwise, some modules will be
				// ignored
				if ("de.hu_berlin.german.korpling.saltnpepper.pepper-framework".equalsIgnoreCase(bundle.getSymbolicName())) {
					pepperBundle = bundle;
				} else {
					start(bundle.getBundleId());
				}
			}
			try {
				if (pepperBundle != null) {
					pepperBundle.start();
				}
			} catch (BundleException e) {
				throw new PepperOSGiFrameworkPluginException("The Pepper framework bundle could not have been started. Unfortunatly Pepper cannot be started without that OSGi bundle. ", e);
			}
		}
	}
	
	/**
	 * Starts the passed bundle
	 * 
	 * @param bundle
	 */
	public void start(Long bundleId) {
		Bundle bundle = bundleIdMap.get(bundleId);
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

	/** {@inheritDoc Pepper#getConfiguration()} **/
	@Override
	public PepperConfiguration getConfiguration() {
		return properties;
	}
	
	/**
	 * @return configuration as {@link AtomicPepperConfiguration}
	 **/
	public AtomicPepperConfiguration getAtomicPepperConfiguration() {
		return properties;
	}

}

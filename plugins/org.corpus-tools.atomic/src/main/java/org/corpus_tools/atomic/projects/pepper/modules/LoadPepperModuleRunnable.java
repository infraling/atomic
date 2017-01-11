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
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.projects.pepper.modules;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.Activator;
import org.corpus_tools.atomic.projects.pepper.AtomicPepperConfiguration;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.connectors.PepperConnector;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Loads and starts all available {@link Pepper} modules from the 'pepper-modules' directory.
 * 
 *   TODO FIXME: Multi-thread this!
 *   Cf. PepperModuleRunnable!
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class LoadPepperModuleRunnable implements IRunnableWithProgress {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "LoadPepperModuleRunnable".
	 */
	private static final Logger log = LogManager.getLogger(LoadPepperModuleRunnable.class);
	private PepperConnector pepper;
	

	/**
	 * @param pepper The {@link Pepper} instance to use
	 */
	public LoadPepperModuleRunnable(PepperConnector pepper) {
		this.pepper = pepper;
	}


	/* 
	 * @copydoc @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Loading available Pepper modules ...", IProgressMonitor.UNKNOWN);
		log.info("Loading Pepper modules.");
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
				if (bundle.getState() != Bundle.ACTIVE) {
					try {
						bundle.start();
					}
					catch (BundleException e) {
						log.debug("Could not start bundle {}!", bundle.getSymbolicName());
					}
				}
			}
		}
		
	}


	/**
	 * @return the pepper
	 */
	private PepperConnector getPepper() {
		return pepper;
	}

}

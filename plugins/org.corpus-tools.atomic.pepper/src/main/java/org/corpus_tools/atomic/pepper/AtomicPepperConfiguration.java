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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Splitter;

import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperUtil;

/**
 * This class represents all properties that Pepper as used in Atomic
 * has. The properties themselves are loaded from the "pepper.properties"
 * file. The location of the file is FIXME in the configuration folder
 * of the Pepper home directory as defined by {@link #findPepperHome()}.
 * <p>The properties also include information about the location of
 * paths needed for bundle-level operations.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
@SuppressWarnings("serial")
public class AtomicPepperConfiguration extends PepperConfiguration {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "AtomicPepperConfiguration".
	 */
	private static final Logger log = LogManager.getLogger(AtomicPepperConfiguration.class);

	/**
	 * Loads the "pepper.properties" file via the 
	 * {@link Properties#load(java.io.InputStream)} mechanism ultimately
	 * at the end of the inheritance chain.
	 * 
	 */
	public void load() {
		File pepperHome = findPepperHome();
		File propFile = new File(pepperHome + "/conf/pepper.properties/");
		try {
			this.setProperty(PepperStarterConfiguration.PROP_PEPPER_HOME, pepperHome.getAbsolutePath());
		}
		catch (Exception e) {
			log.error("Pepper home has not been found!", e);
		}
		try {
			// Set the plugin path
			File atomicPluginPath = pepperHome.getParentFile();
			this.setProperty(PepperStarterConfiguration.PROP_PLUGIN_PATH, atomicPluginPath.getAbsolutePath());
			log.trace("Set plugin path: {}.", this.getProperty(PepperStarterConfiguration.PROP_PLUGIN_PATH));

		}
		catch (Exception e) {
			log.error("Plugin path could no be set for some reason.", e);
		}
		try {
			load(propFile);
		}
		catch (Exception e) {
			log.error("Could not load Pepper configuration for Atomic!", e);
		}
	}

	/**
	 * Tries to "find" the Pepper home directory, which is in fact the directory of the bundle o.c-t.a.pepper.
	 *
	 * @return pepperHome The pepperHome File
	 */
	private File findPepperHome() {
		File pepperHome = null;
		Bundle atomicPepperBundle = FrameworkUtil.getBundle(this.getClass());
		URL bundleURL = FileLocator.find(atomicPepperBundle, new Path("/"), null);
		URL pepperHomeURL = null;
		try {
			pepperHomeURL = FileLocator.resolve(bundleURL);
			pepperHome = new File(pepperHomeURL.getFile());
		}
		catch (IOException e) {
			log.error("Could not resolve pepper home URL!", e);
		}
		return pepperHome;
	}

	/**
	 * Returns the path for the OSGi plugins folder.
	 * 
	 * @return plugIn path
	 */
	public String getPlugInPath() {
//		return (this.getProperty(PepperStarterConfiguration.PROP_PLUGIN_PATH));
		return findPepperHome().getParentFile().getParentFile().getAbsolutePath() + "/" + this.getProperty(PepperStarterConfiguration.PROP_PLUGIN_PATH);
	}

	/**
	 * Returns the dropin paths for OSGi bundles.
	 * FIXME: These are not used in the contenxt of
	 * Atomic.
	 *
	 * @return List<String> the dropin paths
	 */
	public List<String> getDropInPaths() {
			String rawList = this.getProperty(PepperStarterConfiguration.PROP_DROPIN_PATHS);
			if (rawList != null) {
				Iterator<String> it
					= Splitter.on(',').trimResults().omitEmptyStrings().split(rawList).iterator();
				List<String> result = new ArrayList<>();
				while (it.hasNext()) {
					result.add(it.next());
				}
				return result;
			}
			return null;
	}
	
	/**
	 * Returns a temporary path, where the entire system and all modules can
	 * store temp files. If no temp folder is given by configuration file, the
	 * default temporary folder given by the operating system is used.
	 * 
	 * @return path, where to store temporary files
	 */
	public File getTempPath() {
		String tmpFolderStr = getProperty(PROP_TEMP_FOLDER);
		File tmpFolder = null;
		if (tmpFolderStr != null) {
			tmpFolderStr = tmpFolderStr + "/pepper/";
			tmpFolder = new File(tmpFolderStr);
			if (!tmpFolder.exists()) {
				if (!tmpFolder.mkdirs()) {
					log.warn("Cannot create folder {}. ", tmpFolder);
				}
			}
		} else {
			tmpFolder = PepperUtil.getTempFile();
		}
		return (tmpFolder);
	}

	/**
	 * Returns the content of property {@link #PROP_OSGI_SHAREDPACKAGES}.
	 * 
	 * @return plugIn path
	 */
	public String getSharedPackages() {
		return (this.getProperty("pepper." + Constants.FRAMEWORK_SYSTEMPACKAGES));
	}

}

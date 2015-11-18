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
import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;

/**
 * TODO Description
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
	 * TODO: Description
	 */
	public void load() {
		File pepperHome = findPepperHome();
		File propFile = new File(pepperHome + "/conf/pepper.properties/");
		try {
			System.setProperty(PepperStarterConfiguration.PROP_PEPPER_HOME, pepperHome.getAbsolutePath());
		}
		catch (Exception e) {
			log.error("Pepper home has not been found!", e);
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

}

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
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;

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

	private static final String PROP_PEPPER_HOME = "pepper.home";

	/**
	 * TODO: Description
	 */
	public void load() {
		File pepperHome = findPepperHome();
		try {
			System.setProperty(PepperStarterConfiguration.PROP_PEPPER_HOME, pepperHome.getAbsolutePath());
		}
		catch (Exception e) {
			log.error("Pepper home has not been found!", e);
		}
		try {
			super.load(pepperHome);
		}
		catch (Exception e) {
			log.error("Could not load Pepper configuration for Atomic!", e);
		}
	}

	/**
	 * Tries to "find" the Pepper home directory, which is in fact the
	 * Atomic home directory for the running Atomic instance,
	 * be computing the parent of the parent of the location
	 * of the {@link org.corpus_tools.atomic.pepper} bundle.
	 * <br>
	 * The dir structure should be:
	 * .../atomic/plugins/org.corpus_tools.atomic.pepper/
	 *
	 * @return pepperHome The computed File if its name equals "atomic", else null
	 */
	private File findPepperHome() {
		File pepperHome = null;
		Bundle atomicPepperBundle = FrameworkUtil.getBundle(this.getClass());
		if (atomicPepperBundle.getSymbolicName().equals("org.corpus_tools.atomic.pepper")) {
			File atomicPepperBundleLocationFile = new File(atomicPepperBundle.getLocation());
			File pepperBundleParentAsPluginsDirFile = atomicPepperBundleLocationFile.getParentFile();
			File pepperHomeAsAtomicHomeAsPluginsDirParent = pepperBundleParentAsPluginsDirFile.getParentFile();
			if (pepperHomeAsAtomicHomeAsPluginsDirParent.getName().equals("atomic")) {
				pepperHome = pepperHomeAsAtomicHomeAsPluginsDirParent;
				log.trace("Found Pepper home (Atomic home really...) at {}.", pepperHome.getAbsolutePath());
			}
			else {
				log.error("The directory File computed from the bundle location of o.c-t.a.pepper > parent > parent is not the Atomic home directory!");
			}
		}
		else {
			log.error("Could not find Pepper home directory, computed from o.c-t.a.pepper bundle location > parent (plugins dir) > parent", new FileNotFoundException());
		}
		return pepperHome;
	}

}

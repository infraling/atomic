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

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;

/**
 * TODO Description
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
@SuppressWarnings("serial")
public class AtomicPepperConfiguration extends PepperConfiguration {

	private static final String PROP_PEPPER_HOME = "pepper.home";

	/**
	 * TODO: Description
	 */
	public void load() {
		File pepperHome = findPepperHome();
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private File findPepperHome() {
		String atomicHome = null;
		Bundle atomicPepperBundle = FrameworkUtil.getBundle(this.getClass());
		String atomicPepperBundleName = atomicPepperBundle.getSymbolicName();
		System.err.println(atomicPepperBundleName);
//		atomicPepperBundle = 
//		System.setProperty(PROP_PEPPER_HOME, atomicHome);
		return null;
	}

}

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.PepperConnector;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class AtomicPepperStarter {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "AtomicPepperStarter".
	 */
	private static final Logger log = LogManager.getLogger(AtomicPepperStarter.class);
	private PepperConnector pepper = null;
	
	public void startPepper() {
		AtomicPepperOSGiConnector pepper = null;
		AtomicPepperConfiguration pepperProps = null;
		
		pepperProps = new AtomicPepperConfiguration();
		pepperProps.load();
		
		pepper = new AtomicPepperOSGiConnector();
		pepper.setConfiguration(pepperProps);
		
		
		System.err.println("PROPS " + pepperProps.getProperty(PepperStarterConfiguration.PROP_PLUGIN_PATH));
		
		setPepper(pepper);
	}
	
	/**
	 * @return the pepper
	 */
	public PepperConnector getPepper() {
		return pepper;
	}

	/**
	 * @param pepper the pepper to set
	 */
	public void setPepper(PepperConnector pepper) {
		this.pepper = pepper;
		if (!getPepper().isInitialized()) {
			getPepper().init();
		}
	}



}

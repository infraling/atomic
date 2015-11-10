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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector;
import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperConfigurationException;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class AtomicPepperOSGiConnector extends PepperOSGiConnector {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "AtomicPepperOSGiConnector".
	 */
	private static final Logger log = LogManager.getLogger(AtomicPepperOSGiConnector.class);
	
	private AtomicPepperConfiguration properties;

	/**
	 * 
	 */
	public AtomicPepperOSGiConnector() {
		// TODO Auto-generated constructor stub
	}
	
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


}

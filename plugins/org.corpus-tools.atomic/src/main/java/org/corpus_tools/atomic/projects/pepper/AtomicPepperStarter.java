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
package org.corpus_tools.atomic.projects.pepper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.connectors.PepperConnector;

/**
 * This class configures and starts an instance of {@link Pepper} (implemented in
 * {@link AtomicPepperOSGiConnector}. It also stores the Pepper instance in
 * {@link AtomicPepperStarter#pepper}, which can be got from the instantiators
 * of this class via {@link AtomicPepperStarter#getPepper()} to perform
 * actions on the Pepper object.
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
		log.trace("Loading Pepper properties via the object {}.", pepperProps);
		pepperProps.load();
		
		pepper = new AtomicPepperOSGiConnector();
		log.trace("Setting the Pepper properties ({}) as configuration in the {} object {}.", pepperProps, AtomicPepperOSGiConnector.class.getName(), pepper);
		pepper.setConfiguration(pepperProps);
		
		setPepper(pepper);
	}
	
	public void startPepperAndBridgeOSGi() { // NO_UCD (unused code)
		AtomicPepperOSGiConnector pepper = null;
		AtomicPepperConfiguration pepperProps = null;
		
		pepperProps = new AtomicPepperConfiguration();
		log.trace("Loading Pepper properties via the object {}.", pepperProps);
		pepperProps.load();
		
		pepper = new AtomicPepperOSGiConnector();
		log.trace("Setting the Pepper properties ({}) as configuration in the {} object {}.", pepperProps, AtomicPepperOSGiConnector.class.getName(), pepper);
		pepper.setConfiguration(pepperProps);
		
		pepper.addSharedPackage("org.corpus_tools.salt", "3");
	    pepper.addSharedPackage("org.corpus_tools.salt.common", "3");
	    pepper.addSharedPackage("org.corpus_tools.salt.core", "3");
	    pepper.addSharedPackage("org.corpus_tools.salt.graph", "3");
	    pepper.addSharedPackage("org.corpus_tools.salt.util", "3");             
	    pepper.init();
	    
		setPepper(pepper);
	}
	
	/**
	 * @return the Pepper instance (as a {@link PepperConnector} object).
	 */
	public PepperConnector getPepper() {
		return pepper;
	}

	/**
	 * @param pepper The {@link Pepper} to set
	 */
	public void setPepper(PepperConnector pepper) {
		this.pepper = pepper;
		if (!getPepper().isInitialized()) {
			getPepper().init();
		}
	}

	/**
	 * Passes the call to an instance of {@link AtomicPepperOSGiConnector},
	 * which in turn instantiates a {@link AtomicMavenAccessor}, passing
	 * itself as argument. Must be called to be able to resolve Maven
	 * dependencies for modules, etc.
	 *
	 */
	public void initMavenAccessor() {
		((AtomicPepperOSGiConnector) getPepper()).initializeMavenAccessor();
	}



}

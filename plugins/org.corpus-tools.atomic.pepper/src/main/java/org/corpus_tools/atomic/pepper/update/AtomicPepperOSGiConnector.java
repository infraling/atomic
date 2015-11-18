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

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.PepperConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector;
import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperConfigurationException;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class AtomicPepperOSGiConnector extends PepperOSGiConnector {
	
	private AtomicPepperConfiguration properties = null;
	
	@Override
	public void setConfiguration(PepperConfiguration configuration) {
		if (configuration instanceof AtomicPepperConfiguration) {
			this.properties = (AtomicPepperConfiguration) configuration;
		} else {
			throw new PepperConfigurationException("Cannot set the given configuration, since it is not of type '" + AtomicPepperConfiguration.class.getSimpleName() + "'.");
		}
	}

}

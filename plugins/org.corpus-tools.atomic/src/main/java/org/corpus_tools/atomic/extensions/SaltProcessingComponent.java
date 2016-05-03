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
package org.corpus_tools.atomic.extensions;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * This class provides handy methods and default implementations
 * for Salt-based implementations of {@link ProcessingComponent}.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltProcessingComponent {

	private String designatedLayerName = null;
	private String targetLayerId = null;

	/**
	 * Processing components create model elements (nodes,
	 * edges and annotations) in the underlying Salt model.
	 * It makes sense to "file" the output of each component
	 * in a separate layer, or add it to an existing layer.
	 * <p>
	 * In the former case, clients can provide a designated
	 * name for the layer which will contain the output
	 * of the processing component (i.e., the model elements).
	 * This default implementation simply returns the
	 * name of the respective {@link ProcessingComponent}
	 * as defined in the extension.
	 *
	 * @return the designated layer name
	 */
	public String getDesignatedLayerName(IConfigurationElement configurationElement) {
		if (designatedLayerName == null || designatedLayerName.isEmpty()) {
			return configurationElement.getAttribute("name");
		}
		else return designatedLayerName;
	}

	/**
	 * Processing components create model elements (nodes,
	 * edges and annotations) in the underlying Salt model.
	 * It makes sense to "file" the output of each component
	 * in a separate layer, or add it to an existing layer.
	 * <p>
	 * In the latter case, clients can provide the ID of the
	 * target layer  which will contain the output
	 * of the processing component (i.e., the model elements).
	 *
	 * @return
	 */
	public String getTargetLayerId() {
		return targetLayerId;
	}

	/**
	 * @param designatedLayerName the designatedLayerName to set
	 */
	public void setDesignatedLayerName(String designatedLayerName) {
		this.designatedLayerName = designatedLayerName;
	}

	/**
	 * @param targetLayerId the targetLayerId to set
	 */
	public void setTargetLayerId(String targetLayerId) {
		this.targetLayerId = targetLayerId;
	}

	
}

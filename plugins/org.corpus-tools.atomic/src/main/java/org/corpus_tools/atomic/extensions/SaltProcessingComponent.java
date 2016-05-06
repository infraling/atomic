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
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

/**
 * This class provides handy methods and default implementations
 * for Salt-based implementations of {@link ProcessingComponent}.
 * This class is meant to be used with Salt version 2.1.1.
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 * 
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class SaltProcessingComponent {

	private String targetLayerName = null;
	private SLayer targetLayer = null;

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
	public String getTargetLayerName() {
		if (targetLayerName == null || targetLayerName.isEmpty()) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint[] points = registry.getExtensionPoints();
			for (int i = 0; i < points.length; i++) {
				for (IConfigurationElement element : points[i].getConfigurationElements()) {
					if (element.getAttribute("class") != null && element.getAttribute("class").equals(this.getClass().getName())) {
						return getIdPrefix() + element.getAttribute("name");
					}
				}
			}
		}
		return getIdPrefix() + targetLayerName;
	}

	/**
	 * Returns the fragment part of the ID URI concatenated
	 * with "::" to make the name unique.
	 *
	 * @return the ID prefix (ID URI fragment + "::")
	 */
	private String getIdPrefix() {
		return getTargetLayer().getSElementPath().fragment() + "::";
	}

	/**
	 * @param targetLayerName the targetLayerName to set
	 */
	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	/**
	 * @return the targetLayer
	 */
	public SLayer getTargetLayer() {
		if (targetLayer == null) {
			setTargetLayer(SaltFactory.eINSTANCE.createSLayer());
		}
		return targetLayer;
	}

	/**
	 * @param targetLayer the targetLayer to set
	 */
	public void setTargetLayer(SLayer targetLayer) {
		this.targetLayer = targetLayer;
	}

}

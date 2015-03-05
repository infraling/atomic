/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.layerview.util;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

/**
 * @author Stephan Druskat
 *
 */
public class NewLayer {

	private SLayer newLayer;

	/**
	 * @param layer
	 */
	public NewLayer(SLayer layer) {
		this.setNewLayer(layer);
	}

	/**
	 * @return the newLayer
	 */
	public SLayer getNewLayer() {
		return newLayer;
	}

	/**
	 * @param newLayer the newLayer to set
	 */
	public void setNewLayer(SLayer newLayer) {
		this.newLayer = newLayer;
	}

}

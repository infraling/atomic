/**
 * Copyright (c) 2014 Stephan Druskat. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Stephan Druskat - initial API and implementation
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.utils;

/**
 * A spacing algorithm for hue values using a ratio which the user can choose to provide.
 * If no value is provided, the golden ratio conjugate is used as fall-back value.
 * 
 * @author Stephan Druskat
 *
 */
public class CustomSpacedHueStrategy implements ColourStrategy {

	private static float defaultH = -1;
	
	/**
	 * Fall-back ratio in case no custom ratio has been passed. Equals golden ratio conjugate.
	 */
	private double ratio = 0.618033988749895;
	
	/**
	 * Calculates a custom-spaced float value for hue to be used in an HSV to RGB calculation.
	 * 
	 * @param h The hue float that is tested against the default float and used for hue randomization. This should be a {@link Random#nextFloat()}. 
	 * @return A float that is custom-spaced to the last calculated float.
	 */
	/* (non-Javadoc)
	 * @see net.sdruskat.utils.colour.algorithms.ColourStrategy#execute(float, double)
	 */
	@Override
	public float execute(float h, double d) {
		double ratio = d;
		if (defaultH   == -1) {
			defaultH = h;
		}
		else {
			defaultH += ratio;
			float moduloRatioFloat = (defaultH % 1);
			defaultH = moduloRatioFloat;
		}
		return defaultH;

	}

	/**
	 * Executes the actual calculation based on the {@link #ratio}
	 */
	/* (non-Javadoc)
	 * @see net.sdruskat.utils.colour.algorithms.ColourStrategy#execute(float)
	 */
	@Override
	public float execute(float h) {
		double ratio = getRatio();
		if (defaultH   == -1) {
			defaultH = h;
		}
		else {
			defaultH += ratio;
			float moduloRatioFloat = (defaultH % 1);
			defaultH = moduloRatioFloat;
		}
		return defaultH;
	}

	/**
	 * @return the ratio
	 */
	public double getRatio() {
		return ratio;
	}

}

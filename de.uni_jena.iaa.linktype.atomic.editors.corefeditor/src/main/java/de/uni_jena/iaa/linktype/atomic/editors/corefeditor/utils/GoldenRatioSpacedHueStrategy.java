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
 * A spacing algorithm for hue values using the golden ration conjugate.
 * 
 * The implementation of the golden ratio-spaced hue value calculation is based on the
 * blog post <a href="http://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/">&quot;How to Generate Random Colors Programmatically&quot;</a> by <i>Martin Ankerl</i>
 * (http://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/), last accessed on 01 April 2014 (no joke!). 
 *  
 * @author Stephan Druskat
 *
 */
public class GoldenRatioSpacedHueStrategy implements ColourStrategy {
	
	/**
	 * Default float for the hue value, tested against in {@link GoldenRatioSpacedHueStrategy#execute(float)}.
	 */
	private static float goldenRatioFloatForH = -1;

	/**
	 * Calculates a golden ratio-spaced float value for hue to be used in an HSV to RGB calculation.
	 * 
	 * @param h The hue float that is tested against the default float and used for hue randomization. This should be a {@link Random#nextFloat()}. 
	 * @return A float that is golden ratio-spaced to the last calculated float.
	 */
	/* (non-Javadoc)
	 * @see net.sdruskat.utils.colour.algorithms.ColourStrategy#execute(float)
	 */
	@Override
	public float execute(float h) {
		double goldenRatioConjugate = 0.618033988749895;
		if (goldenRatioFloatForH  == -1) {
			goldenRatioFloatForH = h;
		}
		else {
			goldenRatioFloatForH += goldenRatioConjugate;
			float moduloGoldenRatioFloat = (goldenRatioFloatForH % 1);
			goldenRatioFloatForH = moduloGoldenRatioFloat;
		}
		return goldenRatioFloatForH;
	}

	/**
	 * As golden ratio has a fixed ratio = golden ration conjugate,
	 * the passed-in custom ratio is neglected and the call forwarded
	 * to the single argument method. Will never be called due to
	 * algorithm check in RandomColourGeneratorSWT.
	 * 
	 * @see RandomColourGeneratorSWT#randomizedHueSpacedHsvToRgb(float, float, String, double)
	 */
	@Override
	public float execute(float f, double d) {
		/** 
		 * This method will (and should) never be called, as
		 * RandomColourGeneratorSWT#randomizedHueSpacedHsvToRgb(float s, float v, String algorithm, double customRatio)
		 * checks for the algorithm passed in and drops the customRatio arg when
		 * executing the strategy on the context.
		 * 
		 * It has, however, below fall-back option to drop the double argument and forward
		 * to single-arg method.
		 * 
		 * @see RandomColourGeneratorSWT#randomizedHueSpacedHsvToRgb(float s, float v, String algorithm, double customRatio)
		 */
		return execute(f);
	}

}

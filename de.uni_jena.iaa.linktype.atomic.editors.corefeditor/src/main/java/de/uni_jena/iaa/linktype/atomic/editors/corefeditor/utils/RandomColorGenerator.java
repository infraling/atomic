/**
 * Copyright (c) 2014 Stephan Druskat. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Stephan Druskat - initial API and implementation
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 * This class provides methods to create (pseudo-)randomly generated {@link RGB} objects.
 * 
 * @author Stephan Druskat
 * 
 */
public class RandomColorGenerator {
	
	/**
	 * Randomized float for the hue value
	 */
	private float randomizedH;
	
	private String algorithm;
	
	/**
	 * Algorithm constant for hue value spacing from the golden ratio conjugate.
	 */
	public static final String GOLDEN_RATIO = "de.uni_jena.iaa.linktype.atomic.editors.corefeditor.utils.GoldenRatioSpacedHueStrategy";
	/**
	 * Algorithm constant for hue value spacing from a custom ratio. Default is the golden ratio conjugate unless set per CustomSpacedHueStrategy(double ratio).
	 */
	public static final String CUSTOM_RATIO = "de.uni_jena.iaa.linktype.atomic.editors.corefeditor.utils.CustomSpacedHueStrategy";

	/**
	 * No-args constructor sets {@link #randomizedH} to a new pseudo-random float.
	 */
	public RandomColorGenerator() {
		setRandomizedH(new Random().nextFloat());
	}
	
	/**
	 * Creates an {@link RGB} object from floats for hue, saturation and perceived luminance. The hue value is golden ratio-spaced from the last calculated hue value in the life cycle of the {@link RandomColorGenerator} instance. It calls {@link #calculateHue(float)} for the hue calculation.
	 * 
	 * @param s the saturation value
	 * @param v the perceived luminance value
	 * @param algorithm the algorithm which should be used for hue spacing
	 * 
	 * @return An {@link RGB} object, spaced as per algorithm, randomized on hue
	 * 
	 * @see RandomColorGenerator#GOLDEN_RATIO
	 * @see RandomColorGenerator#CUSTOM_RATIO
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public RGB randomizedHueSpacedHsvToRgb(float s, float v, String algorithm) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
		setAlgorithm(algorithm);
		float calculatedHue = -1;
		ColourStrategy instance = null;
		if (getAlgorithm().equals(GOLDEN_RATIO)) {
			/*
			 * Although explicitly getting the default no-args constructor instead of calling
			 * Class.newInstance() is more complicated - and in fact superfluous as the
			 * strategy classes don't declare constructors -, it is the preferred way to do
			 * things (cf. http://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html).
			 */
			Constructor<?>[] constructors = Class.forName(GOLDEN_RATIO).getDeclaredConstructors();
			for (int i = 0; i < constructors.length; i++) {
			    if (constructors[i].getGenericParameterTypes().length == 0)
				instance = (GoldenRatioSpacedHueStrategy) constructors[i].newInstance();
			}
			ColourContext context = new ColourContext(instance);
			calculatedHue = context.executeStrategy(getRandomizedH());
		}
		else if (getAlgorithm().equals(CUSTOM_RATIO)) {
			Constructor<?>[] constructors = Class.forName(CUSTOM_RATIO).getDeclaredConstructors();
			for (int i = 0; i < constructors.length; i++) {
			    if (constructors[i].getGenericParameterTypes().length == 0)
				instance = (CustomSpacedHueStrategy) constructors[i].newInstance();
			}
			ColourContext context = new ColourContext(instance);
			calculatedHue = context.executeStrategy(getRandomizedH());
		}
		return hsvToRgb(calculatedHue, s, v);
	}
	
	/**
	 * Uses the passed-in algorithm strategy to get a randomized hue value, and
	 * passes the complete set of HSV values for conversion to RGB to {@link #hsvToRgb(float, float, float)}. 
	 * 
	 * @param s the saturation value
	 * @param v the perceived luminance value
	 * @param algorithm the algorithm to use for the spacing of the random hue value
	 * @param customRatio a custom spacing ratio for the randomized hue values
	 * 
	 * @return the {@link RGB} object returned from {@link #hsvToRgb(float, float, float)} for the values
	 * created in this method
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public RGB randomizedHueSpacedHsvToRgb(float s, float v, String algorithm, double customRatio) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		setAlgorithm(algorithm);
		ColourContext context = null;
		float calculatedHue = -1;
		if (getAlgorithm().equals(GOLDEN_RATIO)) {
			context = new ColourContext((ColourStrategy) Class.forName(getAlgorithm()).newInstance());
			calculatedHue = context.executeStrategy(getRandomizedH());
		}
		else if (getAlgorithm().equals(CUSTOM_RATIO)) {
			context = new ColourContext((ColourStrategy) Class.forName(getAlgorithm()).newInstance());
			calculatedHue = context.executeStrategy(getRandomizedH(), customRatio);
		}
		return hsvToRgb(calculatedHue, s, v);
	}


	/**
	 * Creates an {@link RGB} object, simply randomized by passing Random().nextInt(256) to its constructor thrice.
	 * @return An {@link RGB} object based on simply (pseudo-) randomized ints.
	 */
	public RGB simpleRandomRgb() {
		return new RGB(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
	}

	/**
	 * Converts an <a href="http://en.wikipedia.org/wiki/HSL_and_HSV">HSV</a> value set to the visually closest {@link RGB} value set.
	 * 
	 * @param h A float representing hue
	 * @param s A float representing saturation
	 * @param v A float representing perceived luminance
	 * @return An {@link RGB} object which is visually as close as possible to the passed-in HSV values.
	 */
	public RGB hsvToRgb(float h, float s, float v) {
		int h_i = (int)(h * 6);
		float f = (h * 6) - h_i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);
		float r = 0,g = 0,b = 0;
		switch (h_i) {
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		case 5:
			r = v;
			g = p;
			b = q;
			break;

		default:
			break;
		}
		// Uses simple casting, could use rounding (Math.round()) as well before casting.
		return new RGB((int)(r * 256), (int)(g * 256), (int)(b * 256));
	}
	
	/**
	 * Creates a list of n ratio-spaced random {@link RGB} objects.
	 * 
	 * 
	 * @param numberOfRgbObjects the number of {@link RGB} objects to return
	 * @param saturation the saturation value
	 * @param value the perceived luminance value
	 * @param algorithm the algorithm to use for hue spacing
	 * 
	 * @return a {@link List} of {@link RGB} values
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public List<RGB> createRandomizedRgbList(int numberOfRgbObjects, float saturation, float value, String algorithm) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
		List<RGB> randomGoldenRatioRgbList = new ArrayList<RGB>();
		for (int i = 0; i < numberOfRgbObjects; i++) {
			randomGoldenRatioRgbList.add(randomizedHueSpacedHsvToRgb(saturation, value, algorithm));
		}
		return randomGoldenRatioRgbList;
	}

	/**
	 * @return the randomizedH
	 */
	public float getRandomizedH() {
		return randomizedH;
	}

	/**
	 * @param randomizedH the randomizedH to set
	 */
	public void setRandomizedH(float randomizedH) {
		this.randomizedH = randomizedH;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}


}

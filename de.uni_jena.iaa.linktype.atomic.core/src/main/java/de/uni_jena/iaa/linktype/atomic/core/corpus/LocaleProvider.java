/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A static provider util for available locales. 
 * Provides, e.g., getting a Locale by its displayName.
 * 
 * @author Stephan Druskat
 *
 */
public class LocaleProvider {
	
	final static Map<String, Locale> localeNameMap = new HashMap<String, Locale>();
	static {
	    for (Locale l : Locale.getAvailableLocales()) {
	        localeNameMap.put(l.getDisplayName(), l);
	    }
	}

	public static Locale getLocale(String displayName) {
	    return localeNameMap.get(displayName);
	}

	/**
	 * @return the localenamemap
	 */
	public static final Map<String, Locale> getLocalenamemap() {
		return localeNameMap;
	}
	
	public static ArrayList<String> getLocaleNames() {
		ArrayList<String> nameList = new ArrayList<String>(getLocalenamemap().keySet());
		Collections.sort(nameList, new Comparator<String>() {
	        @Override
	        public int compare(String s1, String s2) {
	            return s1.compareToIgnoreCase(s2);
	        }
	    });
		return nameList;
	}

}

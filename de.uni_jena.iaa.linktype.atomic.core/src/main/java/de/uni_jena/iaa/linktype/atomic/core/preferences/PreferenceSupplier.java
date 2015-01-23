/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import java.util.HashMap; 
import java.util.Map;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * This class is the central definition point for application preferences.
 * Preferences are added by supplying a field to take in the preference,
 * and a default value. It also provides these fields via a HashMap to the
 * PreferenceInitializer and provides access to the preferences node to other
 * plugins.
 * 
 * Cf. https://openchrom.wordpress.com/2014/01/11/how-to-handle-preferences-consistently/
 * 
 * @author Stephan Druskat
 *
 */
public class PreferenceSupplier {
	
	public static final IScopeContext SCOPE_CONTEXT = ConfigurationScope.INSTANCE;
    public static final String PREFERENCE_NODE = "de.uni_jena.iaa.linktype.atomic.core";
	
	/* DEF = DEFAULT */
	
	// Reserved key for annotating element type (STYPE)
	public static final String STYPE = "sType";
	public static final String STYPE_DEF = "t";

	/** Provides a HashMap of all (preference,preference default) pairs for
	 * initialization by the PreferenceInitializer.
	 * 
	 * @return Map<String, String> entries A HashMap containing all (preference,preference default) pairs.
	 */
	public static Map<String, String> getInitializationEntries() {
		Map<String, String> entries = new HashMap<String, String>();

        entries.put(STYPE, STYPE_DEF);

        return entries;
	}
	
	/**
	 * Provides access to the preference pairs from other places / plugins.
	 * 
	 * @return IEclipsePreferences The preferences root node. 
	 */
	public static IEclipsePreferences getPreferences() {
	    return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
    }

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * @author Stephan Druskat
 *
 */
public class PreferenceSupplier {
	
	public static final IScopeContext SCOPE_CONTEXT = InstanceScope.INSTANCE;
    public static final String PREFERENCE_NODE = "de.uni_jena.iaa.linktype.atomic.core";
	
	/* DEF -> DEFAULT */
	
	// Reserved word for annotating element type (STYPE)
	public static final String STYPE = "string";
	public static final String STYPE_DEF = "t";

	public static Map<String, String> getInitializationEntries() {
		Map<String, String> entries = new HashMap<String, String>();

        entries.put(STYPE, STYPE_DEF);

        return entries;
	}
	
	public static IEclipsePreferences getPreferences() {
	    return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
    }

}

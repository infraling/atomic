/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.preferences;

import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.uni_jena.iaa.linktype.atomic.core.Activator;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicPreferencesInitializer extends AbstractPreferenceInitializer {
	
	/**
	 * 
	 */
	public AtomicPreferencesInitializer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		Map<String, String> initializationEntries = PreferenceProvider.getInitializationEntries();
        for(Map.Entry<String, String> entry : initializationEntries.entrySet()) {
            store.setDefault(entry.getKey(), entry.getValue());
        }
	}

}

/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephan Druskat
 *
 */
public class AccessorUtil {
	
	private static final Logger log = LoggerFactory.getLogger(AccessorUtil.class);

	/**
	 * @param string
	 * @return
	 */
	public static String getReservedKeysProperty(String key) {
		try {
			return ((FileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput()).getFile().getProject().getPersistentProperty(new QualifiedName("de.uni_jena.iaa.linktype.atomic.core.reservedKeysPreferencePage", key));
		} catch (CoreException e) {
			log.error("Core exception!", e);
			e.printStackTrace();
			return null;
		}
	}

}

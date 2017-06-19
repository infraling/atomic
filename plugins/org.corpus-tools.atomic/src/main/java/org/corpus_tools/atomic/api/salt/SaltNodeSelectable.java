package org.corpus_tools.atomic.api.salt;

import java.util.List;

/**
 * Interface for all components where an external party can select a number of salt nodes by their name.
 * @author Thomas Krause <krauseto@hu-berlin.de>
 *
 */
public interface SaltNodeSelectable {

	/**
	 * 
	 * @param nodeIDs
	 */
	public void setSelection(List<String> nodeIDs);
}

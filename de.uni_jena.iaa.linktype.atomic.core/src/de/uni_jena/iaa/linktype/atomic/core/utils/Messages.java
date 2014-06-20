package de.uni_jena.iaa.linktype.atomic.core.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.uni_jena.iaa.linktype.atomic.core.messages"; //$NON-NLS-1$
	public static String AtomicProjectBasicsWizardPage_CORPUS_TEXTFIELD_DEFAULT;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

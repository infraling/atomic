package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_jena.iaa.linktype.atomic.editors.grapheditor"; //$NON-NLS-1$
	public static final String iconsPath = "src/main/resources/icons/";

	public static final String SPANNINGRELATION_ICON = "SPANNINGRELATION_ICON";
	public static final String DOMINANCERELATION_ICON = "DOMINANCERELATION_ICON";
	public static final String POINTINGRELATION_ICON = "POINTINGRELATION_ICON";
	public static final String ORDERRELATION_ICON = "ORDERRELATION_ICON";
	public static final String STRUCTURE_ICON = "STRUCTURE_ICON";
	public static final String SPAN_ICON = "SPAN_ICON";

	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        ImageDescriptor structure = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "structure.gif"), null));
        ImageDescriptor span = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "span.gif"), null));
        ImageDescriptor domRel = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "dominancerelation.gif"), null));
        ImageDescriptor spanRel = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "spanningrelation.gif"), null));
        ImageDescriptor pointRel = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "pointingrelation.gif"), null));
        ImageDescriptor orderRel = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(iconsPath + "orderrelation.gif"), null));
        registry.put(STRUCTURE_ICON, structure);
        registry.put(SPAN_ICON, span);
        registry.put(DOMINANCERELATION_ICON, domRel);
        registry.put(SPANNINGRELATION_ICON, spanRel);
        registry.put(POINTINGRELATION_ICON, pointRel);
        registry.put(ORDERRELATION_ICON, orderRel);

    }

}

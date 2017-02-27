/**
 * 
 */
package org.corpus_tools.atomic.api.events;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class PartContextListener implements IPartListener2 {
	
	private final String partId;
	private IContextActivation contextActivation = null;
	private String contextId = null;

	/**
	 * @param partId 
	 * 
	 */
	public PartContextListener(String partId, String pluginId) {
		this.partId = partId;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.contexts");
		
		for (IConfigurationElement element : elements) {
			if (element.getContributor().getName().equals(pluginId)) {
				this.contextId = element.getAttribute("id");
				break; // Allow only one context per plugin
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	/**
	 * If {@link #contextId} is set and {@link #partId} equals the
	 * part's ID that has been **activated**, activate the
	 * respective context and set {@link #contextActivation} 
	 * to the activation method's return object, as we will
	 * need this later to deactivate context when the part is
	 * deactivated. 
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		if (contextId != null && partRef.getId().equals(partId)) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					contextActivation = PlatformUI.getWorkbench().getService(IContextService.class).activateContext(contextId);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	/**
	 * If both {@link #contextId} and {@link #contextActivation}
	 * are set, and {@link #partId} equals the part's ID that 
	 * has been **deactivated**, deactivate the respective
	 * context.
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		if (contextId != null && contextActivation != null && partRef.getId().equals(partId)) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					PlatformUI.getWorkbench().getService(IContextService.class).deactivateContext(contextActivation);
				}
			});
		}
	}

	// Unimplemented methods
	@Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}
	@Override public void partClosed(IWorkbenchPartReference partRef) {}
	@Override public void partOpened(IWorkbenchPartReference partRef) {}
	@Override public void partHidden(IWorkbenchPartReference partRef) {}
	@Override public void partVisible(IWorkbenchPartReference partRef) {}
	@Override public void partInputChanged(IWorkbenchPartReference partRef) {}

}

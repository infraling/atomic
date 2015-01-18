/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.update;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.core.helpers.LogHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicAutoUpdateJob extends Job {
	
	private static final Logger log = LoggerFactory.getLogger(AtomicAutoUpdateJob.class);

	private IProvisioningAgent agent;
	private IPreferenceStore prefStore;
	private String JUSTUPDATED;

	public AtomicAutoUpdateJob(IProvisioningAgent agent, IPreferenceStore prefStore, String justupdated) {
		super("Searching for updates");
		this.agent = agent;
		this.prefStore = prefStore;
		this.JUSTUPDATED = justupdated;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		InetAddress atomicUpdateSite = null;
		try {
			atomicUpdateSite = InetAddress.getByName("linktype.iaa.uni-jena.de");
			log.info("Can connect to " + atomicUpdateSite.getHostName() + ".");

			IStatus updateStatus = P2Updater.checkForUpdates(agent, monitor);
			if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
				log.info("No updates were found.");
			} else if (updateStatus.getSeverity() != IStatus.ERROR) {
				prefStore.setValue(JUSTUPDATED, true);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						boolean restart = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Restart Atomic?", "The latest Atomic updates have been installed. In order to activate them, it is necessary to restart the application.\nRestart now?");
						if (restart) {
							PlatformUI.getWorkbench().restart();
						}
					}
				});
			} else {
				LogHelper.log(updateStatus);
			}
		} catch (UnknownHostException e) {
			log.warn("No internet connection, aborting update.");
		}
		return Status.OK_STATUS;
	}

}

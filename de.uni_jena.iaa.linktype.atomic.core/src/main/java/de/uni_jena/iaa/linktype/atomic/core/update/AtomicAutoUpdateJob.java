/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.update;

import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicAutoUpdateJob extends Job {

	private IProvisioningAgent agent;
	private IPreferenceStore prefStore;
	private String JUSTUPDATED;
	private IStatus result;

	public AtomicAutoUpdateJob(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public AtomicAutoUpdateJob(IProvisioningAgent agent, IPreferenceStore prefStore, String justupdated) {
		super("Atomic Auto-Update");
		this.agent = agent;
		this.prefStore = prefStore;
		this.JUSTUPDATED = justupdated;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		InetAddress atomicUpdateSite = null;
		try {
			atomicUpdateSite = InetAddress.getByName("linktype.iaa.uni-jena.de");
			System.out.println("Can connect to " + atomicUpdateSite.getHostName() + ".");

			// #####################################
			// Eigentlicher Job
			
//			final IRunnableWithProgress runnable = new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					IStatus updateStatus = P2Updater.checkForUpdates(agent, monitor);
					if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openInformation(null, "Updates", "No updates were found");
							}
						});
					} else if (updateStatus.getSeverity() != IStatus.ERROR) {
						prefStore.setValue(JUSTUPDATED, true);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								PlatformUI.getWorkbench().restart();
							}
						});
					} else {
						LogHelper.log(updateStatus);
					}
//				}
//			};
//			Display.getDefault().asyncExec(new Runnable() {
//
//				@Override
//				public void run() {
//					try {
//						new ProgressMonitorDialog(null).run(true, true, runnable);
//					} catch (InvocationTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			});

			// #####################################

		} catch (UnknownHostException e) {
			// FIXME: No internet connection, react!
			System.err.println("No internet connection, aborting update.");
		}
		return Status.OK_STATUS;
	}

}

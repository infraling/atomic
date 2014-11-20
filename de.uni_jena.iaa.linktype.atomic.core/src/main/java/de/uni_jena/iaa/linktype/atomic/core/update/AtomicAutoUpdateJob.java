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

/**
 * @author Stephan Druskat
 *
 */
public class AtomicAutoUpdateJob extends Job {

	public AtomicAutoUpdateJob(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
	    InetAddress atomicUpdateSite = null;
		try {
			atomicUpdateSite = InetAddress.getByName("linktype.iaa.uni-jena.de");
			System.out.println("Can connect to " + atomicUpdateSite.getHostName() + ".");
		} catch (UnknownHostException e) {
			// FIXME: No internet connection, react!
			System.err.println("No internet connection, aborting update.");
		}
	    return Status.OK_STATUS;
	}

}

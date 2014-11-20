/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.update;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

/**
 * @author Stephan Druskat
 *
 */
public class P2UpdateUtil {
	
	public static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor) throws OperationCanceledException {
		ProvisioningSession session = new ProvisioningSession(agent);
		// the default update operation looks for updates to the currently
		// running profile, using the default profile root marker. To change
		// which installable units are being updated, use the more detailed
		// constructors.
		UpdateOperation operation = new UpdateOperation(session);
		SubMonitor sub = SubMonitor.convert(monitor,
				"Checking for application updates...", 200);
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			return status;
		}
		if (status.getSeverity() == IStatus.CANCEL)
			throw new OperationCanceledException();
		
		if (status.getSeverity() != IStatus.ERROR) {
			// More complex status handling might include showing the user what updates
			// are available if there are multiples, differentiating patches vs. updates, etc.
			// In this example, we simply update as suggested by the operation.
			ProvisioningJob job = operation.getProvisioningJob(null);
			status = job.runModal(sub.newChild(100));
			if (status.getSeverity() == IStatus.CANCEL)
				throw new OperationCanceledException();
		}
		return status;
	}

}

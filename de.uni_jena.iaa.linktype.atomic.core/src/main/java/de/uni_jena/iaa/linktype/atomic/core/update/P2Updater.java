/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.update;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

/**
 * @author Stephan Druskat
 * 
 */
public class P2Updater {
	
	private static final Logger log = LogManager.getLogger(P2Updater.class);

	static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor) throws OperationCanceledException {
		try {
			addUpdateSite(agent);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			return status;
		}
		if (status.getSeverity() == IStatus.CANCEL)
			throw new OperationCanceledException();

		if (status.getSeverity() != IStatus.ERROR) {
			ProvisioningJob job = operation.getProvisioningJob(null);
			status = job.runModal(sub.newChild(100));
			if (status.getSeverity() == IStatus.CANCEL)
				throw new OperationCanceledException();
		}
		return status;
	}

	private static void addUpdateSite(IProvisioningAgent provisioningAgent) throws InvocationTargetException {
		IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (metadataManager == null) {
			Throwable throwable = new Throwable("Could not load Metadata Repository Manager");
			throwable.fillInStackTrace();
			log.error("Metadata manager was null", throwable);
			throw new InvocationTargetException(throwable);
		}
		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (artifactManager == null) {
			Throwable throwable = new Throwable("Could not load Artifact Repository Manager");
			throwable.fillInStackTrace();
			log.error("Artifact manager was null", throwable);
			throw new InvocationTargetException(throwable);
		}
		try {
			URI repoLocation = new URI("https://dl.bintray.com/corpus-tools/atomic/");
			log.info("Adding repository " + repoLocation);
			metadataManager.loadRepository(repoLocation, null);
			artifactManager.loadRepository(repoLocation, null);
		} catch (ProvisionException pe) {
			log.error("Caught provisioning exception", pe.getMessage());
			throw new InvocationTargetException(pe);
		} catch (URISyntaxException e) {
			log.error("Caught URI syntax exception", e.getMessage());
			throw new InvocationTargetException(e);
		}
	}
}

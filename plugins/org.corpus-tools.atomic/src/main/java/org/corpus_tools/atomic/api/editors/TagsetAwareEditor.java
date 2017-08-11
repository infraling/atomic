/**
 * 
 */
package org.corpus_tools.atomic.api.editors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.impl.TagsetFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface TagsetAwareEditor extends IEditorPart {

	static final Logger log = LogManager.getLogger(TagsetAwareEditor.class);

	default public Tagset loadTagset(IEditorInput input) {
		Tagset tagset = null;
		IProject iProject = ((FileEditorInput) input).getFile().getProject();

		IFile tagsetFileResource = null;
		File tagsetFile = null;
		try {
			for (IResource member : iProject.members()) {
				if (member instanceof IFile && member.getFileExtension().equals("ats")) {
					tagsetFileResource = (IFile) member;
				}
			}
		}
		catch (CoreException e) {
			log.error("An error occurred getting the tagset file.", e);
		}
		if (tagsetFileResource != null) {
			IPath location = tagsetFileResource.getLocation();
			if (location != null)
				tagsetFile = location.toFile();
		}
		else {
			log.info("There is no tagset file in project \"{}\".", iProject.getName());
			return null;
		}
		if (tagsetFile != null) {
			String tagsetFilePath = tagsetFile.getAbsolutePath();
			long tagsetFileSize = 0;
			try {
				tagsetFileSize = Files.size(Paths.get(tagsetFile.getAbsolutePath()));
				log.trace("Tagset file {} has size {}.", tagsetFilePath, String.valueOf(tagsetFileSize));
			}
			catch (IOException e) {
				log.warn("Failed to load tagset file {} in order to calculate its size.", tagsetFilePath, e);
				return null;
			}
			if (tagsetFileSize != 0) {
				tagset = TagsetFactory.load(URI.createFileURI(tagsetFilePath));
			}
			else {
				log.info("Tagset file \"{}\" is empty.", tagsetFile.getName());
				return null;
			}
			if (tagset == null) {
				log.error("Could not read tagset from tagset file {}.", tagsetFilePath);
				return null;
			}
			log.info("Loaded tagset {} ({}) from {}.", tagset, tagset.getName(), tagsetFilePath);
			return tagset;
		}
		else {
			log.info("There is no actual file backing the IResource \"{}\".", tagsetFileResource);
			return null;
		}
	}

}

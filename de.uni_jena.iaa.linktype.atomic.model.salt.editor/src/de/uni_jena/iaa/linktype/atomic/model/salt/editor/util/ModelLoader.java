/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class ModelLoader {

	private static SaltProject saltProject;

	/**
	 * Loads the SDocumentGraph from the SaltProject.salt file
	 * that's been chosen for IEditorInput by the user.
	 * @param input
	 */
	public static SDocumentGraph loadSDocumentGraph(IFile iFile2) {
		saltProject = SaltFactory.eINSTANCE.createSaltProject();
		SDocumentGraph inputToLoad = null;
		
		File file = new File(iFile2.getLocation().toString());
		URI uri = URI.createFileURI(file.getParentFile().getAbsolutePath());
		saltProject.loadSaltProject(uri);
		inputToLoad = saltProject.getSCorpusGraphs().get(0).getSDocuments().get(0).getSDocumentGraph();
		
		return inputToLoad;
	}

	public static IFile getIFileFromInput(IEditorInput input) {
		IFile iFile = null;
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) input;
			iFile = fileEditorInput.getFile();
		}
		return iFile;
	}

	public static SaltProject getSaltProject() {
		return saltProject;
	}

}

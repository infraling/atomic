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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SStructureDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class SStructureComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		SStructureDeleteCommand command = new SStructureDeleteCommand();
		command.setSStructure((SStructure) getHost().getModel());
		return command;
	}

}

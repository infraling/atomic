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
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SDominanceRelationCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SPointingRelationCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SSpanningRelationCreateCommand;

/**
 * @author Stephan Druskat
 *
 */
public class STokenGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	@Override 
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		Command result = null;
		if (request.getStartCommand() instanceof SPointingRelationCreateCommand) {
			SPointingRelationCreateCommand sPointingRelationResult = (SPointingRelationCreateCommand) request.getStartCommand();
		    sPointingRelationResult.setTarget((SToken) getHost().getModel());
		    result = sPointingRelationResult;
		}
		else if (request.getStartCommand() instanceof SDominanceRelationCreateCommand) {
			SDominanceRelationCreateCommand sDominanceRelationResult = (SDominanceRelationCreateCommand) request.getStartCommand();
		    sDominanceRelationResult.setTarget((SToken) getHost().getModel());
		    result = sDominanceRelationResult;
		}
		else if (request.getStartCommand() instanceof SSpanningRelationCreateCommand) {
			SSpanningRelationCreateCommand sSpanningRelationResult = (SSpanningRelationCreateCommand) request.getStartCommand();
		    sSpanningRelationResult.setTarget((SToken) getHost().getModel());
		    result = sSpanningRelationResult;
		}
	    return result;
	}
	 
	@Override 
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Command result = null;
		if (request.getNewObject() instanceof SPointingRelation) {
			SPointingRelationCreateCommand sPointingRelationResult = new SPointingRelationCreateCommand();
		    sPointingRelationResult.setSource((SToken) getHost().getModel());
		    sPointingRelationResult.setSPointingRelation((SPointingRelation) request.getNewObject());
		    sPointingRelationResult.setGraph(((SToken) getHost().getModel()).getSDocumentGraph());
		    request.setStartCommand(sPointingRelationResult);
		    result = sPointingRelationResult;
		}
		else if (request.getNewObject() instanceof SDominanceRelation) {
			 // An instance of SToken cannot be the source of an SDominanceRelation!
		}
	    return result;
	}
	 
	@Override 
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}
	 
	@Override 
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}}

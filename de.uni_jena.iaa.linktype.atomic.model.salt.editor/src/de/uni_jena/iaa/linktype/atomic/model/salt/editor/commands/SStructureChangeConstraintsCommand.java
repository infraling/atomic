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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;

/**
 * @author Stephan Druskat
 *
 */
public class SStructureChangeConstraintsCommand extends Command {
	
	private Rectangle oldConstraints;
	private Rectangle newConstraints;
	private SStructure model;
	
	@Override 
	public void execute() {
		if(oldConstraints == null) {
			oldConstraints = getConstraints();
		}
		setConstraints(newConstraints);
		model.eNotify(new NotificationImpl(Notification.SET, null, "SET_CONSTRAINTS"));
	}
			 
	private void setConstraints(Rectangle newConstraints) {
		model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_X").setSValue(Integer.toString(newConstraints.x));
		model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_Y").setSValue(Integer.toString(newConstraints.y));
	}

	private Rectangle getConstraints() {
		int x = Integer.parseInt((String) model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_X").getSValue());
		int y = Integer.parseInt((String) model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_Y").getSValue());
		return new Rectangle(x, y, newConstraints.width, newConstraints.height);
	}

	@Override 
	public void undo() {
		setConstraints(oldConstraints);
	}
			 
	public void setModel(SStructure model) {
		this.model = model;
	}
			   
	public void setNewConstraints(Rectangle constraint) {
		this.newConstraints = constraint;
	}

}

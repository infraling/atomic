/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class StructuredNodeChangeConstraintsCommand extends Command {

	private AbstractGraphicalEditPart editPart;
	private Rectangle newConstraint, oldConstraint;
	private SStructuredNode model;

	@Override 
	public void execute() {
		SProcessingAnnotation anno = getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS");
		if (anno != null) {
			int[] xy = (int[]) anno.getValue();
			setOldConstraint(new Rectangle(xy[0], xy[1], -1, -1));
			int versionInt = ((int[]) anno.getValue())[2];
			anno.setValue(new int[]{PartUtils.getRelativeX((SDocumentGraph) getModel().getSGraph(), getModel(), getNewConstraint().x), getNewConstraint().y, versionInt++});
			getModel().eNotify(new NotificationImpl(Notification.SET, anno, anno));
		}
		else {
			getModel().createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[]{PartUtils.getRelativeX((SDocumentGraph) getModel().getSGraph(), getModel(), getNewConstraint().x),  getNewConstraint().y, 1}, SDATATYPE.SOBJECT);
		}
	}
	
//	@Override
//	public void undo() {
//		SProcessingAnnotation anno = getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS");
//		if (anno != null) {
//			int versionInt = ((int[]) anno.getValue())[2];
//			if (versionInt == 1) {
//				Rectangle calculatedConstraint = PartUtils.calculateStructuredNodeLayout(getEditPart(), getModel(), (Figure) getEditPart().getFigure());
//				anno.setValue(new int[]{calculatedConstraint.x, calculatedConstraint.y, 0});
//			}
//			else {
//				anno.setValue(new int[]{getOldConstraint().x, getOldConstraint().y, versionInt--});
//			}
//			getModel().eNotify(new NotificationImpl(Notification.SET, anno, anno));
//		}
//	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setNewConstraint(Rectangle constraint) {
		this.newConstraint = constraint;
	}

	public void setOldConstraint(Rectangle bounds) {
		this.oldConstraint = bounds;
	}

	/**
	 * @return the model
	 */
	public SStructuredNode getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(SStructuredNode model) {
		this.model = model;
	}

	/**
	 * @return the newConstraint
	 */
	public Rectangle getNewConstraint() {
		return newConstraint;
	}

	/**
	 * @return the oldConstraint
	 */
	public Rectangle getOldConstraint() {
		return oldConstraint;
	}

	/**
	 * @return the editPart
	 */
	public AbstractGraphicalEditPart getEditPart() {
		return editPart;
	}

	/**
	 * @param editPart the editPart to set
	 */
	public void setEditPart(AbstractGraphicalEditPart editPart) {
		this.editPart = editPart;
	}

}

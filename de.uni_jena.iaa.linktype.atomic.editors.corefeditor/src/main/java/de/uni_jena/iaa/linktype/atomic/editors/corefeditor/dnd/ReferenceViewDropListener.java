/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.MyTreeNode;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceViewDropListener extends ViewerDropAdapter {

	private final Viewer viewer;

	  public ReferenceViewDropListener(Viewer viewer) {
	    super(viewer);
	    this.viewer = viewer;
	  }

	  @Override
	  public void drop(DropTargetEvent event) {
	    int location = this.determineLocation(event);
	    Object dtarget = determineTarget(event);
	    MyTreeNode target = null;
	    if (dtarget instanceof MyTreeNode) {
	    	target = (MyTreeNode) dtarget;
	    }
//	    String target = (String) determineTarget(event);
	    String translatedLocation ="";
	    switch (location){
	    case 1 :
	      translatedLocation = "Dropped before the target ";
	      break;
	    case 2 :
	      translatedLocation = "Dropped after the target ";
	      break;
	    case 3 :
	      translatedLocation = "Dropped on the target ";
	      break;
	    case 4 :
	      translatedLocation = "Dropped into nothing ";
	      break;
	    }
	    System.out.println(translatedLocation);
	    System.out.println("The drop was done on the element: " + ((MyTreeNode) target).getText());
	    super.drop(event);
	  }

	  // This method performs the actual drop
	  // We simply add the String we receive to the model and trigger a refresh of the 
	  // viewer by calling its setInput method.
	  @Override
	  public boolean performDrop(Object data) {
		  MyTreeNode input = (MyTreeNode) viewer.getInput();
		  System.err.println(input);
		  System.err.println(data);
	    input.setText((String) data);;
	    viewer.setInput(input);
	    return false;
	  }

	  @Override
	  public boolean validateDrop(Object target, int operation,
	      TransferData transferType) {
	    return true;
	    
	  }

}

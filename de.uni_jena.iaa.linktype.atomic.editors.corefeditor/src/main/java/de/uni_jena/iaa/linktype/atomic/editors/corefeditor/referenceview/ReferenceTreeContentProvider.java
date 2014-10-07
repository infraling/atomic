/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceTreeContentProvider implements ITreeContentProvider {
	
	int stop = 0;
	
	@Override
	  public void dispose() {
	  }

	  @Override
	  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		  System.err.println("INPUT CHANGED! " + oldInput + " > " + newInput);
	  }

	  @Override
	  public Object[] getElements(Object inputElement) {
	    createChildNodes(inputElement);
	    return getChildren(inputElement);
	  }

	  @Override
	  public Object[] getChildren(Object parentElement) {
	    if (parentElement instanceof MyTreeNode) {
	      MyTreeNode parentNode = (MyTreeNode) parentElement;
	      return parentNode.getChildren();
	    }
	    return null;
	  }

	  @Override
	  public Object getParent(Object element) {
	    if (element instanceof MyTreeNode) {
	      MyTreeNode node = (MyTreeNode) element;
	      return node.getParent();
	    }
	    return null;
	  }

	  @Override
	  public boolean hasChildren(Object element) {
	    if (element instanceof MyTreeNode) {
	      MyTreeNode node = (MyTreeNode) element;
	      if (node.getChildren().length == 0) {
	        createChildNodes(node);
	      }
	      return true;
	    }
	    return false;
	  }

	  public void createChildNodes(Object parent) {
		  if (stop < 8) {
	    if (parent instanceof MyTreeNode) {
	      MyTreeNode node = (MyTreeNode)parent;
	      node.addChild(new MyTreeNode(node.getDepth() + 1));
	      node.addChild(new MyTreeNode(node.getDepth() + 1));
	    }
		  }
		  stop++;
	  }

//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
//	 */
//	@Override
//	public void dispose() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
//	 */
//	@Override
//	public Object[] getElements(Object inputElement) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
//	 */
//	@Override
//	public Object[] getChildren(Object parentElement) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
//	 */
//	@Override
//	public Object getParent(Object element) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
//	 */
//	@Override
//	public boolean hasChildren(Object element) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}

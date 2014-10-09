/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceTreeContentProvider implements ITreeContentProvider {

	int stop = 0;
	private ReferenceModel model;

	public ReferenceTreeContentProvider(ReferenceModel referenceModel) {
		this.setModel(referenceModel);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ReferenceModel) {
			return ((ReferenceModel) inputElement).getReferences().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Reference) {
			for (SSpan o : ((Reference) parentElement).getSpanMap().values()) {
			}
			return ((Reference) parentElement).getSpanMap().values().toArray();
		}
		else if (parentElement instanceof SSpan) {
			for (Edge edge : model.getDecoratedSDocumentGraph().getInEdges(((SSpan) parentElement).getSId())) {
				if (edge instanceof SPointingRelation) {
					SAnnotation anno = ((SPointingRelation) edge).getSAnnotation("ATOMIC::coref");
					if (anno != null) {
						return new SAnnotation[]{anno};
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SSpan) {
			for (Reference ref : getModel().getReferences()) {
				if (ref.getSpans().contains(element)) {
					return ref;
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SSpan) {
			for (Edge edge : model.getDecoratedSDocumentGraph().getInEdges(((SSpan) element).getSId())) {
				if (edge instanceof SPointingRelation) {
					SAnnotation anno = ((SPointingRelation) edge).getSAnnotation("ATOMIC::coref");
					if (anno != null) {
						return true;
					}
				}
			}
		}
		else if (element instanceof Reference) {
			return ((Reference) element).getSpans().size() > 0;
		}
		return false;
	}

	/**
	 * @return the model
	 */
	public ReferenceModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(ReferenceModel model) {
		this.model = model;
	}

	// /* (non-Javadoc)
	// * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	// */
	// @Override
	// public void dispose() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	// java.lang.Object, java.lang.Object)
	// */
	// @Override
	// public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	// */
	// @Override
	// public Object[] getElements(Object inputElement) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	// */
	// @Override
	// public Object[] getChildren(Object parentElement) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	// */
	// @Override
	// public Object getParent(Object element) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	// */
	// @Override
	// public boolean hasChildren(Object element) {
	// // TODO Auto-generated method stub
	// return false;
	// }

}

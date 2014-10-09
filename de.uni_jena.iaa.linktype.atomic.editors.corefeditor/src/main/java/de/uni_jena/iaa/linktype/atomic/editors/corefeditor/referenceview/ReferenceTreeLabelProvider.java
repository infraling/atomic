/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceTreeLabelProvider extends StyledCellLabelProvider
		implements ILabelProvider {

	public void update(ViewerCell cell) {
		Object obj = cell.getElement();

		StyledString styledString = null;
		if (obj instanceof Reference) {
			styledString = new StyledString(((Reference) obj).getName());
			styledString.append(" (" + ((Reference) obj).getSpans().size()
					+ ")", StyledString.COUNTER_STYLER);
		}
		else if (obj instanceof SSpan) {
			EList<STYPE_NAME> reList = new BasicEList<STYPE_NAME>();
			reList.add(STYPE_NAME.SSPANNING_RELATION);
			EList<SToken> tokens = ((SSpan) obj).getSDocumentGraph().getOverlappedSTokens(((SSpan) obj), reList);
			EList<SToken> sortedTokens = ((SSpan) obj).getSDocumentGraph().getSortedSTokenByText(tokens);
			reList.clear();
			reList.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
			StringBuilder text = new StringBuilder();
			for (SToken token : sortedTokens) {
				STextualDS ds = token.getSDocumentGraph().getSTextualDSs().get(0);
				SDataSourceSequence sequence = token.getSDocumentGraph().getOverlappedDSSequences(token, reList).get(0);
				text.append(ds.getSText().substring(sequence.getSStart(), sequence.getSEnd()) + " ");
			}
			styledString = new StyledString(text.toString());
		}
		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
		cell.setImage(getImage(obj));
		super.update(cell);
//		getViewer().refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Reference) {
			return ((Reference) element).getName();
		}
		else if (element instanceof SSpan) {
			EList<STYPE_NAME> reList = new BasicEList<STYPE_NAME>();
			reList.add(STYPE_NAME.SSPANNING_RELATION);
			EList<SToken> tokens = ((SSpan) element).getSDocumentGraph().getOverlappedSTokens(((SSpan) element), reList);
			reList.clear();
			reList.add(STYPE_NAME.STEXTUAL_RELATION);
			StringBuilder text = new StringBuilder();
			for (SToken token : tokens) {
				STextualDS ds = token.getSDocumentGraph().getSTextualDSs().get(0);
				SDataSourceSequence sequence = token.getSDocumentGraph().getOverlappedDSSequences(token, reList).get(0);
				text.append(ds.getSText().substring(sequence.getSStart(), sequence.getSEnd()) + " ");
			}
			return text.toString();
		}
		return null;
	}

}

/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset;

import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetContentProvider implements IStructuredContentProvider {
	
	private Tagset tagset = null;
	
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Tagset) {
			tagset = (Tagset) inputElement;
		}
		return tagset.getValues().toArray(new TagsetValue[tagset.getValues().size()]);
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    this.tagset = (Tagset) newInput;
	  }

}

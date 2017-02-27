package org.corpus_tools.search.handle;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ShowSearchHandler {
	
	@Inject EPartService partService;
	
	@Execute
	public void showSearch() {
		partService.showPart("org.corpus-tools.search.parts.annissearch", PartState.ACTIVATE);
	}
}

package org.corpus_tools.atomic.grideditor.configuration;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.eclipse.nebula.widgets.nattable.edit.action.CellEditDragMode;
import org.eclipse.nebula.widgets.nattable.edit.action.KeyEditAction;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;

/**
 * Custom edit bindings for {@link GridEditor}.
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GridEditorEditBindings extends DefaultEditBindings {

	/**
	 *  Customized edit bindings. The difference between
	 *  this implementation and {@link DefaultEditBindings}
	 *  is that {@link UiBindingRegistry#registerSingleClickBinding(org.eclipse.nebula.widgets.nattable.ui.matcher.IMouseEventMatcher, org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction)}
	 *  is not called.
	 *  
	 *  This is to prevent the activation of the cell editor
	 *  whenever a cell is clicked for the first time.
	 *  
	 *  For the activation of the cell editor via
	 *  {@link MouseEditAction}, a double-click binding
	 *  is registered instead. Also, editing is activated
	 *  as soon as any "content" key is pressed in a cell,
	 *  via key bindings.
	 */
	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, 32), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.F2), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(SWT.MOD2), new KeyEditAction());

		uiBindingRegistry.registerDoubleClickBinding(new CellEditorMouseEventMatcher(GridRegion.BODY), new MouseEditAction());

		uiBindingRegistry.registerMouseDragMode(new CellEditorMouseEventMatcher(GridRegion.BODY), new CellEditDragMode());

		uiBindingRegistry.registerFirstMouseDragMode(new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, CheckBoxPainter.class), new CellEditDragMode());
	}

}

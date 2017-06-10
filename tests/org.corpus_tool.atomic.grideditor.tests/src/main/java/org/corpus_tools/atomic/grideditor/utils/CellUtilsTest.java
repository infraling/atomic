/**
 * 
 */
package org.corpus_tools.atomic.grideditor.utils;

import static org.junit.Assert.*;

import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.junit.Test;

import com.google.common.collect.Range;

import static org.mockito.Mockito.*;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CellUtilsTest {

	/**
	 * Test method for {@link org.corpus_tools.atomic.grideditor.utils.CellUtils#getRowIndicesForCell(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell)}.
	 */
	@Test
	public void testGetRowIndicesForSingleCell() {
		ILayerCell cell = mock(ILayerCell.class);
		when(cell.isSpannedCell()).thenReturn(false);
		when(cell.getOriginRowPosition()).thenReturn(7);
		when(cell.getRowPosition()).thenReturn(7);
		when(cell.getRowIndex()).thenReturn(24);
		assertEquals(Range.closed(24, 24), CellUtils.getRowIndicesForCell(cell));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.grideditor.utils.CellUtils#getRowIndicesForCell(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell)}.
	 */
	@Test
	public void testGetRowIndicesForSpannedCell() {
		ILayerCell cell = mock(ILayerCell.class);
		when(cell.isSpannedCell()).thenReturn(true);
		when(cell.getOriginRowPosition()).thenReturn(30);
		when(cell.getRowPosition()).thenReturn(34);
		when(cell.getRowIndex()).thenReturn(34);
		when(cell.getRowSpan()).thenReturn(20);
		assertEquals(Range.closed(30, 49), CellUtils.getRowIndicesForCell(cell));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.grideditor.utils.CellUtils#getRowIndicesForCell(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell)}.
	 */
	@Test
	public void testGetRowIndicesForSpannedCellWithNegativeOrigRowPosition() {
		ILayerCell cell = mock(ILayerCell.class);
		when(cell.isSpannedCell()).thenReturn(true);
		when(cell.getOriginRowPosition()).thenReturn(-10);
		when(cell.getRowPosition()).thenReturn(5);
		when(cell.getRowIndex()).thenReturn(95);
		when(cell.getRowSpan()).thenReturn(20);
		assertEquals(Range.closed(80, 99), CellUtils.getRowIndicesForCell(cell));
	}

}

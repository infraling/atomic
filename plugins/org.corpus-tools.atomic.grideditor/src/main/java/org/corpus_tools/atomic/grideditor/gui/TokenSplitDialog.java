/**
 * 
 */
package org.corpus_tools.atomic.grideditor.gui;

import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TokenSplitDialog extends Dialog {
	private Text tokenTextField;
	private final String tokenText;
	private final TreeSet<Integer> splitPositions = new TreeSet<>();

	public TokenSplitDialog(Shell parentShell, String tokenText) {
		super(parentShell);
		this.tokenText = tokenText;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(3, false);
        composite.setLayout(gridLayout);
        // create message
            Label label = new Label(composite, SWT.WRAP);
            label.setText("Split the token at the current cursor position by pressing \"Split at cursor\". To reset, press \"Reset\".");
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            data.horizontalSpan = 3;
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        
        applyDialogFont(composite);
        
        tokenTextField = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        tokenTextField.setText(tokenText);
        tokenTextField.setEditable(false);
        tokenTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        
        Button btnSplitButton = new Button(composite, SWT.NONE);
        btnSplitButton.setText("&Split at cursor");
        btnSplitButton.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1));
        
        Button btnResetButton = new Button(composite, SWT.NONE);
        btnResetButton.setText("&Reset");
        
        Label lblSplitLabel = new Label(composite, SWT.NONE);
        lblSplitLabel.setText("Splits:");
        
        Label lblSplits = new Label(composite, SWT.NONE);
        lblSplits.setText(tokenText);
        lblSplits.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1));

        // Catch button interactions
        btnSplitButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		splitPositions.add(tokenTextField.getCaretPosition());
        		StringBuilder sb = new StringBuilder();
        		int lastIndex = 0;
        		for (Integer i : splitPositions) {
        			String seq = tokenText.substring(lastIndex, i);
        			sb.append(seq);
        			sb.append(" | ");
        			lastIndex = i;
        		}
        		sb.append(tokenText.substring(lastIndex, tokenText.length()));
        		String splitText = sb.toString();
        		lblSplits.setText(splitText);
        		lblSplits.getParent().layout();
        		tokenTextField.setFocus();
        	}
        });

        btnResetButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		lblSplits.setText(tokenText);
        		lblSplits.getParent().layout();
        		splitPositions.clear();
        		tokenTextField.setFocus();
        	}
        });

        return composite;
    }

	/**
	 * @return the splitPositions
	 */
	public final TreeSet<Integer> getSplitPositions() {
		return splitPositions;
	}
	
}

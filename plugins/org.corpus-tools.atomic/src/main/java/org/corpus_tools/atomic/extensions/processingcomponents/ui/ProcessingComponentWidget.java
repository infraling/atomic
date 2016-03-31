/*******************************************************************************
 * Copyright 2016 Stephan Druskat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.extensions.processingcomponents.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class ProcessingComponentWidget extends Composite {

	private LocalResourceManager resources;

	/**
	 * @param parent
	 * @param style
	 */
	public ProcessingComponentWidget(Composite parent, int style) {
		super(parent, SWT.BORDER);
		resources = new LocalResourceManager(JFaceResources.getResources(), this);
		setLayout(getInnerLayout());
		
		Composite nameWrapper = new Composite(this, SWT.NONE);
		nameWrapper.setBackground(getResources().createColor(new RGB(0, 0, 0)));
		nameWrapper.setLayout(getWrapperLayout());
		
		Composite creatorWrapper = new Composite(this, SWT.NONE);
		creatorWrapper.setBackground(getResources().createColor(new RGB(30, 30, 30)));
		creatorWrapper.setLayout(getWrapperLayout());
		
		Composite infoWrapper = new Composite(this, SWT.NONE);
		infoWrapper.setBackground(getResources().createColor(new RGB(60, 60, 60)));
		FillLayout infoWrapperLayout = new FillLayout();
		infoWrapperLayout.marginHeight = 3;
		infoWrapperLayout.marginWidth = 3;
		infoWrapper.setLayout(infoWrapperLayout);
		
		Label nameLabel = new Label(nameWrapper, SWT.NONE);
		nameLabel.setText("Simple Salt Tokenizer 34567893");
		nameLabel.setForeground(getResources().createColor(new RGB(255, 255, 255)));
		nameLabel.setBackground(getResources().createColor(new RGB(0, 0, 0)));
		
		Label creatorLabel = new Label(creatorWrapper, SWT.NONE);
		creatorLabel.setText("Humboldt Universität zu Berlin\nde, en, it, fr");
		creatorLabel.setBackground(getResources().createColor(new RGB(30, 30, 30)));
		
		Label infoLabel = new Label(infoWrapper, SWT.RIGHT);
		infoLabel.setText("[Info] [Results]");
		infoLabel.setBackground(getResources().createColor(new RGB(60, 60, 60)));
//		infoLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private RowLayout getWrapperLayout() {
		final RowLayout wrapperLayout = new RowLayout();
		wrapperLayout.spacing = 0;
		wrapperLayout.marginTop = 0;
		wrapperLayout.marginBottom = 0;
		wrapperLayout.marginLeft = 0;
		wrapperLayout.marginRight = 0;
		wrapperLayout.marginWidth = 3;
		wrapperLayout.marginHeight = 3;
		return wrapperLayout;
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private RowLayout getInnerLayout() {
		final RowLayout innerLayout = new RowLayout(SWT.VERTICAL | SWT.WRAP);
		innerLayout.fill = true;
		innerLayout.spacing = 0;
		innerLayout.marginBottom = 0;
		innerLayout.marginHeight = 0;
		innerLayout.marginLeft = 0;
		innerLayout.marginRight = 0;
		innerLayout.marginTop = 0;
		innerLayout.marginWidth = 0;
		innerLayout.wrap = true;
		return innerLayout;
	}

	/**
	 * @return the resources
	 */
	private LocalResourceManager getResources() {
		return resources;
	}

}

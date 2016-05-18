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

import org.corpus_tools.atomic.extensions.ProcessingComponent; 
import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Implementations of this class provide the SWT and/or JFace 
 * controls needed to configure a {@link ProcessingComponent}, i.e.,
 * set values of an implementation of {@link ProcessingComponentConfiguration}.
 * <p>
 * Clients need to add the respective controls/widgets in the
 * {@link #addControls()} method. The container's default layout is
 * a four-column {@link GridLayout}, but clients can change this by
 * calling <code>parent.setLayout()</code> in {@link #addControls(Composite, int)}.
 * 
 * @see <a href="https://www.eclipse.org/swt/">https://www.eclipse.org/swt/</a>
 * @see <a href="https://wiki.eclipse.org/JFace">https://wiki.eclipse.org/JFace</a>
 * @see <a href="https://www.eclipse.org/swt/widgets/">https://www.eclipse.org/swt/widgets/</a>
 * @see <a href="http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm">http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/guide/jface.htm</a> 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class ProcessingComponentConfigurationControls {

	/**
	 * This is where SWT widgets/JFace viewers are added.
	 * 
	 * @param none 
	 * @param composite 
	 *
	 */
	public abstract void addControls(Composite parent, int style);

	/**
	 * TODO: Description
	 *
	 * @param parent The parent {@link Composite}
	 * @param style An {@link SWT} style bit
	 */
	public Composite execute(Composite parent, int style) {
		Composite composite = new Composite(parent, style);
		composite.setLayout(new GridLayout(4, false));
		addControls(composite, SWT.NONE);
		return composite;
	}
	
	/** FIXME ADD initBindings()! Cf. PageProjectStructure!
	 * 
	 * Along the lines of this cos its no wizard:
	 * 
	 *  private void bindValues() {
    // The DataBindingContext object will manage the databindings
    // Lets bind it
    DataBindingContext ctx = new DataBindingContext();
    IObservableValue widgetValue = WidgetProperties.text(SWT.Modify)
        .observe(firstName);
    IObservableValue modelValue = BeanProperties.value(Person.class,
        "firstName").observe(person);
    ctx.bindValue(widgetValue, modelValue);

    // Bind the age including a validator
    widgetValue = WidgetProperties.text(SWT.Modify).observe(ageText);
    modelValue = BeanProperties.value(Person.class, "age").observe(person);
    // add an validator so that age can only be a number
    IValidator validator = new IValidator() {
      @Override
      public IStatus validate(Object value) {
        if (value instanceof Integer) {
          String s = String.valueOf(value);
          if (s.matches("\\d*")) {
            return ValidationStatus.ok();
          }
        }
        return ValidationStatus.error("Not a number");
      }
    };

    UpdateValueStrategy strategy = new UpdateValueStrategy();
    strategy.setBeforeSetValidator(validator);

    Binding bindValue = ctx.bindValue(widgetValue, modelValue, strategy,
        null);
    // add some decorations
    ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);

    widgetValue = WidgetProperties.selection().observe(marriedButton);
    modelValue = BeanProperties.value(Person.class, "married").observe(person);
    ctx.bindValue(widgetValue, modelValue);

    widgetValue = WidgetProperties.selection().observe(genderCombo);
    modelValue = BeanProperties.value("gender").observe(person);

    ctx.bindValue(widgetValue, modelValue);

    // address field is bound to the Ui
    widgetValue = WidgetProperties.text(SWT.Modify).observe(countryText);

    modelValue = BeanProperties.value(Person.class, "address.country")
        .observe(person);
    ctx.bindValue(widgetValue, modelValue);

  }
	 */

}

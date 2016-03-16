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
package org.corpus_tools.atomic.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * An {@link IValidator} to validate whether a {@link String} is empty or null. 
 *
 * <p>@author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NotEmptyStringOrNullValidator implements IValidator {
	
	private String fieldName;
	
	/**
	 * Constructor taking a {@link String} for the name of the field that is validated.
	 * Note that the argument should start with a <b>capital letter</b> as it will be used
	 * at the start of the {@link ValidationStatus} return string.
	 */
	public NotEmptyStringOrNullValidator(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * Private constructor that must not be used by clients.
	 */
	@SuppressWarnings("unused")
	private NotEmptyStringOrNullValidator() {
		// Do nothing, will never be called.
	}

	/* 
	 * @copydoc @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			String text = (String) value;
			if (text == null || text.isEmpty()) {
				return ValidationStatus.error(fieldName + " must not be empty!");
			}
			else {
				return ValidationStatus.ok();
			}
		}
		return null;
	}

}

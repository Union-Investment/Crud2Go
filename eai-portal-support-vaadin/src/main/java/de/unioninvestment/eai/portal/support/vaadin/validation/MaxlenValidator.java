/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package de.unioninvestment.eai.portal.support.vaadin.validation;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;

/**
 * Feld Validator zur Prüfung der maximal Länge von Eingabewerten.
 * 
 * @author markus.bonsch
 * 
 */
@ValidatorKeyword("maxlen")
public class MaxlenValidator implements FieldValidator {

	private com.vaadin.data.Validator vaadinValidator;
	private int maxLen;

	/**
	 * @param len
	 *            die maximal Länge.
	 * @param validatorMsg
	 *            Hinweistext für nicht valide Eingaben.
	 */
	public MaxlenValidator(String len, String validatorMsg) {
		maxLen  = Integer.parseInt(len);
		vaadinValidator = new com.vaadin.data.validator.StringLengthValidator(
				validatorMsg, 0, maxLen , true);
	}

	@Override
	public void apply(Field<?> field) {
		if (field instanceof AbstractTextField) {
			((AbstractTextField)field).setMaxLength(maxLen);
		}
		field.addValidator(vaadinValidator);
	}
}

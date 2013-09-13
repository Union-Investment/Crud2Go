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

import com.vaadin.ui.Field;


/**
 * Feld Validator zur Prüfung einer gültigen Eingabe auf Basis eines
 * Regulärenausdrucks.
 * 
 * @author markus.bonsch
 * 
 */
@ValidatorKeyword("regexp")
public class RegexpValidator implements FieldValidator {

	private com.vaadin.data.Validator vaadinValidator;

	/**
	 * 
	 * @param regexp
	 *            der Reguläreausdruck für die Validierung
	 * @param validatorMsg
	 *            Hinweistext für nicht valide Eingaben.
	 */
	public RegexpValidator(String regexp, String validatorMsg) {
		vaadinValidator = new com.vaadin.data.validator.RegexpValidator(regexp,
				validatorMsg);
	}

	@Override
	public void apply(Field<?> field) {
		field.addValidator(vaadinValidator);
	}

}

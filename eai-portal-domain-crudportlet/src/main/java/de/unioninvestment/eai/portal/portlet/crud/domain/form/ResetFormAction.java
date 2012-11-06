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
package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DeferredExecutionActionProxy;

/**
 * Implementiert das Zurücksetzen der Formularfelder.
 * 
 * @author carsten.mjartan
 */
public class ResetFormAction implements ActionHandler {

	@Override
	public void execute(Form form) {
		DeferredExecutionActionProxy valueChangeTriggerAction = form
				.getValueChangeTriggerAction();

		if (valueChangeTriggerAction != null) {
			valueChangeTriggerAction.setActivated(false);
		}
		try {
			for (FormField fieldToFilter : form.getFields()) {
				fieldToFilter.reset();
			}

			if (valueChangeTriggerAction != null) {
				valueChangeTriggerAction.setActivated(true);
				valueChangeTriggerAction.execute();
			}
		} finally {
			if (valueChangeTriggerAction != null) {
				// Im Falle einer Exception sicher gehen dass die Action wieder
				// aktiviert ist
				valueChangeTriggerAction.setActivated(true);
			}
		}
	}
}

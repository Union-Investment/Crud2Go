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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;

/**
 * 
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class ScriptSearchFormAction extends ScriptFormAction {

	/**
	 * Erzeugt eine neue instanz.
	 * 
	 * @param action
	 *            die FormAction.
	 */
	ScriptSearchFormAction(FormAction action) {
		super(action);
		if (!(action.getActionHandler() instanceof SearchFormAction)) {
			throw new IllegalArgumentException(
					"FormActions with SearchFormAction handler are only accepted!");
		}
	}

	/**
	 * @param seconds
	 *            die Timeoutzeit in Sekunden. 0 bedeutet kein Timeout. Werte
	 *            unter 0 werden ignoriert.
	 */
	public void setTimeout(int seconds) {
		((SearchFormAction) this.action.getActionHandler()).setTimeout(seconds);
	}
}

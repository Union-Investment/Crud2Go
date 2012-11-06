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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;

/**
 * Liste von {@link FormAction}, bietet Aggregatsfunktionen.
 * 
 * @author carsten.mjartan
 */
public class FormActions implements Iterable<FormAction> {

	private List<FormAction> actions = new ArrayList<FormAction>();

	/**
	 * 
	 * @param actions
	 *            FormAction
	 */
	public FormActions(List<FormAction> actions) {
		this.actions.addAll(actions);
	}

	/**
	 * @return die erste Action für die Suche oder <code>null</code>, wenn keine
	 *         entsprechende Action gefunden wurde
	 */
	public FormAction getSearchAction() {
		for (final FormAction action : actions) {
			if (action.getActionHandler() instanceof SearchFormAction) {
				return action;
			}
		}
		return null;
	}

	@Override
	public Iterator<FormAction> iterator() {
		return actions.iterator();
	}

	public FormAction getById(String id) {
		for (FormAction action : actions) {
			if (id.equals(action.getId())) {
				return action;
			}
		}
		return null;
	}

	public void attachToForm(Form form) {
		for (FormAction action : actions) {
			action.setForm(form);
		}
	}

}

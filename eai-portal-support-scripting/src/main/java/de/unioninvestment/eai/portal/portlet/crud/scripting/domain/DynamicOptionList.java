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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import groovy.lang.Closure;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormField;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Modell-Klasse für dynamische Auswahl-Boxen.
 * 
 * 
 * @author siva.selvarajah
 */
public class DynamicOptionList implements OptionList {

	private DataContainer container;
	private final Closure<?> optionsClosure;
	private String id;
	private EventRouter<OptionListChangeEventHandler, OptionListChangeEvent> changeEventRouter = new EventRouter<OptionListChangeEventHandler, OptionListChangeEvent>();
	
	/**
	 * Konstruktor.
	 * 
	 * @param table
	 *            Tabelle
	 * @param closure
	 *            closure
	 */
	public DynamicOptionList(Table table, Closure<?> closure,
			SelectConfig config) {
		this.container = table.getContainer();
		this.optionsClosure = closure;
		id = config.getId();
	}

	public DynamicOptionList(Closure<?> closure,
			SelectConfig config) {
		this.optionsClosure = closure;
		id = config.getId();
	}

	@Override
	public Map<String, String> getOptions(SelectionContext context) {
		Object result = null;
		if (context instanceof TableColumnSelectionContext) {
			TableColumnSelectionContext ctx = (TableColumnSelectionContext) context;
			ScriptRow scriptRow = new ScriptRow(container.getRow(ctx.getRowId(), false, true));
			result = optionsClosure.call(scriptRow, ctx.getColumnName());
		} else {
			FormSelectionContext ctx = (FormSelectionContext) context;
			ScriptFormField field = new ScriptFormField(ctx.getOptionListformField());
			result = optionsClosure.call(field);
		}
		Map<String, String> res = new LinkedHashMap<String, String>();
		if (result instanceof Map) {
			for (Entry<?, ?> entry : ((Map<?, ?>) result).entrySet()) {
				res.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} else if (result instanceof Collection<?>) {
			for (Object o : (Collection<?>) result) {
				res.put(o.toString(), o.toString());
			}
		} else {
			throw new IllegalArgumentException(
					"Dynamic Selections only support Maps and Collections");
		}
		return res;
	}

	@Override
	public String getTitle(String key, SelectionContext context) {
		return getOptions(context).get(key);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addValueChangeListener(OptionListChangeEventHandler handler) {
		changeEventRouter.addHandler(handler);
	}

	@Override
	public void refresh() {
		changeEventRouter.fireEvent(new OptionListChangeEvent(this));
	}
}
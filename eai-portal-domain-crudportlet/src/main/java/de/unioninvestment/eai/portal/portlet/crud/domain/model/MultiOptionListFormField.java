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

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Formular-Feld für Auswahl-Boxen.
 * 
 * @author max.hartmann
 * 
 */
public class MultiOptionListFormField extends OptionListFormField {

	private final class HistoryAwareValueChangeListener implements
			ValueChangeListener {
		private static final long serialVersionUID = 1L;

		private Collection<String> currentValue = MultiOptionListFormField.this
				.getValues();

		@Override
		@SuppressWarnings("unchecked")
		public void valueChange(ValueChangeEvent event) {
			if (!currentValue.equals(event.getProperty().getValue())) {
				currentValue = (Set<String>) event.getProperty().getValue();

				fireValueChangeEvent();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ObjectProperty<Set<Object>> listProperty = new ObjectProperty(
			new HashSet(), Set.class);

	/**
	 * Nur für Tests.
	 * 
	 * @param config
	 *            Formular-Konfiguration
	 * @param optionList
	 *            Auswahl-Box-Modell
	 * @param eventRouter
	 *            EventRouter
	 */
	MultiOptionListFormField(
			FormFieldConfig config,
			OptionList optionList,
			EventRouter<FormFieldChangeEventHandler, FormFieldChangeEvent> eventRouter) {
		this(config, optionList);
		this.eventRouter = eventRouter;
	}

	/**
	 * Konstruktor mit Paramtern.
	 * 
	 * @param config
	 *            Formular-Konfiguration
	 * @param optionList
	 *            Auswahl-Box-Modell
	 */
	public MultiOptionListFormField(FormFieldConfig config, OptionList optionList) {
		super(config, optionList, false);
		if (config.getDefault() != null) {
			HashSet<Object> set = hashSetOf(config.getDefault());
			listProperty.setValue(set);
		}
		listProperty.addValueChangeListener(new HistoryAwareValueChangeListener());

	}

	private static final HashSet<Object> hashSetOf(Object... values) {
		return new HashSet<Object>(asList(values));
	}

	@Override
	public ObjectProperty<String> getProperty() {
		throw new UnsupportedOperationException();
	}

	public ObjectProperty<Set<Object>> getListProperty() {
		return listProperty;
	}

	@Override
	public String getValue() {
		return this.listProperty.getValue().size() == 1 ? (String) this.listProperty
				.getValue().iterator().next()
				: null;
	}

	/**
	 * Setzt die selektierten Einträge.
	 * 
	 * @param values
	 *            zu selektierenden Einträge
	 */
	public void setValues(Collection<String> values) {
		listProperty.setValue(new HashSet<Object>(values));
		if (values.size() == 0) {
			super.setValue(null);
		} else if (values.size() == 1) {
			super.setValue(values.iterator().next());
		}
	}

	@Override
	public void setValue(String value) {
		setValues(value == null ? new HashSet<String>() : Arrays.asList(value));
	}

	/**
	 * Selektiert einen Eintrag.
	 * 
	 * @return selektierter Eintrag
	 */
	public Set<String> getValues() {
		HashSet<String> set = new HashSet<String>();
		for (Object element : listProperty.getValue()) {
			set.add((String) element);
		}
		return set;
	}

}

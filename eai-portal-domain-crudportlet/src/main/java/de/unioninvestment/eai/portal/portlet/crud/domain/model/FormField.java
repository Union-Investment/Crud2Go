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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

/**
 * Immutable Value Object für die Tabellenspaltenkonfiguration zur Übergabe an
 * die Presenter.
 * 
 * @author markus.bonsch
 */
public class FormField implements Serializable {

	private final class HistoryAwareValueChangeListener implements
			ValueChangeListener {
		private static final long serialVersionUID = 1L;

		private String currentValue = FormField.this.getValue();

		@Override
		public void valueChange(ValueChangeEvent event) {
			if (!StringUtils.equals(currentValue, (String) event.getProperty()
					.getValue())) {
				currentValue = (String) event.getProperty().getValue();
				fireValueChangeEvent();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	private ObjectProperty<String> property = new ObjectProperty<String>("");

	protected final FormFieldConfig config;

	private List<FieldValidator> validators;

	protected EventRouter<FormFieldChangeEventHandler, FormFieldChangeEvent> eventRouter = new EventRouter<FormFieldChangeEventHandler, FormFieldChangeEvent>();

	private String name;

	private String title;

	private String defaultValue;

	private String inputPrompt;

	private boolean editable;

	/**
	 * Konstruktor.
	 * 
	 * @param name
	 *            Feldbezeichnung
	 * @param title
	 *            Feldtitel
	 * @param inputPrompt
	 *            Feld Inputpromt
	 */
	public FormField(String name, String title, String inputPrompt,
			boolean editable) {
		this.name = name;
		this.title = title;
		this.inputPrompt = inputPrompt;
		this.editable = editable;

		this.config = null;

		property.addListener(new HistoryAwareValueChangeListener());
	}

	/**
	 * Konstruktor.
	 * 
	 * @param config
	 *            FormFieldConfig
	 */
	public FormField(FormFieldConfig config) {
		this(config, true);
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param config
	 *            FormFieldConfig
	 * @param registerValueChangeListener
	 *            Ob der ValueChangeListener registriert werden soll.
	 */
	public FormField(FormFieldConfig config, boolean registerValueChangeListener) {
		this.config = config;
		property.setValue(config.getDefault());

		if (registerValueChangeListener) {
			property.addListener(new HistoryAwareValueChangeListener());
		}

		name = config.getName();
		title = config.getTitle();
		defaultValue = config.getDefault();
		inputPrompt = config.getInputPrompt();
	}

	/**
	 * 
	 * @param handler
	 *            TabChangeEventHandler
	 */
	public void addFormFieldChangeEventListener(
			FormFieldChangeEventHandler handler) {
		eventRouter.addHandler(handler);
	}

	/**
	 * Feuert den ValueChange Event.
	 */
	protected void fireValueChangeEvent() {
		FormFieldChangeEvent event = new FormFieldChangeEvent(this);
		eventRouter.fireEvent(event);
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title != null ? title : name;
	}

	public String getInputPrompt() {
		return inputPrompt;
	}

	public ObjectProperty<String> getProperty() {
		return property;
	}

	/**
	 * Ist nur ein Workaroud. Sollte geändert werden.
	 * 
	 * @return Defaultwert
	 */
	public String getDefault() {
		return defaultValue;
	}

	/**
	 * 
	 * @param value
	 *            Wert
	 */
	public void setValue(String value) {
		this.property.setValue(value);
	}

	public String getValue() {
		return this.property.getValue();
	}

	public List<FieldValidator> getValidators() {
		return validators;
	}

	/**
	 * Setzt am Property den Defaultvalue.
	 */
	public void reset() {
		setValue(defaultValue);
	}

	public void setValidators(List<FieldValidator> validators) {
		this.validators = validators;
	}

	boolean isEditable() {
		return editable;
	}

}

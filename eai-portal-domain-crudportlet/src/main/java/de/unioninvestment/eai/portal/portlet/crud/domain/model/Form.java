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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;

/**
 * 
 * Modell-Klassen für Formulare.
 * 
 * @author max.hartmann
 * 
 */
public class Form extends Component implements Component.ExpandableComponent,
		Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(Form.class);

	private static final long serialVersionUID = 1L;

	private final FormConfig config;
	private final FormFields fields;
	private DeferredExecutionActionProxy valueChangeTriggerAction;
	private FormActions actions;

	/**
	 * 
	 * Konstruktor mit Parametern.
	 * 
	 * @param config
	 *            Konfiguration des Formulares
	 * @param fields
	 *            Felder des Formulares
	 */
	public Form(FormConfig config, FormFields fields) {
		this.config = config;
		this.fields = fields;
	}

	/**
	 * Konstruktor.
	 * 
	 * @param config
	 *            Formularkonfiguration
	 * @param fields
	 *            Formularfelder
	 * @param actions
	 *            Formularaktionen
	 */
	public Form(FormConfig config, FormFields fields, FormActions actions) {
		this.config = config;
		this.fields = fields;
		attachActions(actions);
		wireOnChangesTrigger();
	}

	private void attachActions(FormActions actions) {
		this.actions = actions;
		actions.attachToForm(this);
	}

	public String getId() {
		return config.getId();
	}

	public int getColumns() {
		return config.getColumns();
	}

	public FormFields getFields() {
		return fields;
	}

	public FormActions getActions() {
		return actions;
	}

	public DeferredExecutionActionProxy getValueChangeTriggerAction() {
		return this.valueChangeTriggerAction;
	}

	private void wireOnChangesTrigger() {
		String valueChangeTriggerActionId = this.config.getTriggerOnChanges();
		if (valueChangeTriggerActionId != null) {
			LOG.debug("Value change trigger action ID: "
					+ valueChangeTriggerActionId);
			hideAndWrapSearchAction(valueChangeTriggerActionId);
			addValueChangeTriggerToFields();
		}
	}

	private void addValueChangeTriggerToFields() {
		fields.addListener(new FormFieldChangeEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onValueChange(FormFieldChangeEvent event) {
				valueChangeTriggerAction.execute();
			}
		});
	}

	private void hideAndWrapSearchAction(String valueChangeTriggerActionId) {
		if (actions != null
				&& actions.getById(valueChangeTriggerActionId) != null) {
			FormAction action = actions.getById(valueChangeTriggerActionId);
			ActionHandler actionHandler = action.getActionHandler();
			if (actionHandler instanceof ResetFormAction) {
				throw new BusinessException("portlet.crud.reset.value.change");
			}
			LOG.debug("Setting value change trigger action: " + action.getId());
			action.setHidden(true);
			this.valueChangeTriggerAction = new DeferredExecutionActionProxy(
					action);
		} else {
			throw new TechnicalCrudPortletException("Unknown form action id '"
					+ valueChangeTriggerActionId + "'");
		}
	}

	public boolean hasFilter() {
		for (FormField field : fields) {
			if (field.getValue() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.44
	 */
	@Override
	public int getExpandRatio() {
		return config.getExpandRatio();
	}
}

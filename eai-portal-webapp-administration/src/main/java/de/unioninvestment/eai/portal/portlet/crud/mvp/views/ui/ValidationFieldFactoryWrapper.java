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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

/**
 * Fügt den editierbare Feldern Validatoren hinzu, die per Konfiguration
 * angegeben wurden.
 * 
 * @author carsten.mjartan
 */
public class ValidationFieldFactoryWrapper implements CrudFieldFactory {

	private final class LoggingErrorHandler implements ErrorHandler {
		private final Field<?> field;
		private static final long serialVersionUID = 1L;

		private LoggingErrorHandler(Field<?> field) {
			this.field = field;
		}

		@Override
		public void error(ErrorEvent event) {
			LOG.debug("Error handler:" + field.getClass().getName());
		}
	}

	private static final long serialVersionUID = 1L;

	private final DataContainer dataContainer;
	private final CrudFieldFactory delegate;
	private final TableColumns columns;
	private static final Logger LOG = LoggerFactory
			.getLogger(ValidationFieldFactoryWrapper.class);

	/**
	 * @param databaseContainer
	 *            Container
	 * @param delegate
	 *            Delegat
	 * @param columns
	 *            Tabellenspaltenkonfiguration
	 */
	public ValidationFieldFactoryWrapper(DataContainer databaseContainer,
			DefaultCrudFieldFactory delegate, TableColumns columns) {
		this.dataContainer = databaseContainer;
		this.delegate = delegate;
		this.columns = columns;
	}

	@Override
	public Field<?> createField(Container container, Object itemId,
			Object propertyId, Component uiContext) {
		Field<?> field = delegate.createField(container, itemId, propertyId,
				uiContext);
		Item item = container.getItem(itemId);
		addFieldValidators(item, propertyId, field);
		return field;
	}

	@Override
	public Field<?> createField(Item item, Object propertyId) {
		final Field<?> field = delegate.createField(item, propertyId);

		if (field != null) {
//			if (field.getClass().isAssignableFrom(AbstractField.class)) {
//				((AbstractTextField) field)
//						.setErrorHandler(new LoggingErrorHandler(field));
//			}
			addFieldValidators(item, propertyId, field);
		}
		return field;
	}

	private void addFieldValidators(Item item, Object propertyId, Field<?> field) {
		if (field != null) {
			addConfiguredValidators(field, propertyId);
			addContainerDrivenValidators(item, field, propertyId);
		}
	}

	private void addConfiguredValidators(Field<?> field, Object propertyId) {
		if (columns != null) {
			TableColumn column = columns.get((String) propertyId);
			if (column != null && column.getValidators() != null) {
				for (FieldValidator validator : column.getValidators()) {
					validator.apply(field);
				}
			}
		}
	}

	private void addContainerDrivenValidators(Item item, Field<?> field,
			Object propertyId) {
		if (!field.isRequired()) {
			ContainerRow row = dataContainer
					.convertItemToRow(item, false, true);
			ContainerField containerField = row.getFields().get(propertyId);

			if (containerField.isRequired()) {

				if (!field.isReadOnly()) {
					field.setRequired(true);
				}

				if (field instanceof AbstractSelect) {
					((AbstractSelect) field).setNullSelectionAllowed(false);
				}
				field.setRequiredError(getMessage(
						"portlet.crud.table.field.mandatory", propertyId));
			}
		}
	}
}

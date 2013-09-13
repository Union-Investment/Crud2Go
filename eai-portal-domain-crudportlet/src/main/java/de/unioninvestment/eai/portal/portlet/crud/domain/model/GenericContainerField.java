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

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.TransactionCallback;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * ContainerField Model Klasse für den GeniericDataContainer
 * 
 * @author markus.bonsch
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GenericContainerField extends ContainerField {

	private final GenericContainerRow row;
	private final GenericProperty property;
	private final DataContainer container;

	public GenericContainerField(GenericContainerRow genericContainerRow,
			GenericProperty itemProperty, DataContainer container) {
		this.row = genericContainerRow;
		this.property = itemProperty;
		this.container = container;
	}

	@Override
	public String getName() {
		return property.getName();
	}

	@Override
	public Object getValue() {
		if (row.isTransactional()) {
			return container
					.withExistingTransaction(new TransactionCallback<Object>() {
						@Override
						public Object doInTransaction() {
							return extractValue();
						}

					});
		} else {
			return extractValue();
		}
	}

	private Object extractValue() {
		if (container.isCLob(getName())) {
			return container.getCLob(row.getId(), getName());
		} else {
			return property.getValue();
		}
	}

	public boolean isImmutable() {
		return row.isImmutable();
	}

	@Override
	public void setValue(final Object value) {
		if (isImmutable()) {
			throw new ContainerException("Field '" + property.getName()
					+ "' is immutable!");

		} else if (isReadonly()) {
			throw new ContainerException("Field '" + property.getName()
					+ "' is readonly!");

		} else if (row.isTransactional()) {
			container
					.withExistingTransaction(new TransactionCallback<Object>() {
						@Override
						public Object doInTransaction() {
							property.setValue(value);
							return null;
						}
					});
		} else {
			property.setValue(value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setText(final String value) {
		if (row.isTransactional()) {
			container
					.withExistingTransaction(new TransactionCallback<Object>() {
						@Override
						public Object doInTransaction() {
							return setTextInternal(value);
						}

					});
		} else {
			setTextInternal(value);
		}
	}

	private Object setTextInternal(final String value) {
		EditorSupport editorSupport = container.findEditor((String) property
				.getName());

		if (isImmutable()) {
			throw new ContainerException("Field '" + property.getName()
					+ "' is immutable!");
		}
		if (!isReadonly() && editorSupport != null) {
			Converter<String, ?> formatter = editorSupport.createFormatter(
					property.getType(),
					container.getFormat((String) property.getName()));

			if (formatter != null) {
				Object converted = formatter
						.convertToModel(value, (Class) property.getType(), UI
								.getCurrent().getLocale());
				property.setValue(converted);

			} else {
				property.setValue(value);
			}
		} else {
			throw new ContainerException("Field '" + property.getName()
					+ "' is readonly!");
		}
		return null;
	}

	/**
	 * 
	 * @return Value als String
	 */
	public String getText() {
		DisplaySupport displaySupport = container
				.findDisplayer((String) property.getName());

		if (displaySupport != null) {
			return displaySupport.formatPropertyValue(property,
					container.getFormat((String) property.getName()));
		}
		return null;
	}

	@Override
	public boolean isModified() {
		return property.isModified();
	}

	@Override
	public boolean isReadonly() {
		return property.isReadOnly() || row.isReadonly();
	}

	@Override
	public boolean isRequired() {
		return property.isRequired();
	}

	@Override
	public Class<?> getType() {
		return container.getType(getName());
	}
}

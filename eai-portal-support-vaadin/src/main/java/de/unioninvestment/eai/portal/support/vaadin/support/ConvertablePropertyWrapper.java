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

package de.unioninvestment.eai.portal.support.vaadin.support;

import java.util.LinkedList;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.DateField;

/**
 * Property Wrapper that allows converting values. This is needed for fields
 * like {@link DateField} that support only limited conversion.
 * 
 * @author carsten.mjartan
 * 
 * @param <P>
 *            the front property type
 * @param <M>
 *            the backing property type
 */
public class ConvertablePropertyWrapper<P, M> extends AbstractProperty<P>
		implements Property<P>, Property.ReadOnlyStatusChangeListener,
		Property.ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private Property<M> backingProperty;
	private Converter<P, M> converter;
	private Locale locale;

	private LinkedList<ValueChangeListener> valueChangeListeners = null;

	public ConvertablePropertyWrapper(Property<M> backingProperty,
			Converter<P, M> converter, Locale locale) {
		this.backingProperty = backingProperty;
		this.converter = converter;
		this.locale = locale;
		if (backingProperty instanceof ReadOnlyStatusChangeNotifier) {
			((ReadOnlyStatusChangeNotifier) backingProperty)
					.addReadOnlyStatusChangeListener(this);
		}
		if (backingProperty instanceof ValueChangeNotifier) {
			((ValueChangeNotifier) backingProperty)
					.addValueChangeListener(this);
		}
	}

	@Override
	public P getValue() {
		return converter.convertToPresentation(backingProperty.getValue(),
				converter.getPresentationType(), locale);
	}

	@Override
	public void setValue(P newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		backingProperty.setValue(converter.convertToModel(newValue,
				backingProperty.getType(), locale));
	}

	@Override
	public Class<? extends P> getType() {
		return converter.getPresentationType();
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		backingProperty.setReadOnly(newStatus);
	}

	@Override
	public void readOnlyStatusChange(
			com.vaadin.data.Property.ReadOnlyStatusChangeEvent event) {
		super.setReadOnly(event.getProperty().isReadOnly());
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		fireValueChange();
	}

}

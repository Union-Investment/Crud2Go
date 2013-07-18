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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;

public class ConvertablePropertyWrapperTest {

	private Property<String> backingProperty;
	private Converter<Date, String> converter;
	private ConvertablePropertyWrapper<Date, String> wrapper;

	@Mock
	private ValueChangeListener listenerMock;
	@Mock
	private ReadOnlyStatusChangeListener readonlyListenerMock;

	@Captor
	private ArgumentCaptor<Property.ValueChangeEvent> eventCaptor;
	@Captor
	private ArgumentCaptor<ReadOnlyStatusChangeEvent> readonlyEventCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		backingProperty = new ObjectProperty<String>("Test", String.class);

		converter = new DateToStringConverter(Calendar.DAY_OF_MONTH,
				"dd.MM.yyyy");

		wrapper = new ConvertablePropertyWrapper<Date, String>(backingProperty,
				converter, Locale.GERMANY);
	}

	@Test
	public void shouldConvertToBackingProperty() {
		wrapper.setValue(new GregorianCalendar(2013, 6, 18).getTime());
		assertThat(backingProperty.getValue(), is("18.07.2013"));
	}

	@Test
	public void shouldConvertFromBackingProperty() {
		backingProperty.setValue("18.07.2013");
		assertThat(wrapper.getValue(),
				is(new GregorianCalendar(2013, 6, 18).getTime()));
	}

	@Test
	public void shouldNotifyAboutFrontendChanges() {
		wrapper.addValueChangeListener(listenerMock);

		wrapper.setValue(new GregorianCalendar(2013, 6, 18).getTime());

		verify(listenerMock).valueChange(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getProperty(), sameInstance((Property)wrapper));
		
		verifyNoMoreInteractions(listenerMock);
	}
	
	@Test
	public void shouldNotifyAboutBackendChanges() {
		wrapper.addValueChangeListener(listenerMock);

		backingProperty.setValue("18.07.2013");

		verify(listenerMock).valueChange(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getProperty(), sameInstance((Property)wrapper));
		
		verifyNoMoreInteractions(listenerMock);
	}
	
	@Test
	public void shouldPropagateBackendReadonlyChanges() {
		backingProperty.setReadOnly(true);
		assertThat(wrapper.isReadOnly(), is(true));
	}

	@Test
	public void shouldPropagateFrontendReadonlyChanges() {
		wrapper.setReadOnly(true);
		assertThat(backingProperty.isReadOnly(), is(true));
	}

	@Test
	public void shouldNotifyAboutFrontendReadonlyChanges() {
		wrapper.addReadOnlyStatusChangeListener(readonlyListenerMock);

		wrapper.setReadOnly(true);

		verify(readonlyListenerMock).readOnlyStatusChange(readonlyEventCaptor.capture());
		assertThat(readonlyEventCaptor.getValue().getProperty(), sameInstance((Property)wrapper));
		
		verifyNoMoreInteractions(readonlyListenerMock);
	}
	
	@Test
	public void shouldNotifyAboutBackendReadonlyChanges() {
		wrapper.addReadOnlyStatusChangeListener(readonlyListenerMock);

		backingProperty.setReadOnly(true);

		verify(readonlyListenerMock).readOnlyStatusChange(readonlyEventCaptor.capture());
		assertThat(readonlyEventCaptor.getValue().getProperty(), sameInstance((Property)wrapper));
		
		verifyNoMoreInteractions(readonlyListenerMock);
	}
	
}

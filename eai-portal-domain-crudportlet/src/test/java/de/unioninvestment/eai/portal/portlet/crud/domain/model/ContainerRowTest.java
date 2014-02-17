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

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

abstract public class ContainerRowTest {

	@Mock
	protected DataContainer dataContainerMock;

	@Mock
	private ContainerField containerFieldMock;

	@Mock
	private ContainerRowId rowIdMock;

	@Mock
	private ContainerClob clobMock;

	@SuppressWarnings("rawtypes")
	@Mock
	private Property clobPropertyMock;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSetValue() {
		// given
		String key = "test-name";
		String value = "value";

		ContainerRow row = createContainerRow();
		row.setFields(singletonMap(key, containerFieldMock));

		// when
		row.setValue(key, value);

		// then
		Mockito.verify(containerFieldMock).setValue(value);
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldFailWithExceptionWhenNoFieldForIdExists() {
		ContainerRow containerRow = createContainerRow();
		containerRow.setValue("not-existing-id", "2");
	}

	@Test
	public void shouldGetValue() {
		// given
		String key = "test-name";
		String value = "value";

		ContainerRow row = createContainerRow();
		row.setFields(singletonMap(key, containerFieldMock));

		Mockito.when(containerFieldMock.getValue()).thenReturn(value);

		// when
		Object result = row.getValue(key);

		// then
		Mockito.verify(containerFieldMock).getValue();
		assertThat(result,  is((Object)value));
	}

	@Test
	public void shouldGetValues() {
		// given
		String key = "test-name";
		String value = "value";

		ContainerRow row = createContainerRow();
		row.setFields(singletonMap(key, containerFieldMock));

		Mockito.when(containerFieldMock.getValue()).thenReturn(value);

		// when
		Map<String, Object> values = row.getValues();

		// then
		assertThat(values.size(), is(1));
		assertThat(values.get(key), is((Object)value));
	}

	@Test
	public void shouldSetText() {
		// given
		String key = "test-name";
		String value = "value";
		ContainerRow row = createContainerRow();
		row.setFields(singletonMap(key, containerFieldMock));

		Mockito.when(containerFieldMock.getText()).thenReturn(value);

		// when
		row.setText(key, value);

		// then
		Mockito.verify(containerFieldMock).setText(value);
	}

	abstract ContainerRow createContainerRow();

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnFormItemWrapperContainingAllProperties() {
		ContainerRow rowSpy = spy(createContainerRow());
		Item testItem = createTestItem();
		when(rowSpy.getInternalRow()).thenReturn(testItem);

		Item wrapper = rowSpy.getFormItem();

		assertThat((Collection<String>) wrapper.getItemPropertyIds(), hasItems("text", "clob", "blob"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldReturnFormItemWithReplacedClob() {
		ContainerRow rowSpy = spy(createContainerRow());
		Item testItem = createTestItem();
		when(rowSpy.getId()).thenReturn(rowIdMock);
		when(rowSpy.getInternalRow()).thenReturn(testItem);
		when(dataContainerMock.isCLob("clob")).thenReturn(true);
		when(dataContainerMock.getCLob(rowIdMock, "clob")).thenReturn(clobMock);
		when(clobMock.getPropertyValue()).thenReturn((Property)clobPropertyMock);
		
		Item wrapper = rowSpy.getFormItem();
		
		assertThat(wrapper.getItemProperty("clob"), is(clobPropertyMock));
	}
	
	private Item createTestItem() {
		Item testItem = new PropertysetItem();
		testItem.addItemProperty("text", new ObjectProperty<String>("Text",
				String.class));
		testItem.addItemProperty("clob", new ObjectProperty<Clob>(null,
				Clob.class));
		testItem.addItemProperty("blob", new ObjectProperty<Blob>(null,
				Blob.class));
		return testItem;
	}
}

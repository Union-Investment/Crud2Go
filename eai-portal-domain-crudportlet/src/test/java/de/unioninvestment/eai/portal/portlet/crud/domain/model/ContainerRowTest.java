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

import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

abstract public class ContainerRowTest {

	@Mock
	private ContainerField containerFieldMock;

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
		Assert.assertEquals(value, result);
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
}

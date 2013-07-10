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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.CheckboxConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;

public class CheckBoxFormFieldTest {

	@Mock
	private FormFieldConfig configMock;

	@Mock
	private CheckboxConfig checkboxConfigMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCheckboxFormatter() {
		when(checkboxConfigMock.getCheckedValue()).thenReturn("");
		when(checkboxConfigMock.getUncheckedValue()).thenReturn("");

		when(configMock.getCheckbox()).thenReturn(checkboxConfigMock);
		CheckBoxFormField checkBoxFormField = new CheckBoxFormField(configMock);
		assertThat(checkBoxFormField.getConverter(), notNullValue());
	}
}

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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormSelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.QueryOptionList;

public class ScriptMultiOptionListFormFieldTest {

	private ScriptMultiOptionListFormField field;

	@Mock
	private FormSelectConfig formSelectConfig;

	@Mock
	private FormFieldConfig config;

	@Mock
	private QueryOptionList selection;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(formSelectConfig.getVisibleRows()).thenReturn(3);
		when(config.getSelect()).thenReturn(formSelectConfig);
	}

	@Test
	public void shouldReturnValues() {
		Set<String> values = new HashSet<String>();
		values.add("foo");
		values.add("bar");

		MultiOptionListFormField f = new MultiOptionListFormField(config,
				selection);
		field = new ScriptMultiOptionListFormField(f);
		field.setValues(values);

		assertThat(field.getValues(), equalTo(values));
	}
}

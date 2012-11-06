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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;

public class FormFieldsTest {

	private FormFields fields;
	private FormField field1;
	private FormField field2;

	@Before
	public void setupData() {
		field1 = createFormField("field1", "title1", "prompt1", null);
		field2 = createFormField("field2", "title2", "prompt2", null);
		fields = new FormFields(field1, field2);
	}

	private FormField createFormField(String name, String title,
			String inputPrompt, String defaultValue) {
		FormFieldConfig config = new FormFieldConfig();
		config.setName(name);
		config.setTitle(title);
		config.setInputPrompt(inputPrompt);
		config.setDefault(defaultValue);
		return new FormField(config);
	}

	@Test
	public void shouldReturnFieldCount() {
		assertThat(fields.count(), is(2));
	}

	@Test
	public void shouldAllowIteration() {
		assertThat(fields.iterator().next(), is(field1));
	}

	@Test
	public void shouldReturnFieldByName() {
		assertThat(fields.get("field1"), is(field1));
		assertThat(fields.get("field2"), is(field2));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionOnUnknownElement() {
		fields.get("field3");
	}

	@Test
	public void shouldReturnThatFieldsHaveDefaultValues() {
		field1 = createFormField("field1", "title1", "prompt1", null);
		field2 = createFormField("field2", "title2", "prompt2", "x");
		fields = new FormFields(field1, field2);

		assertThat(fields.hasDefaultValue(), is(true));
	}

	@Test
	public void shouldReturnThatFieldsHaveNoDefaultValues() {
		assertThat(fields.hasDefaultValue(), is(false));
	}
}

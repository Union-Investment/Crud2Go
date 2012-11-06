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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.ObjectProperty;

public class CheckboxPropertyConverterTest {


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetValueReturnTrue() {

		CheckboxPropertyConverter cbpf = new CheckboxPropertyConverter(
				new ObjectProperty<String>(""), "Teststr", "false");
		cbpf.getPropertyDataSource().setValue("Teststr");

		assertTrue((Boolean) cbpf.getValue());
	}

	@Test
	public void shouldGetValueReturnFalse() {

		CheckboxPropertyConverter cbpf = new CheckboxPropertyConverter(
				new ObjectProperty<String>(""), "Teststr", "false");
		cbpf.getPropertyDataSource().setValue("Teststr1");

		assertFalse((Boolean) cbpf.getValue());
	}

	@Test
	public void shouldGetValueReturnFalseNullValue() {

		CheckboxPropertyConverter cbpf = new CheckboxPropertyConverter(
				new ObjectProperty<String>(""), "Teststr", "false");
		cbpf.getPropertyDataSource().setValue(null);

		assertFalse((Boolean) cbpf.getValue());
	}

	@Test
	public void shouldSetValueChecked() {

		CheckboxPropertyConverter cbpf = new CheckboxPropertyConverter(
				new ObjectProperty<String>(""), "Teststr", "false");
		cbpf.setValue(true);
		assertEquals("Teststr", cbpf.getPropertyDataSource().getValue());
	}

	@Test
	public void shouldSetValueUnchecked() {

		CheckboxPropertyConverter cbpf = new CheckboxPropertyConverter(
				new ObjectProperty<String>(""), "Teststr", "keine Teststr");
		cbpf.setValue(false);
		assertEquals("keine Teststr", cbpf.getPropertyDataSource().getValue());
	}
}

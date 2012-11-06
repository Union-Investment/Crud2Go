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
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

abstract public class ContainerFieldTest {

	protected String propertyId = "id";
	protected String propertyName = "name";
	protected Object propertyValue = "value";
	
	@Before
	public final void setupContainerFieldTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetValue() {
		ContainerField containerField = createContainerField();
		assertThat(containerField.getValue(), is(propertyValue));
	}

	@Test
	public void shouldGetName() {
		ContainerField containerField = createContainerField();
		assertThat(containerField .getName(), is(propertyName));
	}
	
	@Test
	public void shouldSetValue() {
		ContainerField containerField = createContainerField();
		containerField.setValue("newValue");
		
		assertThat(containerField.getValue(), is((Object) "newValue"));
	}

	@Test
	public void shouldReturnNotModified() {
		ContainerField containerField = createContainerField();
		assertThat(containerField.isModified(), is(false));
	}
	
	@Test
	public void shouldReturnModified() {
		ContainerField containerField = createContainerField();
		assertThat(containerField.isModified(), is(false));
		
		containerField.setValue("foo");
		assertThat(containerField.isModified(), is(true));
	}

	@Test
	public void shouldBeWritableIfPropertyAndRowAreWritable() {
		ContainerField containerField = createContainerField();
		assertThat(containerField.isReadonly(), is(false));
	}

	abstract ContainerField createContainerField();
}

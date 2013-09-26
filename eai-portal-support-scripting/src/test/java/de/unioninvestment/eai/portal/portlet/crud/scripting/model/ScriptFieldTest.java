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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerClob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;

public class ScriptFieldTest {

	private ScriptField scriptField;

	private String fieldName = "fieldName";
	private String fieldValue = "fieldValue";

	@Mock
	private ContainerField containerFieldMock;

	@Mock
	private ScriptClob scriptClobMock;

	@Mock
	private ScriptBlob scriptBlobMock;

	@Mock
	private ContainerClob containerClobMock;

	@Mock
	private ContainerBlob containerBlobMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(containerFieldMock.getName()).thenReturn(fieldName);
		when(containerFieldMock.getValue()).thenReturn(fieldValue);

		scriptField = new ScriptField(containerFieldMock);
	}

	@Test
	public void shouldGetName() {
		assertThat(scriptField.getName(), is(fieldName));
	}

	@Test
	public void shouldGetValue() {
		assertThat(scriptField.getValue(), is((Object) fieldValue));
	}

	@Test
	public void shouldReturnModified() {
		when(containerFieldMock.isModified()).thenReturn(true);
		assertThat(scriptField.isModified(), is(true));
	}

	@Test
	public void shouldAllowUpdatesOfClobFields() {
		when(scriptClobMock.getContainerClob()).thenReturn(containerClobMock);
		
		scriptField.setValue(scriptClobMock);
		
		verify(containerFieldMock).setValue(containerClobMock);
	}

	@Test
	public void shouldAllowUpdatesOfBlobFields() {
		when(scriptBlobMock.getContainerBlob()).thenReturn(containerBlobMock);
		
		scriptField.setValue(scriptBlobMock);
		
		verify(containerFieldMock).setValue(containerBlobMock);
	}

}
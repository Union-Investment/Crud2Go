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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;

public class ScriptRowTest {

	@Mock
	private ContainerRow rowMock;

	@Mock
	private ContainerRowId rowIdMock;

	Map<String, ContainerField> containerFields = new HashMap<String, ContainerField>();
	@Mock
	private ContainerField containerFieldMock;

	private ScriptRow scriptRow;

	private final String fieldName1 = "field1";;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(containerFieldMock.getName()).thenReturn(fieldName1);
		containerFields.put(fieldName1, containerFieldMock);
		when(rowMock.getFields()).thenReturn(containerFields);

		when(rowMock.getId()).thenReturn(rowIdMock);

		scriptRow = new ScriptRow(rowMock);
	}

	@Test
	public void shouldReturnTheUnderlayingColumnValues() {

		Map<String, Object> values = scriptRow.getValues();
		Set<Map.Entry<String, Object>> entries = values.entrySet();

		assertThat(entries.size(), is(1));
		assertThat(values, notNullValue());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotAllowedToRemoveValues() {
		scriptRow.getValues().remove("123455");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotAllowedToPutAll() {
		scriptRow.getValues().putAll(new HashMap<String, Object>());
	}

	@Test
	public void shouldCloneRowAsWithHisOwnIdentity()
			throws CloneNotSupportedException {

		ContainerRow cloneRowMock = mock(ContainerRow.class);
		when(cloneRowMock.getId()).thenReturn(null);
		when(rowMock.clone()).thenReturn(cloneRowMock);

		ScriptRow scriptRowClone = scriptRow.clone();
		assertThat(scriptRowClone.getId().getContainerRowId(), nullValue());
	}

	@Test
	public void shouldSetScriptFields() {
		Map<String, ScriptField> fields = scriptRow.getFields();

		assertThat(fields, notNullValue());
		assertThat(fields.size(), is(1));
		assertThat(fields.get(fieldName1), notNullValue());
	}

	@Test
	public void shouldReturnModifiedTrue() {
		when(rowMock.isModified()).thenReturn(true);
		when(rowMock.isDeleted()).thenReturn(false);
		when(rowMock.isNewItem()).thenReturn(false);

		assertThat(scriptRow.isModified(), is(true));
	}

	@Test
	public void shouldReturnModifiedFalse() {
		when(rowMock.isModified()).thenReturn(true);
		when(rowMock.isDeleted()).thenReturn(true);
		when(rowMock.isNewItem()).thenReturn(false);

		assertThat(scriptRow.isModified(), is(false));
	}
}

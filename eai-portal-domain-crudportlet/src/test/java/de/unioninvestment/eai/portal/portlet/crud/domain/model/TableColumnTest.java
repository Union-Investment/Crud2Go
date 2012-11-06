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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TableColumnTest {

	private TableColumn tableColumn;

	@Mock
	private ContainerRow rowMock;

	@Mock
	private FieldEditableChecker fieldEditableCheckerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		tableColumn = new TableColumn.Builder().name("name")
				.editableDefault(true).build();
	}

	@Test
	public void shouldCallFieldEditableChecker() {
		tableColumn.setEditableChecker(fieldEditableCheckerMock);

		when(fieldEditableCheckerMock.isEditable(rowMock)).thenReturn(false);

		boolean result = tableColumn.isEditable(rowMock);

		verify(fieldEditableCheckerMock).isEditable(rowMock);
		assertThat(result, is(false));
	}

	@Test
	public void shouldReturnEditableDefault() {
		assertThat(tableColumn.isEditable(rowMock), is(true));
	}
}

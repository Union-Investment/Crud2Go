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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.RowId;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;

public class ContainerClobTest {

	private ContainerClob containerClob;

	@Mock
	private FreeformQueryEventWrapper queryDelegateMock;

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private RowId rowIdMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		when(queryDelegateMock.getCLob(rowIdMock, "Column")).thenReturn(
				"TestClob");
	}

	@Test
	public void shouldGetClobValueAsReader() throws Exception {
		containerClob = new ContainerClob(queryDelegateMock,
				containerRowIdMock, "Column");
		String value = containerClob.getValue();

		assertThat(value, is("TestClob"));
	}

	@Test
	public void shouldModifyClobValue() throws IOException {
		containerClob = new ContainerClob(queryDelegateMock,
				containerRowIdMock, "Column");

		containerClob.setValue("newClob");
		String value = containerClob.getValue();
		assertThat(value, is("newClob"));
		assertThat(containerClob.isModified(), is(true));
	}

	@Test
	public void clobWithoutDataShouldBeNull() {
		containerClob = new ContainerClob();
		int size = containerClob.getSize();
		assertThat(size, is(0));
		assertThat(containerClob.getValue(), is(nullValue()));
	}
}

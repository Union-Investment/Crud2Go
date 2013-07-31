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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.terminal.StreamResource.StreamSource;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;

public class ContainerBlobTest {

	private ContainerBlob containerBlob;

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
		when(queryDelegateMock.getBLob(rowIdMock, "TestBlob")).thenReturn(
				new byte[0]);
		containerBlob = new ContainerBlob(queryDelegateMock,
				containerRowIdMock, "TestBlob");
	}

	@Test
	public void shouldLoadLazyDataFromDb() {

		containerBlob.getValue();

		verify(queryDelegateMock).getBLob(rowIdMock, "TestBlob");

	}

	@Test
	public void shouldBuildLazyStreamSource() {

		StreamSource streamSource = containerBlob.getStreamSource();

		verify(queryDelegateMock, never()).getBLob(rowIdMock, "TestBlob");
		streamSource.getStream();
		verify(queryDelegateMock).getBLob(rowIdMock, "TestBlob");

	}

	@Test
	public void shouldSetNewDataValue() {
		byte[] data = new byte[] { 1, 2 };
		containerBlob.setValue(data);

		assertThat(containerBlob.isEmpty(), is(false));
		assertThat(containerBlob.isModified(), is(true));
		assertThat(containerBlob.getValue(), is(data));
	}

	@Test
	public void shouldBlobIsUnmodifiedAfterCommit() {
		byte[] data = new byte[] { 1, 2 };
		containerBlob.setValue(data);
		containerBlob.commit();
		assertThat(containerBlob.isModified(), is(false));
	}

	@Test
	public void shouldInitializeEmptyBlob() {
		containerBlob = new ContainerBlob();
		assertThat(containerBlob.isEmpty(), is(true));
	}
}

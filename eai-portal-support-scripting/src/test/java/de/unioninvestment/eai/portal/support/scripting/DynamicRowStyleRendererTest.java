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
package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

public class DynamicRowStyleRendererTest {

	@Mock
	private Table tableMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private Closure<?> closureMock;

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private ContainerRow containerRowMock;

	@Captor
	private ArgumentCaptor<ScriptRow> rowCapturer;

	private DynamicRowStyleRenderer rowStyleRenderer;

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(containerMock);
		when(containerMock.getCachedRow(containerRowIdMock, false,
				true)).thenReturn(
				containerRowMock);
		rowStyleRenderer = new DynamicRowStyleRenderer(tableMock, closureMock);
	}

	@Test
	public void shouldGetString() throws Exception {
		letClosureReturn("error");
		String style = rowStyleRenderer.getStyle(containerRowIdMock);
		assertThat(style, is("error"));
	}

	@Test
	public void shouldProvideRowAndColumnNameToClosure() throws Exception {
		letClosureReturn("error");
		rowStyleRenderer.getStyle(containerRowIdMock);

		verify(closureMock).call(rowCapturer.capture());
		assertThat(rowCapturer.getValue(), not(nullValue()));
	}

	@Test
	public void shouldClosureReturnNull() throws Exception {
		letClosureReturn(null);
		String style = rowStyleRenderer.getStyle(containerRowIdMock);

		assertThat(style, is(nullValue()));
	}

	@Test
	public void shouldIgnoreUncachedRows() throws Exception {
		when(containerMock.getCachedRow(containerRowIdMock, false,
				true)).thenReturn(null);

		String style = rowStyleRenderer.getStyle(containerRowIdMock);

		verifyZeroInteractions(closureMock);
		assertThat(style, is(nullValue()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowException() throws Exception {
		letClosureReturn(new Object());
		rowStyleRenderer.getStyle(containerRowIdMock);
	}

	private void letClosureReturn(final Object returnValue) {
		when(closureMock.call(any(ScriptRow.class))).thenAnswer(
				new Answer<Object>() {
					public Object answer(
							org.mockito.invocation.InvocationOnMock invocation)
							throws Throwable {
						return returnValue;
					};
				});
	}
}

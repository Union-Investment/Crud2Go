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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class DynamicOptionListTest {

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

	private SelectionContext context;

	private DynamicOptionList selection;

	@Mock
	private SelectConfig selectConfigMock;

	@Captor
	private ArgumentCaptor<ScriptRow> rowCapturer;

	@Mock
	private EventBus eventBus;

	// fonanteilscheinstammdaten-apdaptor
	@Before
	public void setUp() throws InstantiationException, IllegalAccessException {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(containerMock);
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(
				containerRowMock);
		when(selectConfigMock.getId()).thenReturn("1");
		context = new TableColumnSelectionContext(containerRowIdMock, "COL1");

		selection = new DynamicOptionList(eventBus, tableMock, closureMock,
				selectConfigMock);
	}

	@Test
	public void shouldGetMapOfOptions() throws Exception {
		letClosureReturn(Collections.singletonMap("KEY", "VALUE"));
		Map<String, String> options = selection.getOptions(context);
		assertThat(options.get("KEY"), is("VALUE"));
	}

	@Test
	public void shouldGetMapFromCollection() throws Exception {
		letClosureReturn(Arrays.asList("A", "B", "C"));
		Map<String, String> options = selection.getOptions(context);
		assertThat(options.get("A"), is("A"));
		assertThat(options.keySet(), hasItems("A", "B", "C"));
		assertThat(options.values(), hasItems("A", "B", "C"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetMapFromArraysThrowsException() throws Exception {
		letClosureReturn(new String[] { "ERROR" });
		selection.getOptions(context);
	}

	private void letClosureReturn(final Object returnValue) {
		when(closureMock.call(any(ScriptRow.class), eq("COL1"))).thenAnswer(
				new Answer<Object>() {
					public Object answer(
							org.mockito.invocation.InvocationOnMock invocation)
							throws Throwable {
						return returnValue;
					};
				});
	}

	@Test
	public void shouldProvideRowAndColumnNameToClosure() throws Exception {
		letClosureReturn(Collections.singletonMap("KEY", "VALUE"));
		selection.getOptions(context);

		verify(closureMock).call(rowCapturer.capture(), eq("COL1"));
		assertThat(rowCapturer.getValue(), not(nullValue()));
	}

	@Test
	public void shouldGetTitle() throws Exception {
		letClosureReturn(Collections.singletonMap("KEY", "VALUE"));

		String title = selection.getTitle("KEY", context);

		assertThat(title, is("VALUE"));
	}
	
	@Test
	public void shouldGetKey() throws Exception {
		letClosureReturn(Collections.singletonMap("KEY", "VALUE"));
		
		String key = selection.getKey("VALUE", context);
		
		assertThat(key, is("KEY"));
	}

}

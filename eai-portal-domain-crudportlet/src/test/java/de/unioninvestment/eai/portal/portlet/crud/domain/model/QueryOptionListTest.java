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

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableMap;

import de.unioninvestment.eai.portal.portlet.crud.config.InitializeTypeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.QueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.QueryOptionListRepository;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class QueryOptionListTest {

	private OptionList selection;

	@Mock
	private EventBus eventBusMock;

	@Mock
	private QueryOptionListRepository repositoryMock;

	private SelectConfig config;

	private String query;

	@Mock
	private ExecutorService executorMock;

	@Mock
	private OptionListChangeEventHandler listenerMock;

	@Mock
	private Future<?> futureMock;

	private QueryConfig queryConfig;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		config = new SelectConfig();

		query = "Select a as key, b as title from table";

		queryConfig = new QueryConfig();
		queryConfig.setValue(query);
		config.setQuery(queryConfig);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldSupportQueryForOptions() {
		selection = createDefaultQueryOptionList();

		ImmutableMap<String, String> exampleOptions = ImmutableMap
				.<String, String> builder() //
				.put("key1", "title1") //
				.put("key2", "title").build();

		when(repositoryMock.getOptions("ds", query, false)).thenReturn(
				exampleOptions);

		Iterator<Entry<String, String>> it = selection.getOptions(null)
				.entrySet().iterator();
		assertThat(it.next().getKey(), is("key1"));
		assertThat(it.next().getKey(), is("key2"));
		assertThat(it.hasNext(), is(false));
	}

	private QueryOptionList createDefaultQueryOptionList() {
		return new QueryOptionList(config, eventBusMock, repositoryMock, "ds",
				executorMock, false);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldPrefetchOnLoadWithAsyncConfig() {
		config.getQuery().setInitialize(InitializeTypeConfig.ASYNC);

		createDefaultQueryOptionList();

		verify(executorMock).submit(any(Callable.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldPrefetchOnRefreshWithAsyncConfig()
			throws InterruptedException, ExecutionException {
		config.getQuery().setInitialize(InitializeTypeConfig.ASYNC);
		when(executorMock.submit(any(Callable.class))).thenReturn(futureMock);
		QueryOptionList queryOptionList = createDefaultQueryOptionList();

		queryOptionList.refresh();

		verify(executorMock, times(2)).submit(any(Callable.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldTakeResultOfFutureOnAsyncCall()
			throws InterruptedException, ExecutionException {
		config.getQuery().setInitialize(InitializeTypeConfig.ASYNC);
		when(executorMock.submit(any(Callable.class))).thenReturn(futureMock);
		final QueryOptionList queryOptionList = createDefaultQueryOptionList();

		when(futureMock.get()).thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				queryOptionList.setOptions(singletonMap("FUTURE", "MOCK"));
				return null;
			};
		});

		assertThat(queryOptionList.getOptions(null).get("FUTURE"), is("MOCK"));
	}

	@Test(expected = TechnicalCrudPortletException.class)
	@SuppressWarnings("unchecked")
	public void shouldFailIfFutureFailsWithExecutionException()
			throws InterruptedException, ExecutionException {
		config.getQuery().setInitialize(InitializeTypeConfig.ASYNC);
		when(executorMock.submit(any(Callable.class))).thenReturn(futureMock);
		QueryOptionList queryOptionList = createDefaultQueryOptionList();

		when(futureMock.get()).thenThrow(
				new ExecutionException(new RuntimeException("Test")));

		queryOptionList.getOptions(null);
	}

	@Test
	@SuppressWarnings({ "unchecked", "serial" })
	public void shouldLoadOptionsSynchronouslyIfFutureIsInterrupted()
			throws InterruptedException, ExecutionException {
		config.getQuery().setInitialize(InitializeTypeConfig.ASYNC);
		when(executorMock.submit(any(Callable.class))).thenReturn(futureMock);

		QueryOptionList queryOptionList = new QueryOptionList(config,
				eventBusMock, repositoryMock, "ds", executorMock, false) {
			protected java.util.Map<String, String> loadOptions() {
				return singletonMap("A", "B");
			};
		};

		when(futureMock.get()).thenThrow(new InterruptedException());

		assertThat(queryOptionList.getOptions(null).get("A"), is("B"));
	}

	@SuppressWarnings("serial")
	@Test
	public void shouldRefresh() {
		Map<String, String> options = new HashMap<String, String>();
		options.put("key", "value");

		QueryOptionList queryOptionList = createDefaultQueryOptionList();
		queryOptionList.setOptions(options);

		final List<OptionList> result = new ArrayList<OptionList>();
		queryOptionList.addChangeListener(new OptionListChangeEventHandler() {
			@Override
			public void onOptionListChange(OptionListChangeEvent event) {
				result.add(event.getSource());
			}
		});

		queryOptionList.refresh();

		Map<String, String> storedOptions = queryOptionList.getOptions();

		assertThat(result.size(), is(1));
		assertThat(storedOptions, nullValue());
	}

	@Test
	public void shouldRemoveChangeListener() {
		Map<String, String> options = new HashMap<String, String>();
		options.put("key", "value");

		QueryOptionList queryOptionList = createDefaultQueryOptionList();
		queryOptionList.setOptions(options);

		queryOptionList.addChangeListener(listenerMock);
		queryOptionList.removeChangeListener(listenerMock);
		queryOptionList.refresh();

		verifyZeroInteractions(listenerMock);
	}

	@Test
	public void shouldRefreshFromCacheOnRefreshedEvent() {
		Map<String, String> options = new HashMap<String, String>();
		queryConfig.setCached(true);

		QueryOptionList queryOptionList = createDefaultQueryOptionList();
		queryOptionList.setOptions(options);

		verify(eventBusMock).addHandler(PortletRefreshedEvent.class,
				queryOptionList);

		queryOptionList.onPortletRefresh(null);

		assertThat(queryOptionList.getOptions(), is(nullValue()));
		verifyZeroInteractions(repositoryMock);
	}

	@Test
	public void shoudRefreshFromDatabaseByDefault() {
		Map<String, String> options = new HashMap<String, String>();
		queryConfig.setCached(true);
		
		QueryOptionList queryOptionList = createDefaultQueryOptionList();
		queryOptionList.setOptions(options);

		queryOptionList.refresh();

		verify(repositoryMock).evict("ds", query);
	}

}

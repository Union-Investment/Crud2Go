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

package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.core.RowMapper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;

public class DefaultQueryOptionListRepositoryTest {

	@Mock
	private ConnectionPoolFactory connectionPoolFactoryMock;

	@Mock
	private ConnectionPool connectionPoolMock;

	private Cache cache;

	@Mock
	private ResultSet resultSet;

	private DefaultQueryOptionListRepository repository;

	private String query = "select a as key, b as title from table";

	@Captor
	private ArgumentCaptor<Element> elementCaptor;

	private static CacheManager singletonManager;

	@BeforeClass
	public static void prepareCache() {
		singletonManager = CacheManager.create();
		singletonManager.addCache("testCache");
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(connectionPoolFactoryMock.getPool("ds")).thenReturn(
				connectionPoolMock);

		cache = singletonManager.getCache("testCache");
		cache.removeAll();

		repository = new DefaultQueryOptionListRepository(
				connectionPoolFactoryMock, cache);

		stubDatabaseInteraction();
	}

	@SuppressWarnings("unchecked")
	private void stubDatabaseInteraction() {
		String nullSafeQuery = DefaultQueryOptionListRepository
				.nullSafeQuery(query);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				RowMapper<?> rowmapper = (RowMapper<?>) invocation
						.getArguments()[1];

				when(resultSet.getString("key")).thenReturn("key1");
				when(resultSet.getString("title")).thenReturn("title1");

				rowmapper.mapRow(resultSet, 0);

				when(resultSet.getString("key")).thenReturn("key2");
				when(resultSet.getString("title")).thenReturn("title2");
				rowmapper.mapRow(resultSet, 1);
				return null;
			}
		}).when(connectionPoolMock).executeWithJdbcTemplate(
				Mockito.eq(nullSafeQuery), Mockito.any(RowMapper.class));
	}

	@Test
	public void shouldSupportQueryForOptions() {

		Map<String, String> options = repository.getOptions("ds", query, false);

		Iterator<Entry<String, String>> it = options.entrySet().iterator();
		assertThat(it.next().getKey(), is("key1"));
		assertThat(it.next().getKey(), is("key2"));
		assertThat(it.hasNext(), is(false));
	}

	@Test
	public void shouldSupportFirstQueryForOptionsWithCaching() {

		Map<String, String> options = repository.getOptions("ds", query, true);

		Iterator<Entry<String, String>> it = options.entrySet().iterator();
		assertThat(it.next().getKey(), is("key1"));
		assertThat(it.next().getKey(), is("key2"));
		assertThat(it.hasNext(), is(false));

		assertThat((Map<String, String>) cache.get(createKey(query))
				.getObjectValue(), equalTo(options));
	}

	@Test
	public void shouldDeliverSecondQueryResultFromCache() {

		Map<String, String> options = repository.getOptions("ds", query, true);
		Map<String, String> options2 = repository.getOptions("ds", query, true);

		assertThat(options2, equalTo(options));
		verify(connectionPoolMock, times(1)).executeWithJdbcTemplate(
				Mockito.anyString(), Mockito.any(RowMapper.class));
	}

	@Test
	public void shouldInformAboutExistenceInCacheWithoutLocking() {
		assertThat(repository.isQueryInCache("ds", query), is(false));

		repository.getOptions("ds", query, true);
		assertThat(repository.isQueryInCache("ds", query), is(true));
	}

	private Serializable createKey(String query) {
		return DefaultQueryOptionListRepository.createKey("ds", query);
	}

	@Test
	public void shouldCreateQueryWithoutNullValues() {
		assertThat(
				DefaultQueryOptionListRepository
						.nullSafeQuery("select a as key, b as title from table"),
				is("select key, title from (select a as key, b as title from table) where key is not null and title is not null"));
	}

	@Test
	public void shouldCreateKeyNormalizingWhitespaces() {
		assertThat(DefaultQueryOptionListRepository.createKey("ds",
				"   \t\tselect * \n\t\tfrom x\nwhere 1=2\n"),
				is("ds|select * from x where 1=2"));
	}
}

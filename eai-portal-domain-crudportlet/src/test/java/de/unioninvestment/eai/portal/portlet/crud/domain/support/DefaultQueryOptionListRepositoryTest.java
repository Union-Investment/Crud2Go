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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
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

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;

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
		QueryBuilder.setStringDecorator(DatabaseDialect.ORACLE.getStringDecorator());

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

	@Test(expected=CacheException.class)
	public void shouldHandleExceptionsProperly() {
		when(connectionPoolFactoryMock.getPool("ds")).thenThrow(new IllegalArgumentException("unknown"));

		repository.getOptions("ds", query, true);
	}

	@Test
	public void shouldProperlyHandleSecondRetry() {
		when(connectionPoolFactoryMock.getPool("ds")).thenThrow(new IllegalArgumentException("unknown"));
		try {
			repository.getOptions("ds", query, true);			
		} catch (CacheException e) {
			// expected that
		}
		assertThat(repository.isQueryInCache("ds", query), is(false));
		
		reset(connectionPoolFactoryMock);
		when(connectionPoolFactoryMock.getPool("ds")).thenReturn(
				connectionPoolMock);
		Map<String, String> options = repository.getOptions("ds", query, true);
		
		assertThat(options.size(), is(2));
		assertThat(repository.isQueryInCache("ds", query), is(true));
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
				is("select \"KEY\",\"TITLE\" from (select a as key, b as title from table) opt where \"KEY\" is not null and \"TITLE\" is not null"));
	}

	@Test
	public void shouldCreateKeyNormalizingWhitespaces() {
		assertThat(DefaultQueryOptionListRepository.createKey("ds",
				"   \t\tselect * \n\t\tfrom x\nwhere 1=2\n"),
				is("ds|select * from x where 1=2"));
	}

	@Test
    public void shouldRemoveExactQuery() {
        repository.getOptions("ds", query, true);
        repository.remove("ds", query);
        assertThat(repository.isQueryInCache("ds", query), is(false));
    }

    @Test
    public void shouldRemoveMatchingQueriesByPattern() {
        query = "select a as key, b as title from table t inner join table2 t2 on t1.id = t2.id where t2.name = 'table3'";

        checkRemovalByPattern("ds", "from table", true);
        checkRemovalByPattern("ds", "from t", true);
        checkRemovalByPattern("ds", "\\btable\\b", true);
        checkRemovalByPattern("ds", "\\btable2\\b", true);
        checkRemovalByPattern("ds", "\\btable3\\b", true); // keep that in mind
        checkRemovalByPattern("ds", "(?i)\\bTABLE\\b", true);
        checkRemovalByPattern("ds", "^"+query+"$", true);

        checkRemovalByPattern("otherds", "from table", false);
        checkRemovalByPattern("ds", "FROM TABLE", false);
        checkRemovalByPattern("ds", "\bfrom t\b", false);
    }

    private void checkRemovalByPattern(String dataSource, String pattern, boolean expectRemoval) {
        repository.getOptions("ds", query, true);
        repository.removeAll(dataSource, Pattern.compile(pattern));
        assertThat(repository.isQueryInCache("ds", query), is(!expectRemoval));
    }


}

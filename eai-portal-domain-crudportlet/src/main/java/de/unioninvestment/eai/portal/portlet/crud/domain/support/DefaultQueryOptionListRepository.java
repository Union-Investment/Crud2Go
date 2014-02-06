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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;

@Component
@SuppressWarnings("unchecked")
public class DefaultQueryOptionListRepository implements
		QueryOptionListRepository {

	private static final Logger CACHE_LOGGER = LoggerFactory
			.getLogger("de.unioninvestment.crud2go.optionListCache");

	ConnectionPoolFactory connectionPoolFactory;

	Ehcache underlyingCache;
	BlockingCache cache;

	@Autowired
	public DefaultQueryOptionListRepository(
			ConnectionPoolFactory connectionPoolFactory,
			@Qualifier("optionListCache") Ehcache cache) {
		this.connectionPoolFactory = connectionPoolFactory;
		this.underlyingCache = cache;
		this.cache = new BlockingCache(underlyingCache);
	}

	@Override
	public Map<String, String> getOptions(String dataSource, String query,
			boolean useCache) {
		if (useCache) {
			return getOptionsWithCaching(dataSource, query);
		} else {
			return getOptionsFromDatabase(dataSource, query);
		}
	}

	@Override
	public Map<String, String> getOptionsFromCache(String dataSource,
			String query) {
		String key = createKey(dataSource, query);
		Element element = underlyingCache.get(key);
		return element == null ? null : (Map<String, String>) element.getObjectValue();
	}

	private Map<String, String> getOptionsWithCaching(String dataSource,
			String query) {
		Serializable key = createKey(dataSource, query);
		Element element = cache.get(key); // creates a lock if the element does not exist 
		Map<String, String> options = null;

		if (element == null) {
			try {
				// Value not cached - fetch it
				options = getOptionsFromDatabase(dataSource, query);
				element = new Element(key, options);

				CACHE_LOGGER.debug("Caching option list [{}] ({} elements)",
						key, options.size());

			} catch (final Throwable throwable) {
				// Could not fetch - Ditch the entry from the cache and rethrow
				// release the lock you acquired
				element = new Element(key, null);
				throw new CacheException(
						"Could not fetch object for cache entry with key \""
								+ key + "\".", throwable);

			} finally {
				cache.put(element);
			}
		} else {
			options = (Map<String, String>) element.getObjectValue();
			CACHE_LOGGER.debug("Using cached option list [{}] ({} elements)",
					key, options.size());
		}
		return options;
	}


	static String createKey(String dataSource, String query) {
        String normalizedQuery = normalizeQuery(query);
        return dataSource + "|" + normalizedQuery;
	}

    private static String normalizeQuery(String query) {
        Iterable<String> lines = Splitter.on('\n').trimResults()
                .omitEmptyStrings().split(query);
        return Joiner.on(' ').join(lines);
    }

    private Map<String, String> getOptionsFromDatabase(String dataSource,
			String query) {
		ConnectionPool pool = connectionPoolFactory.getPool(dataSource);

		String nullSafeQuery = nullSafeQuery(query);
		final LinkedHashMap<String, String> newOptions = new LinkedHashMap<String, String>();
		pool.executeWithJdbcTemplate(nullSafeQuery, new RowMapper<Object>() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				newOptions.put(rs.getString("key"), rs.getString("title"));
				return null;
			}
		});

		return newOptions;
	}

	/**
	 * Sorgt dafür, dass keine leeren Keys oder Values in der Liste stehen.
	 * 
	 * @param query
	 *            Abfrage
	 * @return NULL-Sichere-Abfrage
	 */
	static String nullSafeQuery(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("select key, title from (");
		sb.append(query);
		sb.append(") where key is not null and title is not null");
		return sb.toString();
	}

	@Override
	public boolean isQueryInCache(String dataSource, String query) {
		return cache.isKeyInCache(createKey(dataSource, query));
	}

	@Override
	public boolean remove(String datasSource, String query) {
		String key = createKey(datasSource, query);
		boolean removed = cache.remove(key);
		if (removed) {
			CACHE_LOGGER.debug("Removed option list from cache [{}]", key);
		}
		return removed;
	}

    public int removeAll(String dataSource, Pattern pattern) {
        List<Object> keysToRemove = findAllMatchingKeys(dataSource, pattern);
        int count = 0;
        for (Object cacheKey : keysToRemove) {
            boolean removed = cache.remove(cacheKey);
            if (removed) {
            	count++;
                CACHE_LOGGER.debug("Removed option list from cache [{}]", cacheKey);
            }
        }
        return count;
    }

    List<Object> findAllMatchingKeys(String dataSource, Pattern pattern) {
        List<Object> cacheKeys = cache.getKeys();
        List<Object> keysToRemove = new LinkedList<Object>();
        for(Object cacheKey : cacheKeys) {
            Iterator<String> spliterator = Splitter.on('|').split((String) cacheKey).iterator();
            String ds = spliterator.next();
            if (ds.equals(dataSource)) {
                String query = spliterator.next();
                Matcher matcher = pattern.matcher(query);
                if (matcher.find()) {
                    keysToRemove.add(cacheKey);
                }
            }
        }
        return keysToRemove;
    }
}

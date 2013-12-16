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

import java.util.Map;

/**
 * Cached Repository for OptionList queries.
 * 
 * @author cmj
 */
public interface QueryOptionListRepository {

	/**
	 * This query
	 * 
	 * @param dataSource
	 *            the dataSource shortname
	 * @param query
	 *            the query to execute
	 * @param useCache
	 *            tells the repository if caching should be done
	 * @return the query results
	 */
	Map<String, String> getOptions(String dataSource, String query,
			boolean useCache);

	/**
	 * @param dataSource
	 *            the dataSource shortname
	 * @param query
	 *            the query
	 * @return the query results or <code>null</code>, if the cache contains no
	 *         entry
	 */
	Map<String, String> getOptionsFromCache(String dataSource, String query);

	/**
	 * 
	 * @param dataSource
	 * @param query
	 * @return <code>true</code> if the results for the query are currently in
	 *         cache
	 */
	boolean isQueryInCache(String dataSource, String query);

	/**
	 * Removes the query results from the cache.
	 * 
	 * @param dataSource
	 *            the dataSource shortname
	 * @param query
	 *            the query
	 */
	void remove(String dataSource, String query);

}

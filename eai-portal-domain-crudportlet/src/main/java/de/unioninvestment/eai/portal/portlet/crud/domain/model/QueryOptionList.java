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

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.InitializeTypeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.QueryOptionListRepository;
import de.unioninvestment.eai.portal.support.vaadin.context.BackgroundThreadContextProvider;
import de.unioninvestment.eai.portal.support.vaadin.context.ContextualCallable;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * 
 * Modell-Klasse für Auswahl-Boxen. Sofern die Query mit
 * <code>initialize="async"</code> konfiguriert wurde, wird bei der
 * Initialisierung und beim Refresh die Optionsliste asynchron vorgeladen.
 * 
 * @author max.hartmann
 * @author carsten.mjartan
 * 
 */
public class QueryOptionList extends VolatileOptionList {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QueryOptionList.class);

	private volatile Future<Map<String, String>> future;
	private volatile Map<String, String> options;

	private String query;
	private Object lock = new Object();
	private String id;
	private boolean lazy;
	private boolean prefetched;
	private boolean useCache;

	private final ExecutorService prefetchExecutor;

	private QueryOptionListRepository repository;

	private String dataSource;

	/**
	 * Konstruktor mit Parametern. Wird verwendet, wenn die Optionen aus der
	 * Daten Datenbank gelesen werden sollen.
	 * 
	 * @param config
	 *            Konfiguration der Auswahl-Box
	 * @param eventBus
	 * @param repository
	 *            repository for query retrieval which also provides caching
	 *            functionality
	 * @param asyncExecutor
	 *            executes the prefetch operation, if configured
	 */
	public QueryOptionList(SelectConfig config, EventBus eventBus,
			QueryOptionListRepository repository, String dataSource,
			ExecutorService asyncExecutor, boolean useCacheByDefault) {
		super(eventBus);
		this.dataSource = dataSource;
		this.prefetchExecutor = asyncExecutor;
		this.repository = repository;

		this.id = config.getId();
		this.query = config.getQuery().getValue();

		this.useCache = config.getQuery().isCached() == null ? useCacheByDefault
				: config.getQuery().isCached();

		InitializeTypeConfig initialize = config.getQuery().getInitialize();
		this.lazy = initialize.equals(InitializeTypeConfig.LAZY)
				|| initialize.equals(InitializeTypeConfig.ASYNC);
		this.prefetched = initialize.equals(InitializeTypeConfig.ASYNC);

		if (useCache) {
			options = repository.getOptionsFromCache(dataSource, query);
		}
		if (options == null && prefetched) {
			startPrefetch();
		}
	}

	private void startPrefetch() {
		cancelOlderPrefetch();
		logIfThreadPoolIsFull(prefetchExecutor);
		BackgroundThreadContextProvider provider =  new BackgroundThreadContextProvider();
		future = prefetchExecutor.submit(new ContextualCallable<Map<String, String>>(provider) {
			@Override
			public Map<String, String> callWithContext() {
				LOGGER.debug("Prefetching option list {}", logId());
				synchronized (lock) {
					options = loadOptions();
					fireChangeEvent(true);
					return options;
				}
			}

		});
	}

	private void logIfThreadPoolIsFull(ExecutorService prefetchExecutor) {
		if (prefetchExecutor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor executor = (ThreadPoolExecutor) prefetchExecutor;
			int requestsInProgress = executor.getActiveCount()
					+ executor.getQueue().size();
			if (requestsInProgress >= executor.getMaximumPoolSize()) {
				LOGGER.warn(
						"Maximum OptionList prefetch threads reached (~{}/{}). Request will be queued.",
						requestsInProgress, executor.getMaximumPoolSize());
			}
		}
	}

	private String logId() {
		return id != null ? "'" + id + "'" : "";
	}

	private void cancelOlderPrefetch() {
		Future<Map<String, String>> currentFuture = future;
		if (currentFuture != null) {
			currentFuture.cancel(true);
		}
		future = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getOptions(SelectionContext context) {
		synchronized (lock) {
			if (options == null) {
				if (future != null) {
					waitForOptionsFromFuture();
					future = null;
				}
				if (options == null) {
					options = loadOptions();
				}
			}
			return options;
		}
	}

	private void waitForOptionsFromFuture() {
		try {
			future.get();

		} catch (InterruptedException e) {
			// Task interrupted (by refresh?) => continue reading
			// synchronously

		} catch (ExecutionException e) {
			throw new TechnicalCrudPortletException(
					"Error acquiring result of prefetch operation",
					e.getCause());
		}
	}

	/**
	 * 
	 * Nur für UnitTests Liefert die Einträge. Beachten, dass die Werte nicht
	 * aus der DB geladen werden.
	 * 
	 * @return Einträge im Objekt
	 */
	Map<String, String> getOptions() {
		return options;
	}

	protected Map<String, String> loadOptions() {
		LOGGER.debug("Loading option list {}", logId());
		long startTime = System.currentTimeMillis();

		Map<String, String> newOptions = repository.getOptions(dataSource,
				query, useCache);

		long duration = System.currentTimeMillis() - startTime;
		LOGGER.debug("Finished loading option list {} ({}ms)", logId(),
				duration);
		return newOptions;
	}

	/**
	 * Entfernt die gepufferten Werte.
	 */
	@Override
	public void refresh(RefreshPolicy policy) {
		synchronized (lock) {
			if (policy == RefreshPolicy.FROM_SOURCE && useCache) {
				repository.remove(dataSource, query);
			}
			options = null;
			if (prefetched) {
				startPrefetch();
			} else {
				fireChangeEvent(false);
			}
		}
	}

	public String getId() {
		return id;
	}

	/**
	 * Information für das Frontend, wann die Optionsliste aus der DB zu laden
	 * ist.
	 */
	@Override
	public boolean isLazy() {
		return lazy;
	}

	@Override
	public boolean isInitialized() {
		return options != null;
	}

	/**
	 * @param options
	 *            for testing
	 */
	void setOptions(Map<String, String> options) {
		this.options = options;
	}

	/**
	 * @return if the portlet is configured to be prefetched
	 */
	public boolean isPrefetched() {
		return prefetched;
	}

}

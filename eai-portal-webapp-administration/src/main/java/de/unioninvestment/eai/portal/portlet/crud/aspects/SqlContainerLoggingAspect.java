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
package de.unioninvestment.eai.portal.portlet.crud.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Aspekt für SQLContainer-interne Aufrufe auf Queries. Aufrufe auf
 * getResults() bzw. getCount() werden bei über 100ms Laufzeit im Info-Level
 * geloggt, bei schnelleren Aufrufen im DEBUG-Level.
 * 
 * @author carsten.mjartan
 */
@Aspect
public class SqlContainerLoggingAspect {

	static Logger LOG = LoggerFactory
			.getLogger(SqlContainerLoggingAspect.class);

	static final long SLOW_CALL_DURATION_MS = 100;

	/**
	 * Leerer Konstruktor.
	 */
	@SuppressWarnings("all")
	public SqlContainerLoggingAspect() {
		// empty constructor
	}

	/**
	 * @param pjp
	 *            die eigentlich aufgerufene Methode
	 * @param windowId
	 *            die WindowID des aktuellen Portlets
	 * @return eine gecachete oder neu gelesene PortletConfig Instanz
	 * @throws Throwable
	 *             bei Fehler
	 */
	@Around("execution(public * com.vaadin.addon.sqlcontainer.query.QueryDelegate.getCount())")
	public Object logCountQueries(ProceedingJoinPoint pjp) throws Throwable {
		LOG.info("querying database for row count");
		return runAndLogDuration(pjp);
	}

	@Around("execution(public * com.vaadin.addon.sqlcontainer.query.QueryDelegate.getResults(..))")
	public Object logResultQueries(ProceedingJoinPoint pjp) throws Throwable {
		return runAndLogDuration(pjp);
	}

	private Object runAndLogDuration(ProceedingJoinPoint pjp) throws Throwable {
		if (LOG.isInfoEnabled()) {
			long startTime = System.currentTimeMillis();
			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Starting " + pjp.toShortString());
				}
				return pjp.proceed();
			} finally {
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				if (duration > SLOW_CALL_DURATION_MS) {
					LOG.info("Leaving " + pjp.toShortString() + " (" + duration
							+ "ms)");
				} else if (LOG.isDebugEnabled()) {
					LOG.debug("Leaving " + pjp.toShortString() + " ("
							+ duration + "ms)");
				}
			}
		} else {
			return pjp.proceed();
		}
	}

	@Around("execution(public * com.vaadin.addon.sqlcontainer.SQLContainer.*(..)) && !execution(public * com.vaadin.addon.sqlcontainer.SQLContainer.getType(..))")
	public Object logContainerCalls(ProceedingJoinPoint pjp) throws Throwable {
		return runAndLogDuration(pjp);
	}

}

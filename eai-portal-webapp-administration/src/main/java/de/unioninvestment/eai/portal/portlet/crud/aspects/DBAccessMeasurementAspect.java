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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.server.VaadinPortletService;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortletService;
import de.unioninvestment.eai.portal.support.vaadin.RequestProcessingInfo;

/**
 * Logging Aspekt für SQLContainer-interne Aufrufe auf Queries. Aufrufe auf
 * getResults() bzw. getCount() werden bei über 100ms Laufzeit im Info-Level
 * geloggt, bei schnelleren Aufrufen im DEBUG-Level.
 * 
 * @author carsten.mjartan
 */
@Aspect
@Configurable
public class DBAccessMeasurementAspect {

	@Autowired
	private Settings settings;

	/**
	 * Leerer Konstruktor.
	 */
	@SuppressWarnings("all")
	public DBAccessMeasurementAspect() {
		// empty constructor
	}

	/**
	 * @param pjp
	 *            die eigentlich aufgerufene Methode
	 * @return eine gecachete oder neu gelesene PortletConfig Instanz
	 * @throws Throwable
	 *             bei Fehler
	 */
	@Around("execution(public * com.vaadin.data.util.sqlcontainer.query.QueryDelegate.getCount())")
	public Object measureCountQueries(ProceedingJoinPoint pjp) throws Throwable {
		return measureAndCallMethod(pjp);
	}

	@Around("execution(public * com.vaadin.data.util.sqlcontainer.query.QueryDelegate.getResults(..))")
	public Object measureResultQueries(ProceedingJoinPoint pjp)
			throws Throwable {
		return measureAndCallMethod(pjp);
	}

	@Around("execution(public * com.vaadin.data.util.sqlcontainer.SQLContainer.*(..)) && !execution(public * com.vaadin.data.util.sqlcontainer.SQLContainer.getType(..))")
	public Object measureContainerCalls(ProceedingJoinPoint pjp)
			throws Throwable {
		return measureAndCallMethod(pjp);
	}

	@Around("execution(protected * de.unioninvestment.eai.portal.portlet.crud.domain.model.QueryOptionList.loadOptions())")
	public Object measureLoadingOfOptionList(ProceedingJoinPoint pjp)
			throws Throwable {
		return measureAndCallMethod(pjp);
	}

	private Object measureAndCallMethod(ProceedingJoinPoint pjp)
			throws Throwable {
		if (settings.isDisplayRequestProcessingInfo()) {
			CrudVaadinPortletService service = (CrudVaadinPortletService) VaadinPortletService
					.getCurrent();
			if (service != null) {
				RequestProcessingInfo info = service
						.getCurrentRequestProcessingInfo();
				if (info != null) {
					try {
						info.startMeasuring("db");
						return pjp.proceed();
					} finally {
						info.stopMeasuring("db");
					}
				}
			}
		}
		return pjp.proceed();
	}

}

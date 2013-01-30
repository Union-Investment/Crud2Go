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

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspekt zur Einhaltung der System-Architektur.
 * 
 * @author max.hartmann
 * 
 */
@Aspect
public class SystemArchitecture {

	/**
	 * innerhalb des CRUD-Applikations-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud..*)")
	public void inCrudPortlet() {
		// pointcut specification
	}

	/**
	 * innerhalb des View-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.mvp.views..*)")
	public void inMvpViews() {
		// pointcut specification
	}

	/**
	 * innerhalb des Presenter-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.mvp.presenters..*)")
	public void inMvpPresenters() {
		// pointcut specification
	}

	/**
	 * innerhalb des Model-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.mvp.model..*)")
	public void inMvpModel() {
		// pointcut specification
	}

	/**
	 * innerhalb des Event-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.mvp.events..*)")
	public void inMvpEvents() {
		// pointcut specification
	}

	/**
	 * innerhalb des MVP-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.mvp..*)")
	public void inMvpLayer() {
		// pointcut specification
	}

	/**
	 * innerhalb des Service-Layer-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.services..*)")
	public void inServiceLayer() {
		// pointcut specification
	}

	/**
	 * innerhalb des Persistence-Layer-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.persistence..*)")
	public void inPersistenceLayer() {
		// pointcut specification
	}

	/**
	 * innerhalb der Test-Packages.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal..*Test)")
	public void inTest() {
		// pointcut specification
	}

	/**
	 * Im ConfigurationService.
	 */
	@Pointcut("within(de.unioninvestment.eai.portal..*Service)")
	public void inConfigurationService() {
		// pointcut specification
	}

	@Pointcut("within(de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction)")
	public void inSearchFormAction() {
		// pointcut specification
	}
}
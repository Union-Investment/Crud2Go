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
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspekt zur Einhaltung von Architektur-Richtlinien.
 * 
 * @author max.hartmann
 * 
 */
@Aspect
public class ArchitecturalConstraints {

	@DeclareError("SystemArchitecture.inMvpViews() && accessMvpPresenters()")
	static final String DISALLOW_VIEWS_TO_PRESENTERS = "views are not allowed to access the presenter layer";

	@DeclareError("SystemArchitecture.inMvpModel() && accessMvpPresenters()")
	static final String DISALLOW_MODEL_TO_PRESENTERS = "model classes are not allowed to access the presenter layer";

	@DeclareWarning("SystemArchitecture.inMvpLayer() && accessSqlContainer()")
	static final String DISALLOW_MVP_TO_SQLCONTAINER = "mvp layer is not allowed to access the SQLContainer specific classes";

	@DeclareError("SystemArchitecture.inServiceLayer() && accessMvpLayer()")
	static final String DISALLOW_SERVICES_TO_MVP = "service layer is not allowed to access the MVP layer";

	@DeclareError("SystemArchitecture.inPersistenceLayer() && accessMvpLayer()")
	static final String DISALLOW_PERSISTENCE_TO_MVP = "persistence layer is not allowed to access the MVP layer";

	@DeclareError("SystemArchitecture.inPersistenceLayer() && accessServiceLayer()")
	static final String DISALLOW_PERSISTENCE_TO_SERVICES = "persistence layer is not allowed to access the services layer";

	@DeclareError("SystemArchitecture.inCrudPortlet() "
			+ "&& !SystemArchitecture.inTest() "
			+ "&& !SystemArchitecture.inConfigurationService() "
			+ "&& !SystemArchitecture.inSearchFormAction() && accessConfigSetter()")
	static final String DISALLOW_CHANGING_CONFIGURATION = "code is not allowed to change config settings, because they are shared between sessions";

	/**
	 * Aufruf von Konfigurations-Settern.
	 */
	@Pointcut("call(* (de.unioninvestment.eai.portal.portlet.crud.config..*).set*(*))")
	public void accessConfigSetter() {
		// pointcut specification
	}

	/**
	 * Aufruf von Presenter-Klassen.
	 */
	@Pointcut("call(* de.unioninvestment.eai.portal.portlet.crud.mvp.presenters..*.*(..))")
	public void accessMvpPresenters() {
		// pointcut specification
	}

	/**
	 * Aufruf von View-Klassen.
	 */
	@Pointcut("call(* (de.unioninvestment.eai.portal.portlet.crud.mvp.views..*).*(..))")
	public void accessMvpViews() {
		// pointcut specification
	}

	/**
	 * Aufruf von MVP-Klassen.
	 */
	@Pointcut("call(* (de.unioninvestment.eai.portal.portlet.crud.mvp..*).*(..))")
	public void accessMvpLayer() {
		// pointcut specification
	}

	/**
	 * Aufruf von Service-Layer-Klassen.
	 */
	@Pointcut("call(* (de.unioninvestment.eai.portal.portlet.crud.services..*).*(..))")
	public void accessServiceLayer() {
		// pointcut specification
	}

	/**
	 * Aufruf von Vaadin SQL-Container Klassen
	 */
	@Pointcut("call(* (com.vaadin.addon.sqlcontainer..*).*(..))")
	public void accessSqlContainer() {
		// pointcut specification
	}

}

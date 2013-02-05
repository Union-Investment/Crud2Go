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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponentGenerator;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder;

/**
 * Scripting-Implementierung zur Erzeugung von Vaadin-{@link Component}s.
 * 
 * @author bastian.spanneberg
 * 
 */
public class CustomComponentGeneratorImpl implements CustomComponentGenerator {

	private final Closure<Object> closure;
	private final PortletApplication application;

	/**
	 * Konstruktor.
	 * 
	 * @param closure
	 *            die {@link Closure} zur Erzeugung der Komponente
	 * @param application
	 */
	public CustomComponentGeneratorImpl(Closure<Object> closure,
			PortletApplication application) {
		this.closure = closure;
		this.application = application;
	}

	@Override
	public Component generate() {
		return (Component) closure.call(new VaadinBuilder(application));
	}
}

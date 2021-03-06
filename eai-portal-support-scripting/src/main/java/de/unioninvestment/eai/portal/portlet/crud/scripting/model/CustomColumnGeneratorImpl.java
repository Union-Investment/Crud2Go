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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomColumnGenerator;
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder;

/**
 * Generator-Implementierung, die an das Scripting delegiert.
 * 
 * @author markus.bonsch
 * 
 */
public class CustomColumnGeneratorImpl implements CustomColumnGenerator {

	private final Closure<Object> closure;

	/**
	 * @param closure
	 *            Closure, die für die Generierung der Vaadin-Komponenten
	 *            verwendet werden soll
	 */
	public CustomColumnGeneratorImpl(Closure<Object> closure) {
		this.closure = closure;
	}

	@Override
	public Component generate(ContainerRow row) {
		ScriptRow scriptRow = new ScriptRow(row);
		Object component = closure.call(scriptRow, new VaadinBuilder());
		return (Component) component;
	}

}

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
package de.unioninvestment.eai.portal.support.scripting.web;

import groovy.lang.Closure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.codehaus.groovy.runtime.GroovyCategorySupport;

import de.unioninvestment.eai.portal.portlet.crud.scripting.category.CollectionsCategory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.category.DateCategory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.category.GStringCategory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.category.StringCategory;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortlet;

/**
 * Erweitert bestehende Klassen um Methoden, die in Groovy verwendet werden
 * können.
 * 
 * @author eugen.melnichuk
 * @author bastian.kr0l
 */
public class GroovyCategorySpringApplicationPortlet extends CrudVaadinPortlet {

	/**
	 * Liste mit Klassen, die zusätzliche Methoden enthalten.
	 */
	@SuppressWarnings("rawtypes")
	static private List<Class> categories = new ArrayList<Class>();
	static {
		categories.add(CollectionsCategory.class);
		categories.add(GStringCategory.class);
		categories.add(StringCategory.class);
		categories.add(DateCategory.class);
	}

	@Override
	protected void handleRequest(final PortletRequest request,
			final PortletResponse response) throws PortletException,
			IOException {
		GroovyCategorySupport.use(categories, new Closure<Object>(null) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void doCall() throws PortletException, IOException {
				GroovyCategorySpringApplicationPortlet.super.handleRequest(
						request, response);
			}
		});
	}
}

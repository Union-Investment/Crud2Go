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
package de.unioninvestment.eai.portal.support.scripting;

import groovy.lang.Closure;

import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterFactory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormAction;

/**
 * Factoryklasse für SQLFilter.
 * 
 */
public class ScriptCustomFilterFactory implements CustomFilterFactory {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(ScriptCustomFilterFactory.class);

	private final ScriptFormAction formAction;

	private final ScriptBuilder scriptBuilder;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param mainScript
	 *            Hauptscript
	 * @param formAction
	 *            Script-FormAction Model
	 */
	public ScriptCustomFilterFactory(ScriptBuilder scriptBuilder,
			ScriptFormAction formAction) {
		this.formAction = formAction;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CustomFilter createCustomFilter(CustomFilterConfig config) {
		Closure<Object> filterClosure = scriptBuilder
				.buildClosure(config.getFilter());

		return new CustomFilter(new ScriptCustomFilterMatcher(filterClosure),
				false);
	}

}

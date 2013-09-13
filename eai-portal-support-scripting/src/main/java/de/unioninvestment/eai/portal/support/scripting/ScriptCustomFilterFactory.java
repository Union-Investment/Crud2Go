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
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterFactory;

/**
 * Factoryklasse für SQLFilter.
 * 
 */
public class ScriptCustomFilterFactory implements CustomFilterFactory {

	private final ScriptBuilder scriptBuilder;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param mainScript
	 *            Hauptscript
	 * @param formAction
	 *            Script-FormAction Model
	 */
	public ScriptCustomFilterFactory(ScriptBuilder scriptBuilder) {
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	public CustomFilter createCustomFilter(CustomFilterConfig config) {
		Closure<Object> filterClosure = scriptBuilder
				.buildClosure(config.getFilter());

		return new CustomFilter(new ScriptCustomFilterMatcher(filterClosure),
				false);
	}

}

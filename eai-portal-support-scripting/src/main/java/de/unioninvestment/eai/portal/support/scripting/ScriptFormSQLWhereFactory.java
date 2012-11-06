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
import groovy.lang.GString;
import groovy.lang.Script;

import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLWhereFactory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormAction;

/**
 * Factoryklasse für SQLFilter.
 * 
 */
public class ScriptFormSQLWhereFactory extends ScriptSQLWhereFactory implements
		SQLWhereFactory {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(ScriptFormSQLWhereFactory.class);

	private final Script mainScript;
	private final ScriptFormAction formAction;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param mainScript
	 *            Hauptscript
	 * @param formAction
	 *            Script-FormAction Model
	 */
	public ScriptFormSQLWhereFactory(Script mainScript,
			ScriptFormAction formAction) {
		this.mainScript = mainScript;
		this.formAction = formAction;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SQLFilter createFilter(String column, String where) {
		String closureString = "{ fields -> "
				+ multilineGStringExpression(where) + " }";
		Closure<?> whereClosure;
		try {
			whereClosure = (Closure<GString>) new ScriptCompiler()
					.compileScript(closureString).newInstance().run();
		} catch (Exception e) {
			LOG.error("Error compiling script", e);
			throw new BusinessException("portlet.crud.error.compilingScript");
		}
		whereClosure.setDelegate(mainScript);
		GString result = (GString) whereClosure.call(formAction.getForm()
				.getFields());

		return createFilter(column, result, false);
	}

	private static String multilineGStringExpression(String sqlString) {
		return sqlString == null ? null : "\"\"\""
				+ sqlString.replace("\"\"\"", "\\\"\\\"\\\"") + "\"\"\"";
	}

}

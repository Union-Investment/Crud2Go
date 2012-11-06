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
import de.unioninvestment.eai.portal.portlet.crud.config.StatementConfig;

/**
 * Wrapper Klasse der SQL Konfigurationselemente.
 * 
 * @author markus.bonsch
 * 
 */
public class StatementWrapper {

	private final Closure<?> statementClosure;
	private final StatementConfig.Type type;

	public StatementWrapper(Closure<?> statementClosure, StatementConfig.Type type) {
		this.statementClosure = statementClosure;
		this.type = type;
	}
	
	public Closure<?> getStatementClosure() {
		return statementClosure;
	}
	
	public StatementConfig.Type getType() {
		return type;
	}
}

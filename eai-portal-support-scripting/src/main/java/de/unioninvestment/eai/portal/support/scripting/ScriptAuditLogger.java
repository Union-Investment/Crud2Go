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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.AuditLogger;

/**
 * 
 * Audit Logger für Datenmanipulationen aus dem Script Code.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptAuditLogger extends Closure<String> {

	private static final long serialVersionUID = 42L;

	static org.slf4j.Logger logger = LoggerFactory
			.getLogger("de.uit.eai.portal.crud.auditLogger");

	private AuditLogger domainAuditLogger;

	/**
	 * Konstruktor.
	 * 
	 * @param owner
	 *            Closure Kontext
	 * @param currentUser
	 *            Aktueller Benutzer
	 */
	public ScriptAuditLogger(Object owner, CurrentUser currentUser) {
		super(owner);
		domainAuditLogger = new AuditLogger(currentUser);
	}

	/**
	 * Schreibt eine Info-Logmeldung in den Audit Appender.
	 * 
	 * @param meldung
	 *            Logmeldung
	 */
	public void doCall(String meldung) {
		domainAuditLogger.audit(meldung);
	}
}

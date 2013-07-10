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
package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;

/**
 * 
 * @author nicolai.woeller
 * 
 */
public class AuditLogger {

	private static final long serialVersionUID = 42L;

	private org.slf4j.Logger logger;

	private String currentUserName;

	/**
	 * Konstruktor.
	 * 
	 * @param owner
	 *            Closure Kontext
	 * @param currentUser
	 *            Aktueller Benutzer
	 */
	public AuditLogger(CurrentUser currentUser) {
		this.currentUserName = isNamedUser(currentUser) ? currentUser.getName()
				: "Anonymous";
		LiferayUI currentApplication = LiferayUI
				.getCurrent();
		long communityId = -1;
		if (currentApplication != null) {
			communityId = currentApplication.getCommunityId();
		}
		logger = LoggerFactory.getLogger("de.uit.eai.portal.crud.auditLogger."
				+ communityId);
	}

	private boolean isNamedUser(CurrentUser currentUser) {
		return currentUser != null && currentUser.isAuthenticated();
	}

	/**
	 * Schreibt eine Info-Logmeldung in den Audit Appender.
	 * 
	 * @param meldung
	 *            Logmeldung
	 */
	public void audit(String meldung) {
		logger.info("Benutzer <" + currentUserName + "> - " + meldung);
	}

	/**
	 * Schreibt eine JMX-Info-Logmeldung in den Audit Appender.
	 * 
	 * @param server
	 *            JMX Server Name
	 * @param mbeanName
	 *            MBean Name
	 * @param propertyId
	 *            Id der JMX Property
	 * @param itemProperty
	 *            JMX Property
	 */
	public void auditMBeanPropertyChange(String server, String mbeanName, String propertyId,
			GenericProperty itemProperty) {
		String itemPropertyValue = "";
		if (itemProperty.getValue() != null) {
			itemPropertyValue = itemProperty.getValue().toString();
		}
		logger.info("Benutzer <" + currentUserName + "> - "
				+ "Server <" + server + "> - "
				+ "MBean-Name <" + mbeanName + "> - "
				+ "Property ID <" + propertyId + "> - "
				+ "Property-Wert <" + itemPropertyValue + ">");
	}

	/**
	 * Logs a ReST request without payload body.
	 * 
	 * @param requestMethod
	 *            PUT,GET,DELETE,...
	 * @param url
	 *            the target url
	 * @param statusLine
	 *            the response statusline
	 */
	public void auditReSTRequest(String requestMethod, String url,
			String statusLine) {
		auditReSTRequest(requestMethod, url, null, statusLine);
	}

	/**
	 * Logs a ReST request with payload body.
	 * 
	 * @param requestMethod
	 *            PUT,GET,DELETE,...
	 * @param url
	 *            the target url
	 * @param body
	 *            the payload body
	 * @param statusLine
	 *            the response statusline
	 */
	public void auditReSTRequest(String requestMethod, String url,
			String body, String statusLine) {
		logger.info("Benutzer <" + currentUserName + "> - "
				+ "Method <" + requestMethod + "> - "
				+ "URL <" + url + "> - "
				+ "Status <" + statusLine + ">"
				+ (body != null ? ("\n" + body) : ""));
	}

}

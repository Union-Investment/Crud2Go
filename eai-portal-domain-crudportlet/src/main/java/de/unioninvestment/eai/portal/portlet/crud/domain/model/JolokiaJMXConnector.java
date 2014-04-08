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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.io.IOException;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;

import org.jolokia.client.J4pClient;

/**
 * Jolokia-Konnektor. Stellt eine Verbindung zu dem Jolokia-Agent her.
 * 
 * @author polina.vinarski
 * 
 */
public class JolokiaJMXConnector implements JMXConnector {

	private String url = "";
	private JolokiaMBeanServerConnection connection;

	/**
	 * Stellt eine Verbindung zu dem Jolokia-Agent her.
	 */
	@Override
	public void connect() throws IOException {
		J4pClient j4pClient = new J4pClient(getUrl());
		JolokiaMBeanServerConnection connection = new JolokiaMBeanServerConnection();
		connection.setJ4pClient(j4pClient);
		setConnection(connection);
	}

	/**
	 * Gibt die Verbindung zu dem Jolokia-Agent als
	 * MBeanServerConnection-Objektrepräsentation zurück.
	 * 
	 * @return die Verbindung zu dem Jolokia-Agent
	 */
	@Override
	public MBeanServerConnection getMBeanServerConnection() throws IOException {
		return getConnection();
	}

	/**
	 * Setzt die Jolokia-Url fest.
	 * 
	 * @param url
	 *            Jolokia-Url
	 */
	public void setUrl(String url) {
		this.url = url;

	}

	/**
	 * Gibt die Jolokia-Url zurück.
	 * 
	 * @return Jolokia-Url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Gibt eine Verbindung zu dem Jolokia-Agent zurück.
	 * 
	 * @return Verbindung zu dem Jolokia-Agent
	 */
	public JolokiaMBeanServerConnection getConnection() {
		return connection;
	}

	/**
	 * Setzt die Verbindung zu dem Jolokia-Agent fest.
	 * 
	 * @param connection
	 *            Verbindung zu dem Jolokia-Agent
	 */
	public void setConnection(JolokiaMBeanServerConnection connection) {
		this.connection = connection;
	}

	// Diese Methoden werden aktuell nicht benötigt

	@Override
	public void addConnectionNotificationListener(
			NotificationListener listener, NotificationFilter filter,
			Object handback) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void connect(Map<String, ?> env) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public String getConnectionId() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public MBeanServerConnection getMBeanServerConnection(
			Subject delegationSubject) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeConnectionNotificationListener(
			NotificationListener listener) throws ListenerNotFoundException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeConnectionNotificationListener(NotificationListener l,
			NotificationFilter f, Object handback)
			throws ListenerNotFoundException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

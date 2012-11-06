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

import groovy.util.GroovyMBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.JMXWrapper;

/**
 * 
 * Wrapperklasse für den Zugriff auf JMX aus einem Script.
 * 
 */
public class ScriptJMXWrapper {

	private JMXWrapper jmxWrapper;

	/**
	 * Konstruktor.
	 * 
	 * @param connectionArgs
	 *            Parameter der Verbindung
	 */
	public ScriptJMXWrapper(@SuppressWarnings("rawtypes") Map connectionArgs) {
		jmxWrapper = new JMXWrapper(connectionArgs);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param serverAdress
	 *            Server
	 * @param serverPort
	 *            Port
	 */
	public ScriptJMXWrapper(String serverAdress, String serverPort) {
		jmxWrapper = new JMXWrapper(serverAdress, serverPort);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param serverAndPort
	 *            Server und Port
	 */
	public ScriptJMXWrapper(String serverAndPort) {
		jmxWrapper = new JMXWrapper(serverAndPort);
	}

	public ScriptJMXWrapper(JMXWrapper jmxWrapper) {
		this.jmxWrapper = jmxWrapper;
	}

	public ScriptJMXWrapper(MBeanServerConnection conn) {
		this.jmxWrapper = new JMXWrapper(conn);
	}

	/**
	 * Gibt eine GroovyMBean anhand des Beannames zurück.
	 * 
	 * @param beanName
	 *            Beanname
	 * @return MBean
	 * @throws IOException
	 *             IOException
	 * @throws JMException
	 *             JMException
	 */
	public GroovyMBean proxyFor(String beanName) throws JMException,
			IOException {

		return jmxWrapper.proxyFor(beanName);
	}

	/**
	 * @param script
	 *            Groovy-Script, dass Serverseitig auszuführen ist.
	 * @return serialisierbarer Rückgabewert des Scripts
	 * @throws IOException
	 *             bei Verbindungsproblemen
	 * @throws JMException
	 *             bei sonstigen JMX-Fehlern
	 */
	public Serializable executeScript(String script) throws JMException,
			IOException {
		return jmxWrapper.executeScript(script);
	}

	/**
	 * Liest aus der Ergebnismenge der Query die Attribute aus.
	 * 
	 * @param query
	 *            Query-String
	 * @param properties
	 *            Liste von zu lesenden Attributen
	 * @return Attribute aus der Ergebnismenge
	 * @throws IOException
	 *             IOException
	 * @throws JMException
	 *             JMException
	 */
	public Map<String, ? extends Map<String, ? extends Object>> query(
			String query, List<String> properties) throws IOException,
			JMException {

		return jmxWrapper.query(query, properties);
	}

	public void connect(String connectionString) {
		jmxWrapper.connect(connectionString);
	}
}

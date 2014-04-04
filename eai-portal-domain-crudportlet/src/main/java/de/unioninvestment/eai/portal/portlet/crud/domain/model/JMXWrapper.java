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

import groovy.jmx.builder.JmxBuilder;
import groovy.util.GroovyMBean;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;

/**
 * JMX Wrapper Objekt die JMX Connections und JMX Operationen
 * 
 * @author markus.bonsch
 * 
 */
public class JMXWrapper {
	private static final String JBOSS_REMOTING_URL = "service:jmx:rmi:///jndi/jmxconnector";
	private static final String JOLOKIA_REC = "jolokia";

	@SuppressWarnings("rawtypes")
	private Map connectionArgs;
	private MBeanServerConnection connection;

	private boolean hasRemoteScriptingMBean;

	private ObjectName remoteScriptingMBeanName;

	private String url;
	private HashMap<String, String> environment;

	/**
	 * Konstruktor.
	 * 
	 * @param connectionArgs
	 *            Parameter der Verbindung
	 */
	public JMXWrapper(@SuppressWarnings("rawtypes") Map connectionArgs) {
		this.connectionArgs = connectionArgs;
	}

	/**
	 * Konstruktor.
	 * 
	 * @param serverAdress
	 *            IP oder Name des Servers
	 * @param serverPort
	 *            Portnummer
	 */
	public JMXWrapper(String serverAdress, String serverPort) {
		this(serverAdress + ":" + serverPort);
	}

	/**
	 * No-Args constructor uses the globally available <code>MBeanServer</code>.
	 * 
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 * @see ManagementFactory#getPlatformMBeanServer()
	 */
	public JMXWrapper() {
		this(ManagementFactory.getPlatformMBeanServer());
	}

	/**
	 * @param connection
	 *            bestehende Verbindung zum MBeanServer
	 */
	public JMXWrapper(MBeanServerConnection connection) {
		this.connection = connection;
	}

	/**
	 * Konstruktor.
	 * 
	 * @param connectionString
	 *            siehe {@link #connect(String)}
	 */
	public JMXWrapper(String connectionString) {
		init(connectionString);
	}

	/**
	 * @param connectionString
	 *            verbindet sich mit dem Server anhand der Angaben im
	 *            {@code connectionString}. Dieser kann entweder im Format
	 *            <code>"server:port"</code> oder
	 *            <code>"service:jmx:rmi:///jndi/rmi://#/jmxconnector"</code>
	 *            oder <code>"http://server/jolokia"</code> angegeben werden.
	 */
	public void connect(String connectionString) {
		init(connectionString);
	}

	private void init(String connectionString) {
		if (connectionString != null && connectionString.length() > 0) {

			url = connectionString;
			environment = null;

			if (!connectionString.startsWith("service:jmx")
					&& !connectionString.contains(JOLOKIA_REC)) {
				url = JBOSS_REMOTING_URL;

				environment = new HashMap<String, String>();
				environment.put(Context.INITIAL_CONTEXT_FACTORY,
						"com.sun.jndi.rmi.registry.RegistryContextFactory");
				environment.put(Context.PROVIDER_URL, "rmi://"
						+ connectionString);
			}
		}
		connectionArgs = null;
		connection = null;
	}

	/**
	 * Gibt die Vebindung zum JMXServer zurück.
	 * 
	 * @return die Vebindung zum JMXServer
	 * @throws IOException
	 *             bei Verbindungsproblemen
	 */
	public MBeanServerConnection getServer() throws IOException {
		if (connection == null) {
			JMXConnector connector;

			if (url != null && url.contains(JOLOKIA_REC)) {
				connector = JolokiaJMXConnectorFactory.connect(url);
			} else if (connectionArgs != null) {
				JmxBuilder jmxBuilder = new JmxBuilder();
				connector = (JMXConnector) jmxBuilder.invokeMethod("client",
						getConnectionArgs());
			} else if (url != null) {
				connector = JMXConnectorFactory.connect(new JMXServiceURL(url),
						environment);
			} else {
				connector = JMXConnectorFactory.connect(new JMXServiceURL(
						JBOSS_REMOTING_URL), environment);
			}
			connector.connect();
			setConnection(connector.getMBeanServerConnection());

			try {
				remoteScriptingMBeanName = new ObjectName(
						"crud:service=CrudRemoteScript,name=script");
				hasRemoteScriptingMBean = connection
						.isRegistered(remoteScriptingMBeanName);

			} catch (MalformedObjectNameException e) {
				throw new TechnicalCrudPortletException(
						"Error querying for CrudRemoteScript-MBean", e);
			}
		}
		return connection;
	}

	/**
	 * Gibt eine GroovyMBean anhand des Beannames zurück.
	 * 
	 * @param beanName
	 *            Beanname
	 * @return MBean
	 * @throws IOException
	 *             bei Verbindungsproblemen
	 * @throws JMException
	 *             bei sonstigen JMX-Fehlern
	 */
	public GroovyMBean proxyFor(String beanName) throws JMException,
			IOException {

		return new GroovyMBean(getServer(), beanName);
	}

	/**
	 * Gibt die Parameter der Verbindung zurück.
	 * 
	 * @return Parameter der Verbindung
	 */
	@SuppressWarnings("rawtypes")
	Map getConnectionArgs() {
		return connectionArgs;
	}

	void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
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
	 *             bei Verbindungsproblemen
	 * @throws JMException
	 *             bei sonstigen JMX-Fehlern
	 */
	public Map<String, ? extends Map<String, ? extends Object>> query(
			String query, List<String> properties) throws IOException,
			JMException {

		if (connectionNotConfigured() || query == null || query.length() == 0) {
			return new HashMap<String, Map<String, Object>>();
		}

		MBeanServerConnection con = getServer();

		if (hasRemoteScriptingMBean) {
			return query(query, properties, new LinkedList<String>());

		} else {
			Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

			Set<ObjectName> queryNames = con.queryNames(new ObjectName(query),
					null);
			for (ObjectName objectName : queryNames) {
				Map<String, Object> attributesMap = new HashMap<String, Object>();

				result.put(objectName.getCanonicalName(), attributesMap);

				for (String propertyName : properties) {
					attributesMap.put(propertyName,
							con.getAttribute(objectName, propertyName));
				}
			}

			return result;
		}

	}

	private boolean connectionNotConfigured() {
		return connectionArgs == null && url == null && connection == null;
	}

	/**
	 * @param query
	 *            die Datenbankquery als MBean-Selektor
	 * @param properties
	 *            die Namen der abzufragenden Attribute
	 * @param getterScripts
	 *            optional:die Liste von Scripten, die zur Ermittlung der
	 *            Spaltenwerte auf jedes MBean anzuwenden sind. Die Reihenfolge
	 *            und Anzahl muss denen des {@code properties} Parameters
	 *            entsprechen. {@code null}-Listeneinträge werden wie gehabt als
	 *            Standard-MBean-Attribute abgefragt.
	 * @return eine Map mit den MBean-ObjectName-Strings als Key und einer
	 *         Map<String,Serializable> für die Werte
	 * @throws IOException
	 *             bei Verbindungsproblemen
	 * @throws JMException
	 *             bei sonstigen JMX-Fehlern
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Serializable>> query(String query,
			List<String> properties, List<String> getterScripts)
			throws IOException, JMException {

		if (connectionNotConfigured() || query == null || query.length() == 0) {
			return new HashMap<String, Map<String, Serializable>>();
		}

		return (Map<String, Map<String, Serializable>>) getServer().invoke(
				remoteScriptingMBeanName,
				"query",
				new Object[] { query, new ArrayList<String>(properties),
						new ArrayList<String>(getterScripts) },
				new String[] { "java.lang.String", "java.util.List",
						"java.util.List" });
	}

	void setRemoteScriptingMBeanName(ObjectName remoteScriptingMBeanName) {
		this.remoteScriptingMBeanName = remoteScriptingMBeanName;
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
		return (Serializable) getServer().invoke(remoteScriptingMBeanName,
				"executeScript", new Object[] { script },
				new String[] { "java.lang.String" });
	}
}

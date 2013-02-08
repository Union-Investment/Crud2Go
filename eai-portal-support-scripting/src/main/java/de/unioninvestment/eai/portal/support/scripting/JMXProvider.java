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

import java.util.Map;

import javax.management.MBeanServerConnection;

/**
 * Closure für den Zugriff auf JMX aus einem Script.
 * 
 */
public class JMXProvider extends Closure<ScriptJMXWrapper> {
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param owner
	 *            Besitzer des Closures
	 */
	public JMXProvider(Object owner) {
		super(owner);
	}

	/**
	 * Closure-Aufruf.
	 * 
	 * @param connectionArgs
	 *            Parameter für die Verbindung
	 * @return Wrapperklasse für den Zugriff auf JMX
	 */
	public ScriptJMXWrapper doCall(
			@SuppressWarnings("rawtypes") Map connectionArgs) {
		return new ScriptJMXWrapper(connectionArgs);
	}

	/**
	 * Closure-Aufruf.
	 * 
	 * @param serverAndPort
	 *            Entweder nur Server und Port "<server>:<port>" oder die
	 *            gesamte URL
	 * @return Wrapperklasse für den Zugriff auf JMX
	 */
	public ScriptJMXWrapper doCall(String serverAndPort) {
		return new ScriptJMXWrapper(serverAndPort);
	}

	/**
	 * Closure-Call. Changed: this version now uses the globally available,
	 * local platform <code>MBeanServer</code>.
	 * 
	 * @author Jan Malcomess (codecentric AG)
	 * @since 1.46
	 * @see ScriptJMXWrapper#ScriptJMXWrapper()
	 */
	public ScriptJMXWrapper doCall() {
		return new ScriptJMXWrapper();
	}

	/**
	 * Closure-Call.
	 * 
	 * @param conn
	 *            The <code>MBeanServerConnection</code> to use.
	 * @author Jan Malcomess (codecentric AG)
	 * @since 1.46
	 */
	public ScriptJMXWrapper doCall(MBeanServerConnection conn) {
		return new ScriptJMXWrapper(conn);
	}
}

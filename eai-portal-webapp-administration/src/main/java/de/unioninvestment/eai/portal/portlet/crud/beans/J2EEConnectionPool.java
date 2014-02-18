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
package de.unioninvestment.eai.portal.portlet.crud.beans;

import java.text.MessageFormat;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.AbstractConnectionPool;

/**
 * Factory-Klasse für JDBC-Connections. Der DataSource Lookup geschieht über
 * JNDI, wobei der angegebene Name zunächst gemäß {@link #pattern} konvertiert
 * wird.
 * 
 * @author carsten.mjartan
 * 
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class J2EEConnectionPool extends AbstractConnectionPool {

	private static final long serialVersionUID = 1L;

	private String jndiName;

	/**
	 * Konstruktor.
	 * 
	 * @param pattern
	 *            Template
	 * @param shortName
	 *            JNDIName
	 */
	public J2EEConnectionPool(String pattern, String shortName) {
		super(shortName);

		Assert.state(pattern != null, "Please provide a pattern!");
		Assert.state(shortName != null, "Please provide a shortName!");

		if (shortName.startsWith("java:")) {
			this.jndiName = shortName;
		} else {
			this.jndiName = MessageFormat.format(pattern, shortName);
		}
	}

	/**
	 * @return die DataSource Instanz über JNDI
	 * @throws NamingException
	 *             wenn im JNDI keine gültige DataSource-Instanz ermittelt
	 *             werden konnte
	 */
	@Override
	public DataSource lookupDataSource() throws NamingException {
		DataSource ds = null;

		InitialContext ic = new InitialContext();
		ds = (DataSource) ic.lookup(jndiName);
		return ds;
	}

	public String getJndiName() {
		return jndiName;
	}
}

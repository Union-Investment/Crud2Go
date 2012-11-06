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
import groovy.sql.Sql;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;

/**
 * 
 * Closure für das Ausführen von SQL aus einem Script.
 * 
 * @author max.hartmann
 * 
 * @see {{@link Sql}
 */
public class SqlProvider extends Closure<ExtendedSql> {
	private static final long serialVersionUID = 1L;

	private final ConnectionPoolFactory factory;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param owner Besitzer des Closures
	 * @param factory ConnectionPoolFactory
	 */
	public SqlProvider(Object owner, ConnectionPoolFactory factory) {
		super(owner);
		this.factory = factory;
	}

	/**
	 * Closure-Aufruf.
	 * 
	 * @param name DataSource-Kurzname oder vollständiger JNDI-NAME
	 * @return Groovy-SQL
	 * @throws NamingException wenn die DataSource nicht existiert
	 */
	public ExtendedSql doCall(String name) throws NamingException {
		DataSource dataSource;
		if (name.contains("/")) {
			InitialContext ic = new InitialContext();			
			dataSource = (DataSource) ic.lookup(name);
		} else {			
			dataSource = factory.getPool(name).lookupDataSource();
		}
		return new ExtendedSql(dataSource);
	}
}

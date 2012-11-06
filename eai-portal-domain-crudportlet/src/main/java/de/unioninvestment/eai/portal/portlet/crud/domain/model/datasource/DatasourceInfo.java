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
package de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource;

/**
 * Repräsentiert einen Informationssatz über eine Datenquelle.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DatasourceInfo {

	/**
	 * Typ der DataSource
	 * 
	 * @author carsten.mjartan
	 */
	public static enum Type {
		/**
		 * Non-Transactional
		 */
		NO_TX,
		/**
		 * Transactional
		 */
		LOCAL_TX,
		/**
		 * XA-Transactional
		 */
		XA
	}

	private final String datasourceName;
	private String jndiName;
	private DatasourceInfo.Type type;
	private String userName;
	private String connectionURL;

	/**
	 * Erzeugt eine neue Info-Instanz.
	 * 
	 * @param datasourceName
	 *            der name der Datenquelle.
	 */
	public DatasourceInfo(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	/**
	 * @return der Name der Datenquelle.
	 */
	public String getDatasourceName() {
		return this.datasourceName;
	}

	/**
	 * @return der Username.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName
	 *            der Username.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return die Verbindungs-URL.
	 */
	public String getConnectionURL() {
		return this.connectionURL;
	}

	/**
	 * @param connectionURL
	 *            die Verbindungs-URL.
	 */
	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	/**
	 * @return den JNDI-Namen
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * @param jdbcName
	 *            der JNDI-Name
	 */
	public void setJndiName(String jdbcName) {
		this.jndiName = jdbcName;
	}

	/**
	 * @return der Typ der Verbindung
	 */
	public DatasourceInfo.Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            Typ der Verbindung
	 */
	public void setType(DatasourceInfo.Type type) {
		this.type = type;
	}
}
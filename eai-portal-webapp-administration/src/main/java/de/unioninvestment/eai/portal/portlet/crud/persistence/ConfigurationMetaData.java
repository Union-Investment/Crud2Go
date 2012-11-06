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
package de.unioninvestment.eai.portal.portlet.crud.persistence;

import java.util.Date;

/**
 * Enthält Informationen zu der Konfiguration eines Portlets.
 * 
 * @author carsten.mjartan
 */
public class ConfigurationMetaData {

	private String user;
	private Date created;
	private String fileName;

	/**
	 * @param user
	 *            der Benutzer der die Konfiguration hochgeladen hat
	 * @param created
	 *            das Upload-Datum
	 */
	public ConfigurationMetaData(String user, Date created, String fileName) {
		this.user = user;
		this.created = created;
		this.fileName = fileName;
	}

	/**
	 * @return das Upload-Datum
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return der Benutzer der die Konfiguration hochgeladen hat
	 */
	public String getUser() {
		return user;
	}

	public String getFileName() {
		return fileName;
	}
}

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
package de.unioninvestment.eai.portal.portlet.crud.services;

import java.io.IOException;

import javax.portlet.PortletPreferences;

import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;

/**
 * Schnittstelle für das Konfigurationsmanagement. Methoden werden Transaktional
 * aufgerufen.
 * 
 * @author carsten.mjartan
 */
public interface ConfigurationService {

	/**
	 * @param portletId
	 *            die PortletID der aktuellen PortletPresenter-Instanz
	 * @param communityId
	 *            die ID der Liferay-Community
	 * @return die deserialisierte Konfiguration aus der Datenbank oder
	 *         <code>null</code>, falls keine gefunden wurde
	 */
	Config getPortletConfig(String portletId, long communityId);

	/**
	 * @param filename
	 *            der Dateiname der zu speichernden Konfiguration
	 * @param configXml
	 *            die Konfiguration
	 * @param portletId
	 *            die WindowID der aktuellen PortletPresenter-Instanz
	 * @param communityId
	 *            die ID der Liferay-Community
	 * @param username
	 *            der aktuelle Benutzername
	 */
	void storeConfigurationFile(String filename, byte[] configXml,
			String portletId, long communityId, String username);

	/**
	 * 
	 * @param portletId
	 *            die PortletID der aktuellen PortletPresenter-Instanz
	 * @param communityId
	 *            die ID der Liferay-Community
	 * @return wenn eine Konfiguration vorhanden ist
	 */
	ConfigurationMetaData getConfigurationMetaData(String portletId,
			long communityId);

	/**
	 * Gibt zurück, ob eine Portletkonfiguration vorhanden ist.
	 * 
	 * @param portletId
	 *            ID des Portlets
	 * @param communityId
	 *            die ID der Liferay-Community
	 * @return Ob bereits eine Portletkonfiguration vorhanden ist
	 */
	boolean hasConfigData(String portletId, long communityId);

	/**
	 * Gibt die Portletkonfiguration als String zurück.
	 * 
	 * @param portletId
	 *            Id des Portlets
	 * @param communityId
	 *            die ID der Liferay-Community
	 * @return Portletkonfiguration als String
	 * @throws IOException
	 *             Ausnahme
	 */
	String readConfigAsString(String portletId, long communityId)
			throws IOException;

	/**
	 * Legt einen Eintrag in der Mappingtabelle an bzw. liefert die ID eines
	 * bereits bestehenden Eintrags zurück
	 * 
	 * @param portletId
	 *            die Portlet-ID
	 * @param roleId
	 *            die Ressourcen-ID
	 * @param communityId
	 *            die Community-ID
	 * @return Primärschlüssel der Resource
	 */
	Long storeRoleResourceId(String portletId, long communityId, String roleId);

	/**
	 * Liest einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param portletId
	 *            die Portlet-ID
	 * @param roleId
	 *            die Ressourcen-ID
	 * @param communityId
	 *            die Community-ID
	 * @return Primärschlüssel der Resource
	 */
	Long readRoleResourceIdPrimKey(String portletId, long communityId,
			String roleId);

	/**
	 * Löscht einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param portletId
	 *            Id der Resource
	 * @param communityId
	 *            die ID der Liferay-Community
	 */
	void removeExistingRoleResourceIds(String portletId, long communityId);

	/**
	 * 
	 * @param portletConfig
	 * @param preferences
	 * @return <code>true</code>, if all required preferences are set
	 */
	boolean isConfigured(Config portletConfig, PortletPreferences preferences);

	/**
	 * Remove the portlet instance data based on the given portletId and communityId
	 * 
	 * @param portletId 
	 * @param communityId
	 */
	void removePortletInstanceData(String portletId, long communityId);
}

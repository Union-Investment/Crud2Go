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

import java.io.IOException;
import java.io.InputStream;

public interface ConfigurationDao {

	/**
	 * Callback-Interface für die Verarbeitung des Config-InputStreams aus dem
	 * Datenbank-Blob.
	 * 
	 * @author carsten.mjartan
	 * 
	 * @param <T>
	 *            der nach der Verarbeitung zurückgelieferte Wert
	 */
	public interface StreamProcessor<T> {
		/**
		 * 
		 * @param stream
		 *            Der deserialize InputStream.
		 * @param metaData
		 *            Zusatzinfos zur Config.
		 * @return Das Rückgabe Object des Processors.
		 */
		T process(InputStream stream, ConfigurationMetaData metaData) throws IOException;
	}

	/**
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param communityId
	 * @return <code>true</code>, falls eine Konfiguration existiert
	 */
	public abstract boolean hasConfigData(String portletId, long communityId);

	/**
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param communityId
	 * @return die ID des aktuellen Datenbankeintrags oder null wenn kein
	 *         Eintrag vorhanden.
	 */
	public abstract Long getId(String portletId, long communityId);

	/**
	 * 
	 * @param porteltId
	 *            , dient zur eindeutigen Identifizierung der
	 *            Portletkonfiguration.
	 * @param communityId
	 * @return Die zur PortletId abgelegte Portlet-Konfiguration.
	 */
	public abstract ConfigurationMetaData readConfigMetaData(String porteltId,
			long communityId);

	/**
	 * @param <T>
	 *            der Rückgabetyp des {@link StreamProcessor} Callbacks
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param processor
	 *            eine Callback-Instanz die mit dem {@link InputStream} des
	 *            Config-Blobs aufgerufen wird
	 * @return das vom processor zurückgelieferte Objekt
	 */
	public abstract <T> T readConfigStream(String portletId, long communityId,
			StreamProcessor<T> processor);

	/**
	 * Speichert eine neue Konfiguration für die PortletID und den
	 * Konfigurationsnamen ab oder überschreibt eine entsprechende Konfiguration
	 * gleichen Namens.
	 * 
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param communityId
	 * @param configName
	 *            der Name der Konfiguration
	 * @param configXML
	 *            die Konfiguration
	 * @param user
	 *            der aktuelle Benutzername
	 */
	public abstract void saveOrUpdateConfigData(String portletId,
			long communityId, String configName, byte[] configXML, String user);

	/**
	 * Liest einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param resourceId
	 *            Id des
	 * @return ResourceId bzw. Primäschlüssel
	 */
	public abstract Long readRoleResourceIdPrimKey(String resourceId);

	/**
	 * Legt einen Eintrag in der Mappingtabelle an.
	 * 
	 * @param primkey
	 *            Primärschlüssel
	 * @param resourceId
	 *            Id des Portlets.
	 * @return 
	 */
	public abstract long storeRoleResourceIdPrimKey(String resourceId);

	/**
	 * Löscht einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param portletId
	 *            Id des Portlets
	 * @param communityId
	 */
	public abstract void removeExistingRoleResourceIds(String portletId,
			long communityId);

	/**
	 * Löscht einen bestehenden Konfigurationseintrag.
	 * 
	 * @param portletId
	 * @param communityId
	 */
	public abstract void removeConfiguration(String portletId, long communityId);

}
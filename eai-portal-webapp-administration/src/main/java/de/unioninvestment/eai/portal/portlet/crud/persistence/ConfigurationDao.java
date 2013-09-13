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

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object for the portlet configuration data.
 * 
 * @author markus.bonsch
 * 
 * 
 */
@Repository
public class ConfigurationDao {

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
		 * @return Das Rückgabe Object des Processors.
		 */
		T process(InputStream stream);
	}

	private LobHandler lobHandler = new DefaultLobHandler();
	private JdbcTemplate jdbcTemplate;

	/**
	 * 
	 * @param jdbcTemplate
	 *            JdbcTemplate
	 */
	@Autowired
	public ConfigurationDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param communityId
	 * @return <code>true</code>, falls eine Konfiguration existiert
	 */
	public boolean hasConfigData(final String portletId, long communityId) {
		int count = jdbcTemplate
				.queryForObject(
						"SELECT COUNT(ID) FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ?",
						Integer.class, portletId, communityId);
		return count > 0;
	}

	/**
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param communityId
	 * @return die ID des aktuellen Datenbankeintrags oder null wenn kein
	 *         Eintrag vorhanden.
	 */
	public Long getId(final String portletId, long communityId) {
		List<Long> result = jdbcTemplate
				.queryForList(
						"SELECT ID FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC",
						Long.class, portletId, communityId);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * 
	 * @param porteltId
	 *            , dient zur eindeutigen Identifizierung der
	 *            Portletkonfiguration.
	 * @param communityId
	 * @return Die zur PortletId abgelegte Portlet-Konfiguration.
	 */
	public ConfigurationMetaData readConfigMetaData(final String porteltId,
			long communityId) {

		List<ConfigurationMetaData> result = jdbcTemplate
				.query("SELECT DATE_CREATED, USER_CREATED, CONFIG_NAME FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC",
						new Object[] { porteltId, communityId },
						new RowMapper<ConfigurationMetaData>() {
							public ConfigurationMetaData mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return new ConfigurationMetaData(rs
										.getString("USER_CREATED"), rs
										.getTimestamp("DATE_CREATED"), rs
										.getString("CONFIG_NAME"));
							}
						});

		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);

	}

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
	public <T> T readConfigStream(String portletId, long communityId,
			final StreamProcessor<T> processor) {
		return jdbcTemplate
				.queryForObject(
						"SELECT * FROM ( SELECT CONFIG_XML FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC ) WHERE ROWNUM = 1",
						new RowMapper<T>() {
							public T mapRow(ResultSet rs, int i)
									throws SQLException {
								InputStream stream = lobHandler
										.getBlobAsBinaryStream(rs, 1);
								return processor.process(stream);
							}
						}, portletId, communityId);
	}

	/**
	 * Liest die Portletkonfiguration aus der DB.
	 * 
	 * @param portletId
	 *            Id des Portlets
	 * @param communityId
	 * @return Portletkonfiguration als String
	 */
	public InputStream readConfigAsStream(String portletId, long communityId) {
		return jdbcTemplate
				.queryForObject(
						"SELECT * FROM ( SELECT CONFIG_XML FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC ) WHERE ROWNUM = 1",
						new RowMapper<InputStream>() {
							public InputStream mapRow(ResultSet rs, int i)
									throws SQLException {
								return lobHandler.getBlobAsBinaryStream(rs, 1);
							}
						}, portletId, communityId);
	}

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
	public void saveOrUpdateConfigData(final String portletId,
			long communityId, final String configName, final byte[] configXML,
			final String user) {
		Long id = getId(portletId, communityId);
		if (id != null) {
			updateConfigData(id, configName, configXML, user);

		} else {
			insertConfigData(portletId, communityId, configName, configXML,
					user);
		}
	}

	/**
	 * Legt einen eindeutigen Schlüssel an.
	 * 
	 * @return Eindeutiger Schlüssel.
	 */
	public Long readNextRoleResourceIdPrimKey() {
		return jdbcTemplate.queryForObject(
				"select RESOURCEID_SEQ.nextval from dual", Long.class);
	}

	/**
	 * Liest einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param resourceId
	 *            Id des
	 * @return ResourceId bzw. Primäschlüssel
	 */
	public Long readRoleResourceIdPrimKey(String resourceId) {
		try {
			return jdbcTemplate
					.queryForObject(
							"SELECT PRIMKEY FROM RESOURCEID_PRIMKEY WHERE RESOURCEID = ?",
							Long.class, resourceId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Legt einen Eintrag in der Mappingtabelle an.
	 * 
	 * @param primkey
	 *            Primärschlüssel
	 * @param resourceId
	 *            Id des Portlets.
	 */
	public void storeRoleResourceIdPrimKey(Long primkey, String resourceId) {
		jdbcTemplate
				.update("INSERT INTO RESOURCEID_PRIMKEY (PRIMKEY, RESOURCEID) values (?,?)",
						new Object[] { primkey, resourceId }, new int[] {
								Types.NUMERIC, Types.VARCHAR });
	}

	/**
	 * @param configId
	 *            die ID des bestehenden Datensatzes
	 * @param configName
	 *            der Name der Konfiguration
	 * @param configXML
	 *            die Konfiguration
	 * @param user
	 *            der aktuelle Benutzername
	 */
	private void updateConfigData(final long configId, final String configName,
			final byte[] configXML, final String user) {
		jdbcTemplate
				.execute(
						"UPDATE ADM_CONFIG SET CONFIG_NAME = ?, CONFIG_XML = ?, USER_CREATED = ?,DATE_CREATED = ? WHERE ID=?",
						new AbstractLobCreatingPreparedStatementCallback(
								lobHandler) {
							protected void setValues(PreparedStatement ps,
									LobCreator lobCreator) throws SQLException {

								ps.setString(1, configName);
								lobCreator.setBlobAsBytes(ps, 2, configXML);
								ps.setString(3, user);
								ps.setTimestamp(
										4,
										new Timestamp(System
												.currentTimeMillis()));
								ps.setLong(5, configId);
							}
						});
	}

	/**
	 * Speichert eine neue Konfiguration ab.
	 * 
	 * @param portletId
	 *            die PortletID der aktuellen Portlet-Instanz
	 * @param configName
	 *            der Name der Konfiguration
	 * @param configXML
	 *            die Konfiguration
	 * @param user
	 *            der aktuelle Benutzername
	 */
	private void insertConfigData(final String portletId,
			final long communityId, final String configName,
			final byte[] configXML, final String user) {
		jdbcTemplate
				.execute(
						"INSERT INTO ADM_CONFIG (PORTLET_ID, COMMUNITY_ID, CONFIG_NAME, CONFIG_XML,USER_CREATED,DATE_CREATED) VALUES (?,?,?,?,?,?)",
						new AbstractLobCreatingPreparedStatementCallback(
								lobHandler) {
							protected void setValues(PreparedStatement ps,
									LobCreator lobCreator) throws SQLException {
								ps.setString(1, portletId);
								ps.setLong(2, communityId);
								ps.setString(3, configName);
								lobCreator.setBlobAsBytes(ps, 4, configXML);
								ps.setString(5, user);
								ps.setTimestamp(
										6,
										new Timestamp(System
												.currentTimeMillis()));
							}
						});
	}

	/**
	 * Löscht einen Eintrag aus der Mappingtabelle.
	 * 
	 * @param portletId
	 *            Id des Portlets
	 * @param communityId
	 */
	public void removeExistingRoleResourceIds(String portletId, long communityId) {
		jdbcTemplate
				.execute("DELETE FROM RESOURCEID_PRIMKEY WHERE RESOURCEID LIKE '"
						+ portletId + "_" + communityId + "_%' escape '#'");
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}

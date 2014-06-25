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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile("ORACLE_STORAGE")
public class DefaultConfigurationDao implements ConfigurationDao {

	static final class ConfigStreamRowMapper<T> implements RowMapper<T> {
		private final StreamProcessor<T> processor;

		private LobHandler lobHandler;

		public ConfigStreamRowMapper(StreamProcessor<T> processor,
				LobHandler lobHandler) {
			this.processor = processor;
			this.lobHandler = lobHandler;
		}

		public T mapRow(ResultSet rs, int i) throws SQLException {
			ConfigurationMetaData metaData = new MetaDataRowMapper().mapRow(rs,
					i);
			InputStream stream = lobHandler.getBlobAsBinaryStream(rs,
					"CONFIG_XML");
			try {
				return processor.process(stream, metaData);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final class MetaDataRowMapper implements
			RowMapper<ConfigurationMetaData> {
		public ConfigurationMetaData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			return new ConfigurationMetaData( //
					rs.getString("USER_CREATED"), //
					rs.getTimestamp("DATE_CREATED"), //
					rs.getTimestamp("DATE_UPDATED"), //
					rs.getString("CONFIG_NAME"));
		}
	}

	protected LobHandler lobHandler = new DefaultLobHandler();
	protected JdbcTemplate jdbcTemplate;

	/**
	 * 
	 * @param jdbcTemplate
	 *            JdbcTemplate
	 */
	@Autowired
	public DefaultConfigurationDao(JdbcTemplate jdbcTemplate) {
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
				.query("SELECT DATE_CREATED, DATE_UPDATED, USER_CREATED, CONFIG_NAME FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC",
						new Object[] { porteltId, communityId },
						new MetaDataRowMapper());

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
						"SELECT * FROM ( SELECT DATE_CREATED, DATE_UPDATED, USER_CREATED, CONFIG_NAME, CONFIG_XML FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC ) WHERE ROWNUM = 1",
						new ConfigStreamRowMapper<T>(processor, lobHandler),
						portletId, communityId);
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
	private Long readNextRoleResourceIdPrimKey() {
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
	 * @return
	 */
	public long storeRoleResourceIdPrimKey(String resourceId) {
		long primkey = readNextRoleResourceIdPrimKey();
		jdbcTemplate
				.update("INSERT INTO RESOURCEID_PRIMKEY (PRIMKEY, RESOURCEID) values (?,?)",
						new Object[] { primkey, resourceId }, new int[] {
								Types.NUMERIC, Types.VARCHAR });
		return primkey;
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
						"UPDATE ADM_CONFIG SET CONFIG_NAME = ?, CONFIG_XML = ?, USER_CREATED = ? WHERE ID=?",
						new AbstractLobCreatingPreparedStatementCallback(
								lobHandler) {
							protected void setValues(PreparedStatement ps,
									LobCreator lobCreator) throws SQLException {

								ps.setString(1, configName);
								lobCreator.setBlobAsBytes(ps, 2, configXML);
								ps.setString(3, user);
								ps.setLong(4, configId);
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

	@Override
	public void removeConfiguration(String portletId, long communityId) {
		jdbcTemplate
				.update("DELETE FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ?",
						portletId, communityId);
	}

	@Override
	public void removeExistingRoleResourceIds(String portletId, long communityId) {
		jdbcTemplate
				.execute("DELETE FROM RESOURCEID_PRIMKEY WHERE RESOURCEID LIKE '"
						+ portletId + "_" + communityId + "_%' escape '#'");
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}

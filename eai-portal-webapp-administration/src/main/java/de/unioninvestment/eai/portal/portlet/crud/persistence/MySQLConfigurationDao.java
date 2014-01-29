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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object for the portlet configuration data.
 * 
 * @author markus.bonsch
 * 
 * 
 */
@Repository
@Profile("MYSQL_STORAGE")
public class MySQLConfigurationDao extends DefaultConfigurationDao {

	/**
	 * 
	 * @param jdbcTemplate
	 *            JdbcTemplate
	 */
	@Autowired
	public MySQLConfigurationDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
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
						"SELECT DATE_CREATED, DATE_UPDATED, USER_CREATED, CONFIG_NAME, CONFIG_XML FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ? ORDER BY DATE_CREATED DESC LIMIT 1",
						new ConfigStreamRowMapper<T>(processor, lobHandler),
						portletId, communityId);
	}

	@Override
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

	@Override
	public long storeRoleResourceIdPrimKey(final String resourceId) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement stmt = con
						.prepareStatement("INSERT INTO RESOURCEID_PRIMKEY (RESOURCEID) values (?)", Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, resourceId);
				return stmt;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

}

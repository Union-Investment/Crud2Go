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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao.StreamProcessor;

public class DefaultConfigurationDaoTest {

	DefaultConfigurationDao configurationDao;
	private static DefaultConfigurationDao configurationDaoDerby;

	@Mock
	JdbcTemplate mockJdbcTemplate;

	private static final String testPortletId = "TestID-1";
	private static final long COMMUNITY_ID = 18005L;

	private static ClassPathXmlApplicationContext ctx;

	@BeforeClass
	public static void init() {
		ctx = new ClassPathXmlApplicationContext(
				"eai-portal-web-test-applicationcontext.xml");
		ctx.getEnvironment().setActiveProfiles("ORACLE_STORAGE");
		JdbcTemplate template = ctx.getBean("jdbcTemplate", JdbcTemplate.class);
		template.execute("CREATE TABLE ADM_CONFIG" + "("
				+ "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
				+ "PORTLET_ID VARCHAR(255) NOT NULL,"
				+ "COMMUNITY_ID INT NOT NULL,"
				+ "CONFIG_NAME VARCHAR(255) NOT NULL,"
				+ "CONFIG_XML BLOB NOT NULL,"
				+ "USER_CREATED VARCHAR(255) NOT NULL,"
				+ "DATE_CREATED TIMESTAMP NOT NULL," //
				+ "DATE_UPDATED TIMESTAMP NOT NULL default current timestamp" //
				+ ")");
		configurationDaoDerby = ctx.getBean(DefaultConfigurationDao.class);
	}

	@AfterClass
	public static void destroy() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Before
	public void setUp() throws JAXBException, SAXException {
		MockitoAnnotations.initMocks(this);
		configurationDao = new DefaultConfigurationDao(mockJdbcTemplate);
	}

	@Test
	public void shouldSQLStatements() {
		configurationDaoDerby.saveOrUpdateConfigData(testPortletId,
				COMMUNITY_ID, "testConfig.xml", new byte[] { 1 }, "testUser");
		configurationDaoDerby.saveOrUpdateConfigData(testPortletId,
				COMMUNITY_ID, "testConfig.xml", new byte[] { 1 }, "testUser");
		configurationDaoDerby.readConfigMetaData(testPortletId, COMMUNITY_ID);
		configurationDaoDerby.getId(testPortletId, COMMUNITY_ID);
	}

	@Test
	public void shouldCheckIfConfigIsAvailable() {
		when(
				mockJdbcTemplate.queryForObject(anyString(), eq(Integer.class),
						anyString(), anyLong())).thenReturn(1);
		boolean isConfigAv = configurationDao.hasConfigData(testPortletId,
				COMMUNITY_ID);
		assertTrue(isConfigAv);
	}

	@Test
	public void shouldCheckIfConfigIsNotAvailable() {
		when(
				mockJdbcTemplate.queryForObject(anyString(), eq(Integer.class),
						anyString(), anyLong())).thenReturn(0);
		boolean isConfigAv = configurationDao.hasConfigData(testPortletId,
				COMMUNITY_ID);
		assertFalse(isConfigAv);
	}

	@Test
	public void shouldGetConfigIdByPortletIdAndCommunityId() {
		when(
				mockJdbcTemplate.queryForList(anyString(), eq(Long.class),
						anyString(), eq(COMMUNITY_ID))).thenReturn(
				Arrays.asList(new Long(1)));
		Long id = configurationDao.getId(testPortletId, COMMUNITY_ID);
		assertEquals(new Long(1), id);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldLoadMetaData() {
		when(
				mockJdbcTemplate.query(any(String.class), eq(new Object[] {
						testPortletId, COMMUNITY_ID }), any(RowMapper.class)))
				.thenReturn(
						Arrays.asList(new ConfigurationMetaData("testuser",
								new Date(), new Date(), null)));

		ConfigurationMetaData metaData = configurationDao.readConfigMetaData(
				testPortletId, COMMUNITY_ID);
		assertEquals("testuser", metaData.getUser());
	}

	@Test
	public void shouldUpdateAExistingConfig() {
		when(
				mockJdbcTemplate.queryForObject(anyString(), eq(Integer.class),
						anyString())).thenReturn(1);
		when(
				mockJdbcTemplate.queryForObject(anyString(), eq(Long.class),
						anyString())).thenReturn(new Long(10));
		configurationDao.saveOrUpdateConfigData(testPortletId, COMMUNITY_ID,
				"testConfig.xml", null, "testUser");
		verify(mockJdbcTemplate).execute(any(String.class),
				any(AbstractLobCreatingPreparedStatementCallback.class));

	}

	@Test
	public void shouldInsertANewConfig() {
		when(
				mockJdbcTemplate.queryForObject(anyString(), eq(Integer.class),
						anyString())).thenReturn(0);
		configurationDao.saveOrUpdateConfigData(testPortletId, COMMUNITY_ID,
				"testConfig.xml", null, "testUser");
		verify(mockJdbcTemplate).execute(any(String.class),
				any(AbstractLobCreatingPreparedStatementCallback.class));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldLoadConfigData() {
		PortletConfig config = new PortletConfig();
		config.setTitle("testTitel");
		when(
				mockJdbcTemplate.queryForObject(any(String.class),
						any(RowMapper.class), eq(testPortletId),
						eq(COMMUNITY_ID))).thenReturn(config);

		StreamProcessor<PortletConfig> processor = new StreamProcessor<PortletConfig>() {
			@Override
			public PortletConfig process(InputStream stream,
					ConfigurationMetaData metaData) {
				return new PortletConfig();
			}
		};
		PortletConfig result = configurationDao.readConfigStream(testPortletId,
				COMMUNITY_ID, processor);
		assertEquals("testTitel", result.getTitle());
	}

	@Test
	public void shouldRemoveConfiguration() {
		configurationDao.removeConfiguration("myPortletId", 4711L);

		verify(mockJdbcTemplate)
				.update("DELETE FROM ADM_CONFIG WHERE PORTLET_ID = ? AND COMMUNITY_ID = ?",
						"myPortletId", 4711L);
	}

	@Test
	public void shouldRemoveExistingRoleResourceIDs() {
		configurationDao.removeExistingRoleResourceIds("myPortletId", 4711L);

		verify(mockJdbcTemplate).execute(
				"DELETE FROM RESOURCEID_PRIMKEY WHERE RESOURCEID LIKE '"
						+ "myPortletId_4711_%' escape '#'");
	}
}

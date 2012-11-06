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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao.StreamProcessor;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler;

/**
 * Implementierung des {@link ConfigurationService}. Die Konfiguration wird über
 * das {@link ConfigurationDao} im XML-Format gelesen und gespeichert und per
 * JAXB deserialisiert.
 * 
 * @author carsten.mjartan
 */
@Transactional
public class DefaultConfigurationService implements ConfigurationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultConfigurationService.class);

	private ConfigurationDao dao;

	private ConfigurationScriptsCompiler compiler;

	private PortletConfigurationUnmarshaller unmarshaller;

	/**
	 * Initialisiert den JAXB-Context.
	 * 
	 * @param dao
	 *            Das DAO für die Persistenzschicht der Konfiguration.
	 * @param compiler
	 *            Scriptcompiler
	 * 
	 * @throws JAXBException
	 *             kann beim Parsen des Konfigurations XSD geworfen werden.
	 * @throws SAXException
	 *             kann beim Parsen des Konfigurations XSD geworfen werden.
	 */
	@Autowired
	public DefaultConfigurationService(ConfigurationDao dao,
			ConfigurationScriptsCompiler compiler) throws JAXBException,
			SAXException {
		this.dao = dao;
		this.compiler = compiler;
		this.unmarshaller = new PortletConfigurationUnmarshaller();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConfigurationMetaData getConfigurationMetaData(String portletId,
			long communityId) {
		try {
			return dao.readConfigMetaData(portletId, communityId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config getPortletConfig(final String portletId,
			final long communityId) {

		try {
			return dao.readConfigStream(portletId, communityId,
					new StreamProcessor<Config>() {
						@Override
						public Config process(InputStream stream) {
							try {
								return buildPortletConfig(stream, portletId,
										communityId);
							} catch (JAXBException e) {
								LOG.warn(
										"XML Konfiguration für Window '"
												+ portletId
												+ "' ist nicht über JAXB deserialisierbar: ",
										e);
								return null;
							}
						}
					});

		} catch (EmptyResultDataAccessException e) {
			LOG.info("Konfiguration für portlet '" + portletId
					+ "', communityId " + communityId + " nicht gefunden!");
			return null;

		}
	}

	private Config buildPortletConfig(InputStream stream, String portletId,
			long communityId) throws JAXBException {
		PortletConfig config = unmarshaller.unmarshal(stream);
		compiler.compileAllScripts(config);

		Map<String, Long> roleResourceIDs = new HashMap<String, Long>();
		if (config.getRoles() != null) {
			for (RoleConfig role : config.getRoles().getRole()) {
				Long readRoleResourceIdPrimKey = storeRoleResourceId(portletId,
						communityId, role.getName());

				String roleResourceId = createRoleResourceId(portletId,
						communityId, role.getName());
				roleResourceIDs.put(roleResourceId, readRoleResourceIdPrimKey);
			}
		}

		return new Config(config, roleResourceIDs);
	}

	@Override
	public Long readRoleResourceIdPrimKey(String portletId, long communityId,
			String roleId) {
		String roleResourceId = createRoleResourceId(portletId, communityId,
				roleId);
		return dao.readRoleResourceIdPrimKey(roleResourceId);
	}

	private String createRoleResourceId(String portletId, long communityId,
			String roleId) {
		return portletId + "_" + communityId + "_" + roleId;
	}

	@Override
	public Long storeRoleResourceId(String portletId, long communityId,
			String roleId) {
		Long readRoleResourceIdPrimKey = readRoleResourceIdPrimKey(portletId,
				communityId, roleId);
		if (readRoleResourceIdPrimKey == null) {
			readRoleResourceIdPrimKey = dao.readNextRoleResourceIdPrimKey();
			dao.storeRoleResourceIdPrimKey(readRoleResourceIdPrimKey,
					createRoleResourceId(portletId, communityId, roleId));
		}
		return readRoleResourceIdPrimKey;
	}

	@Override
	public void removeExistingRoleResourceIds(String portletId, long communityId) {
		dao.removeExistingRoleResourceIds(portletId, communityId);
	}

	@Override
	public void storeConfigurationFile(String filename, byte[] configXml,
			String portletId, long communityId, String username) {
		dao.saveOrUpdateConfigData(portletId, communityId, filename, configXml,
				username);
	}

	@Override
	public boolean hasConfigData(String portletId, long communityId) {
		return dao.hasConfigData(portletId, communityId);
	}

	@Override
	public String readConfigAsString(String portletId, long communityId)
			throws IOException {
		InputStream stream = dao.readConfigAsStream(portletId, communityId);
		return IOUtils.toString(stream, "UTF-8");
	}
}

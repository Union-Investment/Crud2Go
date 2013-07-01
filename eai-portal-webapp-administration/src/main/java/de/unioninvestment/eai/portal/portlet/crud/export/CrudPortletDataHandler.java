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
package de.unioninvestment.eai.portal.portlet.crud.export;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.BasePortletDataHandler;
import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.lar.PortletDataException;
import com.liferay.portal.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.portal.kernel.lar.PortletDataHandlerControl;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;

/**
 * Händelt beim ExportCallback Portal-Spezifische Konfiguration.
 * 
 * @author max.hartmann
 */
@Configurable
public class CrudPortletDataHandler extends BasePortletDataHandler {

	private static final String ROLE_PREF_PREFIX = "ROLE_";

	private static final String ROLE_NAMES_PREF = "roleNames";

	private static final int EXPORT_VERSION = 2;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CrudPortletDataHandler.class);

	private static final String NAMESPACE = "crud";

	private static final PortletDataHandlerBoolean CRUD_CONFIGURATION = new PortletDataHandlerBoolean(
			NAMESPACE, "configuration", true, true);

	@Autowired
	private ConfigurationService configurationService;

	/**
	 * Leerer Konstruktor.
	 */
	public CrudPortletDataHandler() {
		super();
	}

	@Override
	public boolean isPublishToLiveByDefault() {
		return true;
	}

	@Override
	public String doExportData(PortletDataContext context, String portletId,
			PortletPreferences preferences) throws PortletDataException {

		long communityId = context.getScopeGroupId();

		LOGGER.debug(
				"Exporting CRUD portlet data for portletId {}, communityId {}",
				portletId, communityId);

		try {
			preferences.setValue("VERSION", String.valueOf(EXPORT_VERSION));

			if (configurationService.hasConfigData(portletId, communityId)) {

				exportRolesAndPermissions(context, portletId, preferences,
						communityId);

				exportConfig(context, portletId, communityId);

				preferences.store();

				ConfigurationMetaData metaData = configurationService
						.getConfigurationMetaData(portletId, communityId);
				return metaData.getFileName();
			} else {
				preferences.store();
				return "Keine Konfiguration vorhanden";
			}

		} catch (Exception e) {
			throw new PortletDataException("Error exporting portlet "
					+ portletId + "/" + communityId, e);
		}

	}

	private void exportConfig(PortletDataContext context, String portletId,
			long communityId) throws IOException {
		String configString = configurationService.readConfigAsString(
				portletId, communityId);
		if (configString != null) {
			ZipWriter zipWriter = context.getZipWriter();
			String filePath = portletId + "/portletConfiguration.xml";
			zipWriter.addEntry(filePath, configString);
		}
	}

	private void exportRolesAndPermissions(PortletDataContext context,
			String portletId, PortletPreferences preferences, long communityId)
			throws ReadOnlyException, PortalException, SystemException {

		PortletConfig portletConfig = configurationService.getPortletConfig(
				portletId, communityId).getPortletConfig();

		if (portletConfig.getRoles() != null) {
			List<RoleConfig> roleList = collectPortletRoles(portletConfig
					.getRoles().getRole());
			String[] roleNames = new String[roleList.size()];
			int idx = 0;
			for (RoleConfig role : roleList) {
				long primkey = configurationService.readRoleResourceIdPrimKey(
						portletId, communityId, role.getName());

				roleNames[idx++] = role.getName();

				preferences.setValue(
						ROLE_PREF_PREFIX + String.valueOf(primkey),
						role.getName());

				context.addPermissions(PortletRole.RESOURCE_KEY, primkey);
			}
			preferences.setValues(ROLE_NAMES_PREF, roleNames);
		}
	}

	private List<RoleConfig> collectPortletRoles(List<RoleConfig> roles) {
		List<RoleConfig> results = new LinkedList<RoleConfig>();
		for (RoleConfig role : roles) {
			if (role.getPortalRole() == null) {
				results.add(role);
			}
		}
		return results;
	}

	@Override
	public PortletPreferences doImportData(PortletDataContext context,
			String portletId, PortletPreferences preferences, String data)
			throws PortletDataException {

		long communityId = context.getScopeGroupId();

		LOGGER.debug(
				"Importing CRUD portlet data for portletId {}, communityId {}",
				portletId, communityId);

		checkCompatibility(preferences);

		try {
			ZipReader zipReader = context.getZipReader();
			byte[] portletConfigData = zipReader.getEntryAsByteArray(portletId
					+ "/portletConfiguration.xml");

			if (portletConfigData != null) {
				configurationService.storeConfigurationFile(data,
						portletConfigData, portletId, communityId, "import");

				Map<String, Long> newRoleIds = createResourceRoles(portletId,
						communityId, preferences);

				importPermissions(context, preferences, newRoleIds);
			}

			return null;

		} catch (RuntimeException e) {
			throw new PortletDataException("Error importing portlet "
					+ portletId + "/" + communityId, e);
		}
	}

	private void importPermissions(PortletDataContext context,
			PortletPreferences preferences, Map<String, Long> newRoleIds) {
		for (Entry<String, List<KeyValuePair>> entry : context.getPermissions()
				.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(PortletRole.RESOURCE_KEY)) {
				long oldId = Long.valueOf(key.substring(key.indexOf('#') + 1));
				String roleName = preferences.getValue(
						ROLE_PREF_PREFIX + oldId, null);

				if (roleName != null) {
					Long newId = newRoleIds.get(roleName);

					LOGGER.debug(
							"Importing permission for resource {}, oldId: {}, roleName: {}, newId: {}",
							new Object[] { key, oldId, roleName, newId });
					try {
						if (newId != null) {
							context.importPermissions(PortletRole.RESOURCE_KEY,
									oldId, newId);
						} else {
							LOGGER.warn("Cannot import permission: Role resource missing");
						}
					} catch (Exception e) {
						throw new BusinessException(
								"portlet.crud.error.import", e);
					}
				} else {
					LOGGER.debug(
							"Ignoring permission for resource {}, oldId: {}. Role name undefined",
							key, oldId);

				}
			}
		}
	}

	private Map<String, Long> createResourceRoles(String portletId,
			long communityId, PortletPreferences preferences) {
		String[] roleNames = preferences.getValues(ROLE_NAMES_PREF,
				new String[0]);
		Map<String, Long> newRoleIds = new HashMap<String, Long>(
				roleNames.length);
		for (String roleName : roleNames) {
			Long newId = configurationService.storeRoleResourceId(portletId,
					communityId, roleName);
			newRoleIds.put(roleName, newId);
			LOGGER.debug("Creating resource for role {}, newId: {}", roleName,
					newId);
		}
		return newRoleIds;
	}

	private void checkCompatibility(PortletPreferences preferences)
			throws PortletDataException {
		String versionString = preferences.getValue("VERSION", "1");
		int version = Integer.parseInt(versionString);
		if (version != 2) {
			throw new PortletDataException(
					"Incompatible CRUD-Portlet export version " + version);
		}
	}

	/**
	 * Löscht die Rollen-Ressourcen, so dass beim nächsten Import die
	 * Berechtigungen erneut importiert werden.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public PortletPreferences deleteData(PortletDataContext context,
			String portletId, PortletPreferences preferences)
			throws PortletDataException {

		long communityId = context.getScopeGroupId();

		configurationService.removeExistingRoleResourceIds(portletId,
				communityId);

		return super.deleteData(context, portletId, preferences);
	}

	@Override
	public PortletDataHandlerControl[] getExportControls() {
		return new PortletDataHandlerControl[] { CRUD_CONFIGURATION };
	}

	@Override
	public PortletDataHandlerControl[] getImportControls() {
		return new PortletDataHandlerControl[] { CRUD_CONFIGURATION };
	}
}

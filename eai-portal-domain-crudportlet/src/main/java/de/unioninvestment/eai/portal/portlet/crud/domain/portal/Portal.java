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
package de.unioninvestment.eai.portal.portlet.crud.domain.portal;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortletService;

/**
 * 
 * Hilfsklasse für den Zugriff auf Liferay.
 * 
 * @author siva.selvarajah
 */
@Component
public class Portal {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(Portal.class);

	@Value("${portal.instanceId}")
	private String portalInstanceId = "liferay.com";

	private Long companyId;
	
	/**
	 * Ermittelt die CompanyId.
	 * 
	 * @return CompanyId
	 */
	public Long getCompanyId() {
		if (companyId == null) {
			try {
				Company company = CompanyLocalServiceUtil
						.getCompanyByWebId(portalInstanceId);
				if (company == null) {
					throw new NoSuchElementException(
							"No Liferay companyId for webId 'liferay.com' found!");
				} else {
					companyId = company.getCompanyId();
				}
			} catch (SystemException e) {
				throw new TechnicalCrudPortletException(
						"Error getting companyId by webId 'liferay.com'", e);
			} catch (PortalException e) {
				throw new TechnicalCrudPortletException(
						"Error getting companyId by webId 'liferay.com'", e);
			}
		}
		return companyId;
	}

	/**
	 * Ermittelt die UserId eines Benutzers.
	 * 
	 * @param username
	 *            Benutzername
	 * @return UserId
	 */
	User getUser(String username) {
		try {
			User user = UserLocalServiceUtil.getUserByScreenName(
					getCompanyId(), username);
			if (user == null) {
				throw new NoSuchElementException(
						"No Liferay userId found for username '" + username
								+ "'");
			}
			return user;

		} catch (SystemException e) {
			throw new TechnicalCrudPortletException(
					"Error getting user for username ' " + username + "'", e);
		} catch (PortalException e) {
			throw new TechnicalCrudPortletException(
					"Error getting user for username ' " + username + "'", e);
		}

	}

	/**
	 * Gibt alle Rollen zurück.
	 * 
	 * @return alle Rollen
	 */
	public Set<String> getAllRoles() {
		try {
			return extractRoleNames(RoleLocalServiceUtil
					.getRoles(getCompanyId()));
		} catch (SystemException e) {
			throw new BusinessException("portlet.crud.error.userRolesLoad");
		}
	}

	/**
	 * Ermittelt alle Rollen des Benutzers.
	 * 
	 * @param username
	 *            Benutzername
	 * @return Rollen des Benutzers
	 */
	public Set<String> getRoles(String username) {
		try {
			Long id = getUser(username).getUserId();
			return extractRoleNames(RoleLocalServiceUtil.getUserRoles(id));
		} catch (SystemException e) {
			throw new BusinessException("portlet.crud.error.userRolesLoad");
		}
	}

	/**
	 * 
	 * Registriert eine Resource.
	 * 
	 * @param type
	 *            Resourcetyp
	 * @param id
	 *            Resourceid
	 * @param owner
	 *            Besitzer der Resource
	 */
	public void registerResource(String type, String id, String owner) {
		try {
			Long ownerId = getUser(owner).getUserId();

			ResourceLocalServiceUtil.addResources(getCompanyId(), 0l, ownerId,
					type, id, false, false, false);
		} catch (SystemException e) {
			throw new TechnicalCrudPortletException(
					"Error at registerResource Ressources type:" + type
							+ " - id:" + id, e);
		} catch (PortalException e) {
			throw new TechnicalCrudPortletException(
					"Error at registerResource Ressources type:" + type
							+ " - id:" + id, e);
		}

	}

	/**
	 * Prüft, ob der Benutzer auf die Resource zugreifen darf.
	 * 
	 * @param username
	 *            Benutzername
	 * @param perm
	 *            Berechtigung
	 * @param type
	 *            Resourcetyp
	 * @param id
	 *            Resourceid
	 * @return ob der Benutzer auf die Resource zugreifen darf.
	 * @throws Exception
	 */
	public boolean hasPermission(String username, String perm, String type,
			String id) {
		try {
			User user = getUser(username);
			PermissionChecker checker = PermissionCheckerFactoryUtil
					.create(user);
			return checker.hasPermission(0, type, id, perm);

		} catch (Exception e) {
			throw new TechnicalCrudPortletException(
					"Error retrieving permissions for user " + username, e);
		}
	}

	private Set<String> extractRoleNames(List<Role> roles) {
		Set<String> result = new HashSet<String>();
		for (Role role : roles) {
			result.add(role.getName());
		}
		return result;
	}

	public void open(String friendlyUrl, Map<String,String> parameters) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(friendlyUrl);
		if (parameters != null) {
			for (Entry<String, String> entry : parameters.entrySet()) {
				uriBuilder.addParameter(entry.getKey(), entry.getValue());
			}
		}
		String url = uriBuilder.build().toASCIIString();
		UI.getCurrent().getPage().open(url, "_self");
	}

	public Map<String, String[]> getParameters() {
		return CrudVaadinPortletService.getCurrent().getRequestParameters();
	}

}

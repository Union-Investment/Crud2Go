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
package de.unioninvestment.eai.portal.robot;

import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.liferay.portal.model.AddressSoap;
import com.liferay.portal.model.CompanySoap;
import com.liferay.portal.model.EmailAddressSoap;
import com.liferay.portal.model.PhoneSoap;
import com.liferay.portal.model.RoleSoap;
import com.liferay.portal.model.UserSoap;
import com.liferay.portal.model.WebsiteSoap;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.http.CompanyServiceSoap;
import com.liferay.portal.service.http.CompanyServiceSoapServiceLocator;
import com.liferay.portal.service.http.ResourcePermissionServiceSoap;
import com.liferay.portal.service.http.ResourcePermissionServiceSoapServiceLocator;
import com.liferay.portal.service.http.RoleServiceSoap;
import com.liferay.portal.service.http.RoleServiceSoapServiceLocator;
import com.liferay.portal.service.http.UserServiceSoap;
import com.liferay.portal.service.http.UserServiceSoapServiceLocator;
import com.liferay.portlet.announcements.model.AnnouncementsDeliverySoap;

public class LiferayLibrary {

	private static final String PORTAL_INSTANCE_WEB_ID = "union-investment.de";
	private static final long GROUP_ID = 0L;
	private static final String RESOURCE_PERMISSION_NAME = "de.unioninvestment.eai.portal.portlet.crud.domain.model.Role";
	private static final String MEMBER_ACTION = "MEMBER";

	private Long companyId;

	private UserServiceSoap userService;
	private CompanyServiceSoap companyService;
	private RoleServiceSoap roleService;
	private ResourcePermissionServiceSoap permissionService;

	public LiferayLibrary(String baseUrl, String remoteUser, String password)
			throws Exception {
		URL userServiceUrl = getURL(baseUrl, remoteUser, password,
				"Portal_UserService");
		UserServiceSoapServiceLocator userServicelocator = new UserServiceSoapServiceLocator();
		userService = userServicelocator.getPortal_UserService(userServiceUrl);

		URL companyServiceUrl = getURL(baseUrl, remoteUser, password,
				"Portal_CompanyService");
		CompanyServiceSoapServiceLocator companyServiceLocator = new CompanyServiceSoapServiceLocator();
		companyService = companyServiceLocator
				.getPortal_CompanyService(companyServiceUrl);

		URL roleServiceUrl = getURL(baseUrl, remoteUser, password,
				"Portal_RoleService");
		RoleServiceSoapServiceLocator roleServiceLocator = new RoleServiceSoapServiceLocator();
		roleService = roleServiceLocator.getPortal_RoleService(roleServiceUrl);

		URL permissionServiceUrl = getURL(baseUrl, remoteUser, password,
				"Portal_ResourcePermissionService");
		ResourcePermissionServiceSoapServiceLocator permissionServiceLocator = new ResourcePermissionServiceSoapServiceLocator();
		permissionService = permissionServiceLocator
				.getPortal_ResourcePermissionService(permissionServiceUrl);

		CompanySoap company = companyService
				.getCompanyByWebId(PORTAL_INSTANCE_WEB_ID);
		companyId = company.getCompanyId();
	}

	public void addLiferayUser(String screenName, String password,
			String firstName, String lastName, String emailAddress, String roles)
			throws ServiceException, RemoteException {

		try {

			long[] roleIds = mapToRoleIds(roles);

			long[] userGroupIds = new long[0];
			long[] organizationIds = new long[0];
			long[] groupIds = new long[0];
			EmailAddressSoap[] emailAddresses = new EmailAddressSoap[0];
			AddressSoap[] addresses = new AddressSoap[0];
			PhoneSoap[] phones = new PhoneSoap[0];
			WebsiteSoap[] websites = new WebsiteSoap[0];
			AnnouncementsDeliverySoap[] announcementsDelivers = new AnnouncementsDeliverySoap[0];
			ServiceContext serviceContext = createServiceContext();

			UserSoap user = userService.addUser(companyId, false, password,
					password, false, screenName, emailAddress, -1L, "", "de",
					firstName, "", lastName, -1, -1, true, 1, 1, 1970, "",
					groupIds, organizationIds, roleIds, userGroupIds,
					addresses, emailAddresses, phones, websites,
					announcementsDelivers, false, serviceContext);
			userService.updateStatus(user.getUserId(), 0);
			// userService.updatePassword(user.getUserId(), password, password,
			// false);

		} catch (RemoteException e) {
			Throwable t = e;
			System.out.println(t.getMessage());
			while (t.getCause() != null) {
				t = t.getCause();
				System.out.println(t.getMessage());
			}
			e.printStackTrace();
			throw e;

		}
	}

	private ServiceContext createServiceContext() {
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setAddCommunityPermissions(false);
		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAssetCategoryIds(null);
		serviceContext.setAssetEntryVisible(true);
		serviceContext.setAssetLinkEntryIds(null);
		serviceContext.setAssetTagNames(null);
		serviceContext.setAttributes(null);
		serviceContext.setCommand("");
		serviceContext.setCommandAdd(false);
		serviceContext.setCommandUpdate(false);
		serviceContext.setCommunityPermissions(null);
		serviceContext.setCompanyId(companyId);
		serviceContext.setDeriveDefaultPermissions(false);
		serviceContext.setExpandoBridgeAttributes(null);
		serviceContext.setGroupPermissions(null);
		serviceContext.setHeaders(null);
		serviceContext.setIndexingEnabled(true);
		serviceContext.setLanguageId("de");
		serviceContext.setSignedIn(false);
		return serviceContext;
	}

	public void removeLiferayUser(String screenName) throws ServiceException,
			RemoteException {

		try {
			UserSoap user = userService.getUserByScreenName(companyId,
					screenName);
			userService.deleteUser(user.getUserId());

		} catch (RemoteException re) {
			// nichts tun
		}
	}

	public void setIndividualResourcePermissionMember(
			long resourcePermissionId, String liferayRole)
			throws ServiceException, RemoteException {
		long roleId = roleService.getRole(companyId, liferayRole).getRoleId();
		permissionService.setIndividualResourcePermissions(GROUP_ID, companyId,
				RESOURCE_PERMISSION_NAME, String.valueOf(resourcePermissionId),
				roleId, new String[] { MEMBER_ACTION });
	}

	public void deleteIndividualResourcePermissions(long resourcePermissionId,
			String liferayRole) throws ServiceException, RemoteException {
		long roleId = roleService.getRole(companyId, liferayRole).getRoleId();
		permissionService.setIndividualResourcePermissions(GROUP_ID, companyId,
				RESOURCE_PERMISSION_NAME, String.valueOf(resourcePermissionId),
				roleId, new String[] {});
	}

	private long[] mapToRoleIds(String roles) throws RemoteException {
		if (roles == null || roles.trim().length() == 0) {
			return new long[0];
		}

		String[] roleNames = roles.split(",");
		long[] roleIds = new long[roleNames.length];

		for (int i = 0; i < roleNames.length; i++) {
			RoleSoap role = roleService.getRole(companyId, roleNames[i]);
			roleIds[i] = role.getRoleId();
		}
		return roleIds;
	}

	private static URL getURL(String baseUrl, String remoteUser,
			String password, String serviceName) throws Exception {

		if (remoteUser != null) {
			// Authenticated url
			return new URL("http://" + remoteUser + ":" + password + "@"
					+ baseUrl + "/api/secure/axis/" + serviceName);
		} else {
			// Unauthenticated url
			return new URL("http://" + baseUrl + "/tunnel-web/axis/"
					+ serviceName);
		}
	}

	public String buildResourceId(String portletInstanceId, String portletRole,
			String communityId) {
		return portletInstanceId + "_" + communityId + "_" + portletRole;
	}
}

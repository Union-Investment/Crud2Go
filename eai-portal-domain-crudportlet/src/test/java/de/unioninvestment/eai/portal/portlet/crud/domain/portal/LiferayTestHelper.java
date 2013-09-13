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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactory;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.PermissionLocalService;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.RoleLocalService;
import com.liferay.portal.service.UserLocalService;

/**
 * Hilfsklasse für Liferay Mocking. In Liferay 6.1.1 werden die Services
 * (i.d.R.) über einen BeanLocator ermittelt. Die Utility-Klassen cachen jedoch
 * die Service-Instanzen, so dass die Mocks immer wieder verwendet werden
 * müssen.
 * 
 * TODO in Rule konvertieren
 * 
 * @author carsten.mjartan
 */
public class LiferayTestHelper {

	private static LiferayTestHelper helper;

	@Mock
	private Company companyMock;

	@Mock
	private CompanyLocalService companyLocalServiceMock;

	@Mock
	private UserLocalService userLocalServiceMock;

	@Mock
	private RoleLocalService roleLocalService;

	@Mock
	private ResourceLocalService resourceLocalService;

	@Mock
	private PermissionLocalService permissionLocalService;

	@Mock
	private PermissionCheckerFactory permissionCheckerFactoryMock;

	@Mock
	private PermissionChecker permissionCheckerMock;

	@Mock
	private BeanLocator beanLocatorMock;

	/**
	 * @return liefert die Klasse als Singleton
	 */
	public static LiferayTestHelper get() {
		if (helper == null) {
			helper = new LiferayTestHelper();
		}
		return helper;
	}

	/**
	 * Einmalige Instanzierung der Mocks, Registrierung und Initialisierung
	 */
	private LiferayTestHelper() {
		MockitoAnnotations.initMocks(this);

		when(beanLocatorMock.locate(CompanyLocalService.class.getName()))
				.thenReturn(companyLocalServiceMock);

		// when(beanLocatorMock.locate(PermissionCheckerFactory.class.getName()))
		// .thenReturn(permissionCheckerFactoryMock);
		// old style needed here:
		new PermissionCheckerFactoryUtil()
				.setPermissionCheckerFactory(permissionCheckerFactoryMock);

		when(beanLocatorMock.locate(UserLocalService.class.getName()))
				.thenReturn(userLocalServiceMock);
		when(beanLocatorMock.locate(RoleLocalService.class.getName()))
				.thenReturn(roleLocalService);
		when(beanLocatorMock.locate(ResourceLocalService.class.getName()))
				.thenReturn(resourceLocalService);
		when(beanLocatorMock.locate(PermissionLocalService.class.getName()))
				.thenReturn(permissionLocalService);

		PortalBeanLocatorUtil.setBeanLocator(beanLocatorMock);

		initializeMocks();
	}

	/**
	 * Default-Verhalten der Mocks
	 */
	private void initializeMocks() {
		try {
			when(permissionCheckerFactoryMock.create(any(User.class)))
					.thenReturn(permissionCheckerMock);

		} catch (Exception e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Zurücksetzen der Mocks. Sollte vor jedem Unit-Testfall aufgerufen werden.
	 */
	public void resetMocks() {
		reset(companyLocalServiceMock, permissionCheckerFactoryMock,
				userLocalServiceMock, roleLocalService, resourceLocalService,
				permissionLocalService, permissionCheckerMock);
		initializeMocks();
	}

	public Company getCompanyMock() {
		return companyMock;
	}

	public CompanyLocalService getCompanyLocalServiceMock() {
		return companyLocalServiceMock;
	}

	public UserLocalService getUserLocalServiceMock() {
		return userLocalServiceMock;
	}

	public RoleLocalService getRoleLocalService() {
		return roleLocalService;
	}

	public ResourceLocalService getResourceLocalService() {
		return resourceLocalService;
	}

	public PermissionLocalService getPermissionLocalService() {
		return permissionLocalService;
	}

	public PermissionCheckerFactory getPermissionCheckerFactoryMock() {
		return permissionCheckerFactoryMock;
	}

	public PermissionChecker getPermissionCheckerMock() {
		return permissionCheckerMock;
	}

	public BeanLocator getBeanLocatorMock() {
		return beanLocatorMock;
	}

}

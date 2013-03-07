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
package de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.crud2go.spi.security.Cryptor;
import de.unioninvestment.crud2go.spi.security.CryptorFactory;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsPasswordConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsUsernameConfig;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplicationMock;

public class RealmTest {

	private LiferayApplicationMock app;

	@Mock
	private PortletRequest requestMock;
	@Mock
	private PortletResponse responseMock;
	@Mock
	private PortletPreferences preferencesMock;
	@Mock
	private CryptorFactory cryptorFactoryMock;
	@Mock
	private Cryptor cryptorMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		app = new LiferayApplicationMock(requestMock, responseMock);
		when(requestMock.getPreferences()).thenReturn(preferencesMock);
	}

	@After
	public void tearDown() {
		app.onRequestEnd(requestMock, responseMock);
	}

	@Test
	public void shouldApplySettingsToHttpClient() {
		when(cryptorFactoryMock.getCryptor("pgp")).thenReturn(cryptorMock);
		AuthenticationRealmConfig config = createRealmConfig();
		when(preferencesMock.getValue("userKey", null)).thenReturn("user1");
		when(preferencesMock.getValue("passKey", null)).thenReturn("pass1");
		when(cryptorMock.decrypt("pass1")).thenReturn("pass2");
		Realm realm = new Realm(config, cryptorFactoryMock);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		realm.applyBasicAuthentication(httpClient);

		Credentials credentials = httpClient.getCredentialsProvider()
				.getCredentials(AuthScope.ANY);
		assertThat(credentials.getUserPrincipal().getName(), is("user1"));
		assertThat(credentials.getPassword(), is("pass2"));
	}

	private AuthenticationRealmConfig createRealmConfig() {
		CredentialsUsernameConfig username = new CredentialsUsernameConfig();
		username.setPreferenceKey("userKey");

		CredentialsPasswordConfig password = new CredentialsPasswordConfig();
		password.setPreferenceKey("passKey");
		password.setEncryptionAlgorithm("pgp");

		CredentialsConfig credentials = new CredentialsConfig();
		credentials.setUsername(username);
		credentials.setPassword(password);

		AuthenticationRealmConfig config = new AuthenticationRealmConfig();
		config.setCredentials(credentials);
		return config;
	}
}

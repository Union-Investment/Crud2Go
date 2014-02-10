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

package de.unioninvestment.eai.portal.support.scripting.http;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm;

public class HttpProviderTest {

	private HttpProvider httpProvider;

	@Mock
	private Portlet portletMock;

	@Mock
	private Realm realmMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		httpProvider = new HttpProvider(null, portletMock);
		when(portletMock.getAuthenticationRealms()).thenReturn(singletonMap("myrealm", realmMock));
	}

	@Test
	public void shouldReturnHttpClientInstance() {
		assertThat(httpProvider.doCall(), instanceOf(HttpClient.class));
	}

	@Test
	public void shouldReturnHttpClientWithoutRealmInstance() {
		assertThat(httpProvider.doCall(new HashMap<String,Object>()), instanceOf(HttpClient.class));
		verifyZeroInteractions(realmMock);
	}

	@Test
	public void shouldApplyAuthenticationSettingsToHttpClientInstance() {
		HttpClient httpClient = httpProvider.doCall(singletonMap("realm",
				(Object) "myrealm"));

		verify(realmMock).applyBasicAuthentication(
				(DefaultHttpClient) httpClient);
	}

	@Test
	public void shouldFailIfRealmIsUnknown() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Unknown realm 'unknownrealm'");

		HttpClient httpClient = httpProvider.doCall(singletonMap("realm",
				(Object) "unknownrealm"));

		verify(realmMock).applyBasicAuthentication(
				(DefaultHttpClient) httpClient);
	}
}

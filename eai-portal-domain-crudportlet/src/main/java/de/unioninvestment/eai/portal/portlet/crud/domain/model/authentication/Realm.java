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

import com.vaadin.server.VaadinPortletService;
import de.unioninvestment.crud2go.spi.security.Cryptor;
import de.unioninvestment.crud2go.spi.security.CryptorFactory;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.portlet.PortletPreferences;

public class Realm {

    private final AuthenticationRealmConfig config;
    private final CryptorFactory cryptorFactory;


	public Realm(AuthenticationRealmConfig config, CryptorFactory cryptorFactory) {
        this.config = config;
        this.cryptorFactory = cryptorFactory;

	}

	/**
	 * Applies credentials to target {@link HttpClient} with Basic
	 * Authentication (PLEASE ONLY USE THIS TOGETHER WITH SSL).
	 * 
	 * @param httpClient
	 */
	public void applyBasicAuthentication(DefaultHttpClient httpClient) {
        String username = null;
        String password = null;
        Cryptor cryptor = null;

        PortletPreferences preferences = VaadinPortletService
                .getCurrentPortletRequest().getPreferences();

        CredentialsConfig credentials = config.getCredentials();
        String usernameKey = credentials.getUsername().getPreferenceKey();
        if (usernameKey != null) {
            username = preferences.getValue(usernameKey, null);
        }

        String passwordKey = credentials.getPassword().getPreferenceKey();
        if (passwordKey != null) {
            password = preferences.getValue(passwordKey, null);
        }

        String encryptionAlgorithm = credentials.getPassword()
                .getEncryptionAlgorithm();
        if (encryptionAlgorithm != null) {
            cryptor = cryptorFactory.getCryptor(encryptionAlgorithm);
        }

		String plaintextPassword = cryptor == null ? password : cryptor
				.decrypt(password);
		httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(username, plaintextPassword));
	}
}

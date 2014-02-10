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

import groovy.lang.Closure;
import groovy.lang.Script;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm;

@SuppressWarnings("serial")
public class HttpProvider extends Closure<HttpClient> {

	private Portlet portlet;

	public HttpProvider(Script owner, Portlet portlet) {
		super(owner);
		this.portlet = portlet;
	}

	public HttpClient doCall() {
		return new DefaultHttpClient();
	}

	public HttpClient doCall(Map<String, Object> args) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (args.containsKey("realm")) {
			String realmName = (String) args.get("realm");
			Realm realm = portlet.getAuthenticationRealms().get(realmName);
			if (realm == null) {
				throw new IllegalArgumentException("Unknown realm '"
						+ realmName + "'");
			}
			realm.applyBasicAuthentication(httpClient);
		}
		return httpClient;
	}
}

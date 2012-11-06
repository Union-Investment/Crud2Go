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
package de.unioninvestment.eai.portal.support.vaadin;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.vaadin.ui.Component;

public class LiferayApplicationMock extends LiferayApplication {

	private static final long serialVersionUID = 1L;

	private String portletId;
	private long communityId;

	public LiferayApplicationMock(PortletRequest requestMock,
			PortletResponse responseMock) {
		onRequestStart(requestMock, responseMock);
	}

	public LiferayApplicationMock(PortletRequest requestMock,
			PortletResponse responseMock, String portletId, long communityId) {
		this(requestMock, responseMock);
		this.portletId = portletId;
		this.communityId = communityId;

	}

	@Override
	public void addToView(Component component) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAddedComponentsFromView() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void doInit() {
		throw new UnsupportedOperationException();
	}

	public String getPortletId() {
		return portletId;
	}

	public long getCommunityId() {
		return communityId;
	}

	public void setPortletId(String portletId) {
		this.portletId = portletId;
	}

	public void setCommunityId(long communityId) {
		this.communityId = communityId;
	}

}

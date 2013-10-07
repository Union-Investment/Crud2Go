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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration;

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;

import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.support.vaadin.liferay.PermissionsUtil;

public class DefaultPortletRolesView extends CustomComponent implements
		PortletRolesView {

	private static final long serialVersionUID = 1L;

	private VerticalLayout rolesLayout;

	public DefaultPortletRolesView(String caption) {
		setCaption(caption);

		rolesLayout = new VerticalLayout();
		rolesLayout.setMargin(true);
		rolesLayout.setSpacing(true);

		rolesLayout.addComponent(new Label(
				getMessage("portlet.crud.page.edit.roles.description")));

		setCompositionRoot(rolesLayout);
	}

	@Override
	public void display(List<PortletRoleTO> roles) {
		for (PortletRoleTO portletRole : roles) {
			ExternalResource res = new ExternalResource(
					permissionsUrl(portletRole));
			rolesLayout.addComponent(new Link(portletRole.getName(), res));
		}
	}

	private String permissionsUrl(PortletRoleTO portletRole) {
		return PermissionsUtil.buildURL(PortletRole.RESOURCE_KEY,
				portletRole.getPrimaryKey(), portletRole.getName());
	}

}

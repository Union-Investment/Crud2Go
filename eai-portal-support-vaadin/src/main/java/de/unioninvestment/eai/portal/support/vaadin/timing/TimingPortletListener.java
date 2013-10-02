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

package de.unioninvestment.eai.portal.support.vaadin.timing;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.vaadin.server.VaadinPortletSession.PortletListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

public class TimingPortletListener implements PortletListener {

	private Component timingDisplayComponent;

	public TimingPortletListener(Component timingDisplayComponent) {
		this.timingDisplayComponent = timingDisplayComponent;
	}

	@Override
	public void handleRenderRequest(RenderRequest request,
			RenderResponse response, UI uI) {
		timingDisplayComponent.markAsDirtyRecursive();
	}

	@Override
	public void handleActionRequest(ActionRequest request,
			ActionResponse response, UI uI) {
		timingDisplayComponent.markAsDirtyRecursive();
	}

	@Override
	public void handleEventRequest(EventRequest request,
			EventResponse response, UI uI) {
		timingDisplayComponent.markAsDirtyRecursive();
	}

	@Override
	public void handleResourceRequest(ResourceRequest request,
			ResourceResponse response, UI uI) {
		timingDisplayComponent.markAsDirtyRecursive();
	}

}

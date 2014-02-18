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

package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinPortletService;
import com.vaadin.ui.Label;

import de.unioninvestment.eai.portal.portlet.crud.CrudUI;
import de.unioninvestment.eai.portal.portlet.crud.services.RequestProcessingLogService;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortletService;
import de.unioninvestment.eai.portal.support.vaadin.RequestProcessingInfo;

/**
 * Label that displays request performance metrics. As it also hooks into the
 * response generation lifecycle, it's also used to submit request logging
 * information to the {@link RequestProcessingLogService}, even if the label is
 * not to be displayed.
 * 
 * @author cmj
 */
public class RequestProcessingLabel extends Label {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RequestProcessingLabel.class);

	private RequestProcessingLogService requestProcessingLogService;

	public RequestProcessingLabel(
			RequestProcessingLogService requestProcessingLogService,
			boolean visible) {
		this.requestProcessingLogService = requestProcessingLogService;
		setSizeUndefined();
		if (!visible) {
			setStyleName("hidden");
		}
	}

	@Override
	public void beforeClientResponse(boolean initial) {
		RequestProcessingInfo info = ((CrudVaadinPortletService) VaadinPortletService
				.getCurrent()).getCurrentRequestProcessingInfo();
		addRequestLogEntry(info, CrudUI.getCurrent());
		updateInfo(info);

		super.beforeClientResponse(initial);
	}

	private void addRequestLogEntry(RequestProcessingInfo info, CrudUI ui) {
		requestProcessingLogService //
				.addRequestLogEntry(info, ui);
	}

	private void updateInfo(RequestProcessingInfo info) {
		long duration = info.getTimeSinceRequestStart();
		long dbDuration = info.getMeasuredTime("db");

		String message = MessageFormat.format("{0}ms ({1}ms@db)", duration,
				dbDuration);
		setValue(message);
		LOGGER.debug("Request processed in {}", message);
	}

}

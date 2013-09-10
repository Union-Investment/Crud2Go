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
package de.unioninvestment.eai.portal.portlet.crud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.ui.Notification;

public class CrudErrorHandler extends DefaultErrorHandler {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CrudErrorHandler.class);

	@Override
	public void error(ErrorEvent event) {
		Throwable throwable = event.getThrowable();
		LOGGER.error("Internal error", throwable);
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		String message = throwable.getMessage();
		if (message != null) {
			Notification.show(message, Notification.Type.ERROR_MESSAGE);
		} else {
			Notification.show(throwable.getClass().getSimpleName(), Notification.Type.ERROR_MESSAGE);
		}
	}
}

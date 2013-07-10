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
package de.unioninvestment.eai.portal.support.vaadin.junit;

import org.mockito.ArgumentMatcher;

import com.google.gwt.thirdparty.guava.common.base.Objects;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class NotificationMatcher extends ArgumentMatcher<Notification> {

	private String caption;
	private String description;
	private Type type;

	public NotificationMatcher(String caption, String description, Type type) {
		this.caption = caption;
		this.description = description;
		this.type = type;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument instanceof Notification) {
			Notification notification = (Notification) argument;
			if (Objects.equal(notification.getCaption(), caption)
					&& Objects
							.equal(notification.getDescription(), description)) {
				return true;
			}
		}
		return false;
	}

}

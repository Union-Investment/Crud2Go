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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import groovy.lang.Closure;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

/**
 * Closure Implementation, um aus dem Skriptkontext heraus eine Notification
 * anzuzeigen.
 * 
 * @author nicolai.woeller
 */
public class NotificationProvider extends Closure<String> {
	private static final long serialVersionUID = 1L;

	private final Type type;

	/**
	 * Arten der Benachrichtigung. Diese werden auf unterschiedliche Art
	 * dargestellt.
	 * 
	 * @author carsten.mjartan
	 */
	public enum Type {
		/**
		 * Warnung
		 */
		WARNING("Warnung"),

		/**
		 * Fehlermeldung
		 */
		ERROR("Fehler"),

		/**
		 * Informationsmeldung
		 */
		INFO("Info");

		private final String value;

		Type(String v) {
			value = v;
		}
	}

	/**
	 * Konstruktor.
	 * 
	 * @param owner
	 *            Closure Kontext
	 * @param type
	 *            die Art der Information
	 */
	public NotificationProvider(Object owner, Type type) {
		super(owner);
		this.type = type;
	}

	/**
	 * Zeigt entweder eine Warnung, einen Fehler oder eine Info im Window für
	 * die entsprechende Anzeige
	 * 
	 * @param description
	 *            Der anzuzeigende Text der Notification
	 */
	public void doCall(String description) {
		if (type == Type.ERROR) {
			Notification.show(Type.ERROR.value, description,
					Notification.Type.ERROR_MESSAGE);
		} else if (type == Type.WARNING) {
			Notification.show(Type.WARNING.value, description,
					Notification.Type.WARNING_MESSAGE);
		} else {
			Notification notification = new Notification(Type.INFO.value, description,
					Notification.Type.HUMANIZED_MESSAGE);
			notification.setDelayMsec(1500);
			notification.show(Page.getCurrent());
		}
	}
}

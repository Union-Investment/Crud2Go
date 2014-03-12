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

package de.unioninvestment.eai.portal.support.vaadin.context;

import static java.util.Arrays.asList;

import java.util.Locale;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

import com.vaadin.server.VaadinPortletService;
import com.vaadin.ui.UI;

/**
 * Thread-safe {@link ContextProvider} that fetches the ApplicationContext from
 * the current PortletReq.
 * 
 * @author cmj
 * 
 */
public class UIContextProvider implements ContextProvider {

	/**
	 * Übersetzung von UI-Texten.
	 * 
	 * @param key
	 *            der Übersetzungsschlüssel
	 * @param args
	 *            Parameter
	 * @return die Übersetzung gemäß der aktuellen Locale-Konfiguration
	 */
	public String getMessage(String key, Object... args) {
		try {

			MessageSource messageSource = getBean(MessageSource.class);
			if (messageSource != null) {
				return messageSource.getMessage(key, args, getLocale());
			} else {
				return rawMessage(key, args);
			}
		} catch (IllegalStateException e) {
			return rawMessage(key, args);
		}
	}

	private String rawMessage(String key, Object... args) {
		return "#"
				+ key
				+ (args == null || args.length == 0 ? "" : asList(args));
	}

	@Override
	public Locale getLocale() {
		return UI.getCurrent().getLocale();
	}

	/**
	 * @param requiredType
	 *            der benötigte Typ eines Beans
	 * @return eine Instanz des Typs aus dem Spring ApplicationContext
	 * @throws NoSuchBeanDefinitionException
	 *             falls die Bean im Spring-Kontext nicht gefunden werden kann
	 * @throws BeansException
	 *             falls keine eindeutige Zuordnung getroffen werden kann
	 * @throws IllegalStateException
	 *             falls kein PortletRequest gefunden wird, an dem der Spring
	 *             Context hängt
	 */
	@Override
	public <T> T getBean(Class<T> requiredType) {
		return getSpringContext().getBean(requiredType);
	}

	@Override
	public ApplicationContext getSpringContext() {
		PortletRequest currentRequest = VaadinPortletService
				.getCurrentPortletRequest();
		if (currentRequest != null) {
			PortletContext pc = currentRequest.getPortletSession()
					.getPortletContext();
			org.springframework.context.ApplicationContext springContext = PortletApplicationContextUtils
					.getRequiredWebApplicationContext(pc);
			return springContext;

		} else {
			throw new IllegalStateException(
					"Found no current portlet request. Did you subclass PortletApplication in your Vaadin Application?");
		}
	}
}

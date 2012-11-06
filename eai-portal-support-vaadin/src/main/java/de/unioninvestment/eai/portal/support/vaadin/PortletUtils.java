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

import static java.util.Arrays.asList;

import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;

import org.springframework.context.MessageSource;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;

/**
 * Hilfsklasse für Vaadin Portlets.
 * 
 * @author carsten.mjartan
 */
public final class PortletUtils {

	private static org.springframework.context.ApplicationContext applicationContextMock = null;

	private PortletUtils() {
		// Utility class
	}

	/**
	 * Gibt dem Browser die Information, zu einer alternativen Ansicht des
	 * Portlets zu wechseln.
	 * 
	 * @param application
	 *            die betroffene {@link Application}
	 * @param targetMode
	 *            der Ziel-Modus
	 * @return <code>true</code>, falls ein Wechsel erfolgen kann
	 */
	public static boolean switchPortletMode(Application application,
			PortletMode targetMode) {
		try {
			ApplicationContext context = application.getContext();
			if (context instanceof PortletApplicationContext2) {
				((PortletApplicationContext2) context).setPortletMode(
						application.getMainWindow(), targetMode);
				return true;
			}
			return false;

		} catch (IllegalStateException e) {
			// not in portlet: then don't switch back
			return false;

		} catch (PortletModeException e) {
			// not allowed: then don't switch back
			return false;
		}
	}

	/**
	 * 
	 * @param code
	 *            Der Message Key.
	 * @return Die die aufgelöste Message.
	 */
	public static String getMessage(String code) {
		MessageSource messageSource = getSpringApplicationContext().getBean(
				MessageSource.class);
		return messageSource.getMessage(code, null, null);
	}

	/**
	 * 
	 * 
	 * @param code
	 *            Der Message Key
	 * @param args
	 *            Die Message Argumente
	 * @return Die die aufgelöste Message
	 */
	public static String getMessage(String code, Object... args) {
		try {
			MessageSource messageSource = getSpringApplicationContext()
					.getBean(MessageSource.class);
			return messageSource.getMessage(code, args, null);
		} catch (IllegalStateException e) {
			return code + (args != null ? asList(args) : "");
		}
	}

	/**
	 * Liefert eine Bean des angegebenen Typs aus dem Spring ApplicationContext.
	 * 
	 * @param <T>
	 *            der Typ der angefragten Spring-Bean-Instanz
	 * @param typeOfBean
	 *            Typinformation
	 * @return die Instanz
	 */
	public static <T> T getBean(Class<T> typeOfBean) {
		return getSpringApplicationContext().getBean(typeOfBean);
	}

	/**
	 * @return den der Anwendung zugehörigen Spring-ApplicationContext (in
	 *         web.xml konfiguriert).
	 */
	public static org.springframework.context.ApplicationContext getSpringApplicationContext() {
		if (applicationContextMock != null) {
			return applicationContextMock;
		} else {
			PortletRequest currentRequest = PortletApplication
					.getCurrentRequest();
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

	public static void setSpringApplicationContextMock(
			org.springframework.context.ApplicationContext ctx) {
		applicationContextMock = ctx;
	}
}

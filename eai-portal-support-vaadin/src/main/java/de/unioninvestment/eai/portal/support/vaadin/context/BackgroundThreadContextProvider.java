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

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

/**
 * Minimaler Provider für Messages und die Locale des erzeugenden Threads.
 * 
 * @author carsten.mjartan
 */
public class BackgroundThreadContextProvider implements ContextProvider {

	private MessageSource messageSource;
	private Locale locale;
	private ApplicationContext applicationContext;

	/**
	 * Initialisierung mit Daten aus aktuellem {@link Context}.
	 */
	public BackgroundThreadContextProvider() {
		this(Context.getProvider().getSpringContext(), Context.getLocale());
	}

	/**
	 * Konstruktor.
	 * 
	 * @param applicationContext
	 * @param locale
	 */
	public BackgroundThreadContextProvider(
			ApplicationContext applicationContext, Locale locale) {
		this.locale = locale;
		this.applicationContext = applicationContext;
		this.messageSource = applicationContext.getBean(MessageSource.class);
	}

	@Override
	public String getMessage(String key, Object... args) {
		if (messageSource != null) {
			return messageSource.getMessage(key, args, locale);
		} else {
			return "#" + key
					+ (args == null || args.length == 0 ? "" : asList(args));
		}
	}

	@Override
	public <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public ApplicationContext getSpringContext() {
		return applicationContext;
	}
}
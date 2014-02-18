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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.xml.sax.SAXException;

import com.cybercom.vaadin.spring.UIScope;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.persistence.DefaultConfigurationDao;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.crud.services.DefaultConfigurationService;
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory;

/**
 * Standard-Implementierung der {@link ApplicationFactory}. Delegiert teilweise
 * an PortletUtils.getBean().
 * 
 * @author carsten.mjartan
 */
@Configuration
public class SpringApplicationFactory {

	@Autowired
	private DefaultConfigurationDao configurationDao;

	@Autowired
	private Settings settings;

	@Resource(name = "dataTypes")
	private List<Object> dataTypeHelpers;

	@Bean
	static UIScope uiScope() {
		return new UIScope();
	}

	/**
	 * Erzeugt eine Instanz der Klasse EventBus.
	 * 
	 * @return EventBus
	 */
	@Bean
	@Lazy
	@Scope("ui")
	public EventBus eventBus() {
		return new EventBus();
	}

	/**
	 * Erzeugt eine Instanz der Klasse ScriptBuilder.
	 * 
	 * @return ScriptBuilder
	 */
	@Bean()
	@Lazy
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
	public ScriptBuilder scriptBuilder() {
		return new ScriptBuilder();
	}

	@Bean
	public TaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.afterPropertiesSet();
		return scheduler;
	}

	/**
	 * Erzeugt eine Instanz der Klasse FieldValidatorFactory.
	 * 
	 * @return FieldValidatorFactory
	 */
	@Bean
	public FieldValidatorFactory fieldValidatorFactory() {
		return new FieldValidatorFactory();
	}

	/**
	 * Erzeugt eine Instanz der Klasse ResetFormAction.
	 * 
	 * @return ResetFormAction
	 */
	@Bean
	public ResetFormAction resetFormAction() {
		return new ResetFormAction();
	}

	/**
	 * Erzeugt eine Instanz der Klasse ConfigurationService.
	 * 
	 * @return ConfigurationService
	 * @throws SAXException
	 *             wenn das XSD Syntaxfehler enthält
	 * @throws JAXBException
	 *             wenn das XSD Fehler enthält
	 */
	@Bean
	public ConfigurationService configurationService() throws JAXBException,
			SAXException {
		return new DefaultConfigurationService(configurationDao,
				new ConfigurationScriptsCompiler(new ScriptCompiler()),
				settings);
	}

	/**
	 * @return eine Liste datentypspezifischer Hilfsklassen in Reihenfolge der
	 *         Relevanz
	 */
	@Bean
	public List<DisplaySupport> displaySupport() {
		return getDataTypeHelpers(DisplaySupport.class);
	}

	/**
	 * @return eine Liste datentypspezifischer Hilfsklassen in Reihenfolge der
	 *         Relevanz
	 */
	@Bean
	public List<EditorSupport> editorSupport() {
		return getDataTypeHelpers(EditorSupport.class);
	}

	/*
	 * Caching
	 */

	@Bean
	public CacheManager cacheManager() throws IOException {
		return CacheManager.newInstance(cacheConfigLocation().getInputStream());
	}

	private ClassPathResource cacheConfigLocation() {
		ClassPathResource configLocation = new ClassPathResource(
				"eai/ehcache.xml",
				SpringApplicationFactory.class.getClassLoader());
		if (!configLocation.exists()) {
			configLocation = new ClassPathResource("ehcache.xml",
					SpringApplicationFactory.class.getClassLoader());
		}
		return configLocation;
	}

	@Bean
	@Qualifier("portletCache")
	public Ehcache portletCache() throws IOException {
		return cacheManager().getEhcache("portletCache");
	}

	@Bean
	@Qualifier("optionListCache")
	public Ehcache optionListCache(CacheManager cacheManager)
			throws IOException {
		return cacheManager().getEhcache("optionListCache");
	}

	/**
	 * @param <T>
	 *            der Typ der benötigten Hilfsklassen
	 * @param clazz
	 *            der Typ der benötigten Hilfsklassen
	 * @return eine Liste der Hilfsklassen aus dem Spring ApplicationContext
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> getDataTypeHelpers(Class<T> clazz) {

		LinkedList<T> results = new LinkedList<T>();
		for (Object helper : dataTypeHelpers) {
			if (clazz.isAssignableFrom(helper.getClass())) {
				results.add((T) helper);
			}
		}
		return results;
	}

}

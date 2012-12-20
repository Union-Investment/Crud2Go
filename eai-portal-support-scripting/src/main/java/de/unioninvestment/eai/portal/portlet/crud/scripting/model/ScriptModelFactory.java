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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.JmxDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.scripting.ScriptJMXWrapper;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;

/**
 * Factory für Script-Modell-Instanzen. Nicht für die Nutzung aus Scripten
 * bestimmt.
 * 
 * @author carsten.mjartan
 */
@Component
public class ScriptModelFactory {

	private final ConnectionPoolFactory connectionPoolFactory;
	private final UserFactory userFactory;

	/**
	 * @param connectionPoolFactory
	 *            für den Zugriff auf SQL-DataSources
	 * 
	 * @param userFactory
	 *            Kactory-Objekt zum Erzeugen von Benutzer-Objekten.
	 */
	@Autowired
	public ScriptModelFactory(ConnectionPoolFactory connectionPoolFactory,
			UserFactory userFactory) {
		this.connectionPoolFactory = connectionPoolFactory;
		this.userFactory = userFactory;
	}

	/**
	 * Erstellt eine Builder-Instanz.
	 * 
	 * @param portlet
	 *            das Modell, für den das Scriptmodell zu erstellen ist
	 * @param modelToConfigMapping
	 *            das Mapping von Modell-Instanzen auf Config-Instanzen, das vom
	 *            {@link ModelBuilder} geliefert wird.
	 * @return die Builder-Instanz
	 */
	public ScriptModelBuilder getBuilder(Portlet portlet,
			Map<Object, Object> modelToConfigMapping) {
		return new ScriptModelBuilder(this, connectionPoolFactory, userFactory,
				getScriptBuilder(), portlet, modelToConfigMapping);
	}

	public ScriptBuilder getScriptBuilder() {
		return new ScriptBuilder();
	}

	/**
	 * @param action
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptTableAction getScriptTableAction(TableAction action) {
		return new ScriptTableAction(action);
	}

	/**
	 * @param page
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptPage getScriptPage(Page page) {
		return new ScriptPage(page);
	}

	/**
	 * @param portlet
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptPortlet getScriptPortlet(Portlet portlet) {
		return new ScriptPortlet(portlet);
	}

	/**
	 * @param table
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptTable getScriptTable(PortletApplication application,
			Table table) {
		return new ScriptTable(application, table);
	}

	/**
	 * @param form
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptForm getScriptForm(Form form) {
		return new ScriptForm(form);
	}

	/**
	 * @param field
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptFormField getFormField(FormField field) {
		if (field instanceof MultiOptionListFormField) {
			return new ScriptMultiOptionListFormField(
					(MultiOptionListFormField) field);

		} else if (field instanceof OptionListFormField) {
			return new ScriptOptionListFormField((OptionListFormField) field);

		} else {
			return new ScriptFormField(field);
		}
	}

	/**
	 * @param tabs
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptTabs getScriptTabs(Tabs tabs) {
		return new ScriptTabs(tabs);
	}

	/**
	 * @param tab
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptTab getScriptTab(Tab tab) {
		return new ScriptTab(tab);
	}

	/**
	 * @param region
	 *            die Region Instanz aus dem Modell
	 * @return eine neue ScriptRegion Instanz.
	 */
	public ScriptRegion getScriptRegion(Region region) {
		return new ScriptRegion(region);
	}

	/**
	 * @param action
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptFormAction getFormAction(FormAction action) {
		if (action.getActionHandler() instanceof SearchFormAction) {
			return new ScriptSearchFormAction(action);
		} else {
			return new ScriptFormAction(action);
		}
	}

	/**
	 * @param container
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptDatabaseContainer getScriptDatabaseContainer(
			DataContainer container) {
		return new ScriptDatabaseContainer(container);
	}

	/**
	 * @param container
	 *            die entsprechende Instanz aus dem Modell
	 * @return eine neue Wrapper-Klasse
	 */
	public ScriptDatabaseQueryContainer getScriptDatabaseQueryContainer(
			DataContainer container) {
		return new ScriptDatabaseQueryContainer(container);
	}

	/**
	 * Erzeugt ein ScriptUser-Objekt.
	 * 
	 * @param authenticatedUser
	 *            authentifizierter User
	 * @return User authentifizierter User
	 */
	public ScriptUser getScriptUser(User authenticatedUser) {
		return new ScriptUser(authenticatedUser);
	}

	/**
	 * Erzeugt ScriptCurrentUser-Objekt.
	 * 
	 * @param currentUser
	 *            Aktuell angemelderter Benutzer
	 * @return Aktuell angemelderter User
	 */
	public ScriptCurrentUser getScriptCurrentUser(CurrentUser currentUser) {
		return new ScriptCurrentUser(currentUser);
	}

	public ScriptJMXContainer getScriptJmxContainer(DataContainer container,
			JmxDelegate delegate) {
		return new ScriptJMXContainer(new ScriptJMXWrapper(
				delegate.getJmxWrapper()), container, delegate);
	}

	public ScriptContainer getScriptContainer(DataContainer container) {
		return new ScriptContainer(container);
	}
}

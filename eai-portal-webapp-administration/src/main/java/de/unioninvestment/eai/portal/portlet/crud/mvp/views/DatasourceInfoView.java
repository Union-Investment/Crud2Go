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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Die View der Datenquellen-Infoübersicht.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DatasourceInfoView extends VerticalLayout implements View {

	/**
	 * Tabelle, die bei leeren Feldern einen Standardtext anzeigt.
	 * 
	 * @author carsten.mjartan
	 */
	private static final class DataSourceInfoTable extends Table {
		private final String defaultText;
		private static final long serialVersionUID = 1L;

		private DataSourceInfoTable(String caption, String defaultText) {
			super(caption);
			this.defaultText = defaultText;
		}

		@Override
		protected Object getPropertyValue(Object rowId, Object colId,
				@SuppressWarnings("rawtypes") Property property) {
			Object result = super.getPropertyValue(rowId, colId, property);
			if (result == null) {
				return defaultText;
			}
			return result;
		}
	}

	private static final long serialVersionUID = 1L;

	static final String DATASOURCENAME_PROPERTYNAME = "datasourceName";
	static final String JNDINAME_PROPERTYNAME = "jndiName";
	static final String TYPE_PROPERTYNAME = "type";
	static final String USERNAME_PROPERTYNAME = "userName";
	static final String CONNECTIONURL_PROPERTYNAME = "connectionURL";

	private final Table table;

	/**
	 * Erzeugt eine neue Instanz dieser View.
	 * 
	 * @param container
	 *            die Container-Instanz mit den Daten.
	 */
	public DatasourceInfoView(Container container) {

		this.setReadOnly(true);

		final String notAvailable = getMessage("portlet.crud.page.help.datasourceInfo.notAvailable");

		this.table = new DataSourceInfoTable(
				getMessage("portlet.crud.page.help.datasourceInfo.header"),
				notAvailable);
		this.table.setWidth("100%");
		this.table.setContainerDataSource(container);
		this.table.setColumnHeader(DATASOURCENAME_PROPERTYNAME,
				getMessage("portlet.crud.page.help.datasourceInfo.name"));
		this.table.setColumnHeader(JNDINAME_PROPERTYNAME,
				getMessage("portlet.crud.page.help.datasourceInfo.jndiName"));
		this.table.setColumnHeader(TYPE_PROPERTYNAME,
				getMessage("portlet.crud.page.help.datasourceInfo.type"));
		this.table.setColumnHeader(USERNAME_PROPERTYNAME,
				getMessage("portlet.crud.page.help.datasourceInfo.username"));
		this.table
				.setColumnHeader(
						CONNECTIONURL_PROPERTYNAME,
						getMessage("portlet.crud.page.help.datasourceInfo.connectionUrl"));
		this.table.setVisibleColumns(new Object[] {
				DATASOURCENAME_PROPERTYNAME, JNDINAME_PROPERTYNAME,
				TYPE_PROPERTYNAME, USERNAME_PROPERTYNAME,
				CONNECTIONURL_PROPERTYNAME });
		// this.table.setColumnWidth(DATASOURCENAME_PROPERTYNAME, 150);
		// this.table.setColumnWidth(USERNAME_PROPERTYNAME, 150);
		table.setPageLength(0);

		this.addComponent(this.table);
	}

}

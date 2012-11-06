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
package de.unioninvestment.eai.portal.support.vaadin.support;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Select;

/**
 * Spezielle Implementierung von {@link Select}, die das Parsen und konvertieren
 * der Auswahl-Keys unterstützt.
 * 
 * @author carsten.mjartan
 * 
 */
public class FormattedSelect extends Select {

	private static final long serialVersionUID = 1L;

	private PropertyFormatter formatter;

	/**
	 * 
	 * @param formatter
	 *            der Formatter der für die spätere DataSource verwendet werden
	 *            soll
	 */
	public FormattedSelect(PropertyFormatter formatter) {
		super();
		this.formatter = formatter;
		this.setInvalidAllowed(false);
	}

	/**
	 * Übergibt die DataSource an den Formatter.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		formatter.setPropertyDataSource(newDataSource);
		super.setPropertyDataSource(formatter);
	}
}

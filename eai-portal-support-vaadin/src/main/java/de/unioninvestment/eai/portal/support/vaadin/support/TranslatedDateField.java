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

import org.vaadin.addon.propertytranslator.PropertyTranslator;

import com.vaadin.data.Property;
import com.vaadin.ui.PopupDateField;

/**
 * Spezielle Implementierung von PopupDateField die das Parsen und konvertieren
 * der Eingabe unterstützt.
 * 
 * Nicht mehr notwendig in Vaadin 7.
 * 
 * @author carsten.mjartan
 * 
 */
public class TranslatedDateField extends PopupDateField {

	private static final long serialVersionUID = 1L;

	private PropertyTranslator translator;

	/**
	 * 
	 * @param translator
	 *            der Translator der für die spätere DataSource verwendet werden
	 *            soll
	 */
	public TranslatedDateField(PropertyTranslator translator) {
		this.translator = translator;
	}

	/**
	 * Übergibt die DataSource an den Translator.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		translator.setPropertyDataSource(newDataSource);
		super.setPropertyDataSource(translator);
	}
}

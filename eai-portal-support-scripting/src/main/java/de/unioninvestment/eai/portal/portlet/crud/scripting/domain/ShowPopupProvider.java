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
import groovy.xml.MarkupBuilder;

import java.io.StringWriter;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * Closure Implementation um aus dem Skriptkontext ein Popup Fenster zu
 * initialisieren.
 * 
 * @author markus.bonsch
 * 
 */
@Configurable
public class ShowPopupProvider extends Closure<String> {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EventBus eventBus;

	/**
	 * Konstruktor.
	 * 
	 * @param owner
	 *            Closure Kontext
	 */
	public ShowPopupProvider(Object owner) {
		super(owner);
	}

	/**
	 * 
	 * @param titel
	 *            - Titel des Popup Fensters
	 * @param closure
	 *            - Das Closure Attribute um im Script mit Hilfe des
	 *            groovy.xml.MarkupBuilder XHMTL Inhalt zu erstellen.
	 */
	public void doCall(String titel, Closure<?> closure) {
		Writer writer = new StringWriter();
		MarkupBuilder markupBuilder = new MarkupBuilder(writer);

		markupBuilder.invokeMethod("div", closure);
		
		eventBus.fireEvent(new ShowPopupEvent(titel, writer.toString(),
				ShowPopupEvent.CONTENT_TYPE_XHTML));
	}

	/**
	 * 
	 * @param titel
	 *            - Titel des Popup Fensters
	 * @param content
	 *            - Der Anzeigetext, Vorformatierungen (z.B. Zeilenumbrücke)
	 *            werden berücksichtigt
	 */
	public void doCall(String titel, String content) {
		eventBus.fireEvent(new ShowPopupEvent(titel, content,
				ShowPopupEvent.CONTENT_TYPE_PLAIN));
	}

	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}

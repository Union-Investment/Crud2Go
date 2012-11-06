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
package de.unioninvestment.eai.portal.support.vaadin.container;

import java.util.List;

import com.vaadin.data.Container.Filter;

/**
 * Schnittstelle für Backends die mit dem GenericContainer zusammenarbeiten.
 * 
 * @author carsten.mjartan
 */
public interface GenericDelegate {

	/**
	 * @return Metadaten zu Spalten und Änderungsmöglichkeiten des Backends
	 */
	MetaData getMetaData();

	/**
	 * @return die aktuelle Zeilenliste gemäß Filterkriterien. Die Werte im
	 *         Object-Array müssen den {@link Column}-Definitionen in den
	 *         Metadaten entsprechen
	 */
	List<Object[]> getRows();

	/**
	 * @param filters
	 *            die Filter
	 */
	void setFilters(Filter[] filters);

	/**
	 * @param items
	 *            die beim commit() des Containers zu übernehmenden Änderungen
	 */
	void update(List<GenericItem> items);
}

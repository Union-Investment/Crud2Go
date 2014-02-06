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

import java.util.regex.Pattern;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletCaching;

/**
 * Zugriff auf Caching-Mechanismen von Crud2Go.
 * 
 * @author cmj
 *
 */
public class ScriptPortletCaching {

	private PortletCaching delegate;

	ScriptPortletCaching(PortletCaching delegate) {
		this.delegate = delegate;
	}

	/**
	 * Entfernt die gecacheten Ergebnisse der angegebenen Query.
	 * 
	 * @param dataSource
	 *            der Datenquellen-Kurzname
	 * @param query
	 *            die Query
	 * @return <code>true</code>, falls die Query im Cache gefunden wurde
	 */
	public boolean invalidateQueryOptionList(String dataSource, String query) {
		return delegate.invalidateQueryOptionList(dataSource, query);
	}

	/**
	 * Entfernt die gecacheten Ergebnisse aller zum Pattern passenden Queries.
	 * 
	 * @param dataSource
	 *            der Datenquellen-Kurzname
	 * @param normalizedQueryPattern
	 *            der reguläre Ausdruck gegen den geprüft wird. Wichtig ist
	 *            hierbei, das gegen die normalisierte form der Queries gematcht
	 *            wird. (Zeilen werden getrimmt, leere Zeilen entfernt und
	 *            Zeilenumbrüche werden in Spaces umgewandelt)
	 * @return die Anzahl gefundener Queries
	 */
	public int invalidateQueryOptionLists(String dataSource,
			Pattern normalizedQueryPattern) {
		return delegate.invalidateQueryOptionLists(dataSource, normalizedQueryPattern);
	}

}

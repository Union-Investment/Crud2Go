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
package de.unioninvestment.eai.portal.portlet.crud.export;

import com.vaadin.server.StreamResource;

/**
 * Schnittstelle, über die ein Frontend Updates von im separaten Thread
 * laufenden Exports empfangen werden können.
 * 
 * @author carsten.mjartan
 */
public interface ExportFrontend {
	/**
	 * @param progress
	 *            Aktueller Fortschrittstatus als Wert zwischen 0 und 1
	 */
	public void updateProgress(float progress);

	/**
	 * Meldung, dass die Verarbeitung beendet ist (nicht bei Abbruch).
	 */
	public void finished();

	/**
	 * Meldung, dass die Verarbeitung beendet ist (nicht bei Abbruch).
	 * 
	 * @param resource
	 *            für den Link
	 */
	public void finished(StreamResource resource);

	/**
	 * Methode, die vom Export-Thread aufgerufen wird, wenn ein Fehler
	 * aufgetreten ist.
	 * 
	 * @param e
	 *            der Fehler
	 */
	public void handleException(Exception e);

}
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

/**
 * Interface für Export Tasks. Exporte sind als {@link Runnable} für die
 * Ausführung in einem separaten Thread ausgelegt.
 * 
 * @author carsten.mjartan
 */
public interface ExportTask extends Runnable {
	/**
	 * Wird hier ein Frontend angegeben, kann dies über Statusänderungen
	 * informiert werden.
	 * 
	 * @param frontend
	 *            das über Status-Updates zu informierende Frontend
	 */
	void setFrontend(ExportFrontend frontend);

	/**
	 * Triggert den Versand des Exports an den Client nach Beendigung des
	 * Threads.
	 * 
	 * @param targetWindow
	 *            das Zielfenster
	 */
	void sendToClient(String targetWindow);

	/**
	 * Beendet die Thread-Ausführung. Die Methode blockiert, bis der Task seine
	 * Beendigung gemeldet hat.
	 */
	void cancel();

	/**
	 * @return true, falls die Verarbeitung beendet ist.
	 */
	boolean isFinished();

}

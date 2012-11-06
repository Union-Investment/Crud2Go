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
package de.unioninvestment.eai.portal.portlet.crud.domain.exception;

/**
 * 
 * Technischer Fehler.
 * 
 * @author siva.selvarajah
 */
public class TechnicalCrudPortletException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg
	 *            Meldung
	 */
	public TechnicalCrudPortletException(String msg) {
		super(msg);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param msg
	 *            Meldung
	 * @param cause
	 *            Exception
	 */
	public TechnicalCrudPortletException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

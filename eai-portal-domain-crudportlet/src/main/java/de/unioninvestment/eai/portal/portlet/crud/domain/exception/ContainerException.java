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

import java.sql.SQLException;

/**
 * Eine Modell Exception für container-basierte Ausnahmen.
 * 
 * @author markus.bonsch
 * 
 */
public class ContainerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 */
	public ContainerException() {
		super();
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param message
	 *            Message
	 * @param cause
	 *            Throwable
	 */
	public ContainerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param message
	 *            Message
	 */
	public ContainerException(String message) {
		super(message);
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param cause
	 *            Throwable
	 */
	public ContainerException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		if (getCause() instanceof SQLException) {
			SQLException sqle = (SQLException) getCause();
			return sqle.getMessage();
		}
		return super.getMessage();
	}

}

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
package de.unioninvestment.eai.portal.support.vaadin.validation;

import java.text.MessageFormat;

import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;

/**
 * Validierungs-Fehler.
 * 
 * @author max.hartmann
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String code;
	private final Object[] args;

	/**
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 */
	public ValidationException(String code) {
		super();
		this.code = code;
		this.args = null;
	}

	/**
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 * @param args
	 * 			  Parameter
	 */
	public ValidationException(String code, Object... args) {
		super();
		this.code = code;
		this.args = args;
	}
	
	@Override
	public String getMessage() {
		return PortletUtils.getMessage(code, args);
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

}

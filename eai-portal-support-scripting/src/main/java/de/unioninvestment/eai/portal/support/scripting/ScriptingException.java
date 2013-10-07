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
package de.unioninvestment.eai.portal.support.scripting;

import java.text.MessageFormat;

import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;

/**
 * Scripting-Fehler.
 * 
 * @author max.hartmann
 */
public class ScriptingException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String code;
	private final Object[] args;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param cause
	 *            Throwable
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 */
	public ScriptingException(Throwable cause, String code) {
		super(cause);
		this.code = code;
		this.args = null;
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param cause
	 *            Throwable
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 * @param args
	 *            Parameter
	 */
	public ScriptingException(Throwable cause, String code, Object... args) {
		super(cause);
		this.code = code;
		this.args = args;
	}

	@Override
	public String getMessage() {
		return Context.getMessage(code, args);
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

}

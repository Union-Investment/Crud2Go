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

import java.text.MessageFormat;

import de.unioninvestment.eai.portal.support.vaadin.context.Context;

/**
 * Fachlicher Fehler, der in der View in eine lesbare Fehlermeldung umgesetzt
 * wird.
 * 
 * @author carsten.mjartan
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String code;
	private final Object[] args;
	private String freeformMessage;

	/**
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 */
	public BusinessException(String code) {
		this(code, (Object[]) null);
	}

	/**
	 * @param code
	 *            der Text-Code im {@link MessageFormat} Format
	 * @param args
	 *            Parameter
	 */
	public BusinessException(String code, Object... args) {
		super();
		this.code = code;
		this.args = args;
		this.freeformMessage = null;
	}

	/**
	 * @param message
	 *            the freeform message text (that is not translated)
	 * @return the created exception
	 */
	public static BusinessException withFreeformMessage(String message) {
		BusinessException exception = new BusinessException(null);
		exception.freeformMessage = message;
		return exception;
	}

	@Override
	public String getMessage() {
		if (freeformMessage != null) {
			return freeformMessage;
		}
		return Context.getMessage(code, args);
	}

	/**
	 * @return the error code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the error arguments
	 */
	public Object[] getArgs() {
		return args;
	}

}

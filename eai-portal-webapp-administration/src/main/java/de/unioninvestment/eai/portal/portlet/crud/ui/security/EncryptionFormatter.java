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
package de.unioninvestment.eai.portal.portlet.crud.ui.security;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.converter.Converter;

import de.unioninvestment.crud2go.spi.security.Cryptor;

public class EncryptionFormatter implements Converter<String, String> {

	private Cryptor cryptor;

	public EncryptionFormatter(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	@Override
	public String convertToModel(String value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return cryptor.encrypt(value);
	}

	@Override
	public String convertToPresentation(String value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (StringUtils.isNotBlank(value)) {
			try {
				return cryptor.decrypt(value);
			} catch (RuntimeException e) {
				throw new ConversionException("Cannot decrypt: "
						+ e.getMessage());
			}
		} else {
			return null;
		}
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}

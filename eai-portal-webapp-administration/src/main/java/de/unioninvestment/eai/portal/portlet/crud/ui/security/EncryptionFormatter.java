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

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;

import de.unioninvestment.crud2go.spi.security.Cryptor;

public class EncryptionFormatter extends PropertyFormatter {

	private Cryptor cryptor;

	public EncryptionFormatter(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	public EncryptionFormatter(Cryptor cryptor, Property propertyDataSource) {
		this.cryptor = cryptor;
		setPropertyDataSource(propertyDataSource);
	}

	/**
	 * Do not return password.
	 */
	@Override
	public String format(Object value) {
		return cryptor.decrypt((String) value);
	}

	/**
	 * Encrypt given password.
	 */
	@Override
	public Object parse(String formattedValue) throws Exception {
		return cryptor.encrypt(formattedValue);
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}
}

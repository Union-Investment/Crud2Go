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
package de.unioninvestment.crud2go.spi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.unioninvestment.crud2go.spi.security.pgp.PGPCryptor;

/**
 * Factory for {@link Cryptor} instances.
 * 
 * @author carsten.mjartan
 */
@Component
public class CryptorFactory {

	@Autowired
	NopCryptor nopCryptor;

	@Autowired
	PGPCryptor pgpCryptor;

	/**
	 * @param name
	 *            the required cryptor or <code>null</code>
	 * @return the cryptor instance of {@link NopCryptor}, if <code>null</code>
	 *         is given.
	 */
	public Cryptor getCryptor(String name) {
		if (StringUtils.hasText(name)) {
			if (name.equals("pgp")) {
				return pgpCryptor;
			} else {
				throw new IllegalArgumentException("Decryptor '" + name
						+ "' unkown");
			}
		} else {
			return nopCryptor;
		}
	}
}

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.crud2go.spi.security.pgp.PGPCryptor;

public class CryptorFactoryTest {

	private CryptorFactory factory;

	@Mock
	private PGPCryptor pgpCryptorMock;
	@Mock
	private NopCryptor nopCryptorMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		factory = new CryptorFactory();
		factory.pgpCryptor = pgpCryptorMock;
		factory.nopCryptor = nopCryptorMock;
	}

	@Test
	public void shouldReturnPGPCryptor() {
		assertThat(factory.getCryptor("pgp"), is((Cryptor) pgpCryptorMock));
	}

	@Test
	public void shouldReturnNopCryptorOnNull() {
		assertThat(factory.getCryptor(null), is((Cryptor) nopCryptorMock));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnUnknownName() {
		factory.getCryptor("unknown");
	}
}

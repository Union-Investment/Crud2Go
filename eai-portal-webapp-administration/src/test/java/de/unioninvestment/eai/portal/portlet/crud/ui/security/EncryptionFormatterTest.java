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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.crud2go.spi.security.Cryptor;

public class EncryptionFormatterTest {

	private EncryptionFormatter formatter;

	@Mock
	private Cryptor cryptorMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		formatter = new EncryptionFormatter(cryptorMock);
	}

	@Test
	public void shouldReturnNullForNull() {
		assertThat(formatter.convertToPresentation(null, String.class, null),
				nullValue());
	}

	@Test
	public void shouldReturnNullForEmptyEncryptedString() {
		when(cryptorMock.decrypt(""))
				.thenThrow(new NullPointerException("NPE"));
		assertThat(formatter.convertToPresentation("", String.class, null),
				nullValue());
	}

	@Test
	public void shouldReturnDecryptedValue() {
		when(cryptorMock.decrypt("encryptedText")).thenReturn("plainText");

		assertThat(formatter.convertToPresentation("encryptedText",
				String.class, null), is("plainText"));
	}

	@Test
	public void shouldParseNullAsNull() {
		assertThat(formatter.convertToModel(null, String.class, null),
				is(nullValue()));
	}

	@Test
	public void shouldForwardEncryptedValue() {
		when(cryptorMock.encrypt("plainText")).thenReturn("encryptedText");
		assertThat(formatter.convertToModel("plainText", String.class, null),
				is("encryptedText"));
	}

	@Test
	public void shouldHandleStringTypes() {
		assertEquals(String.class, formatter.getPresentationType());
		assertEquals(String.class, formatter.getModelType());
	}
}

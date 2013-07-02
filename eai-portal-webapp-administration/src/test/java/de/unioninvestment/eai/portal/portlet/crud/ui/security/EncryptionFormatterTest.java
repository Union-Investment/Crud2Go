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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;

import de.unioninvestment.crud2go.spi.security.Cryptor;

public class EncryptionFormatterTest {

	private EncryptionFormatter formatter;

	@Mock
	private Cryptor cryptorMock;
	@Mock
	private Property backingProperty;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		formatter = new EncryptionFormatter(cryptorMock, backingProperty);
	}

	@Test
	public void shouldReturnNullForNull() {
		when(backingProperty.getValue()).thenReturn(null);
		assertThat(formatter.getValue(), nullValue());
	}

	@Test
	public void shouldReturnNullForEmptyEncryptedString() {
		when(backingProperty.getValue()).thenReturn("");
		when(cryptorMock.decrypt(""))
				.thenThrow(new NullPointerException("NPE"));
		assertThat(formatter.getValue(), nullValue());
	}

	@Test
	public void shouldReturnDecryptedValue() {
		when(backingProperty.getValue()).thenReturn("encryptedText");
		when(cryptorMock.decrypt("encryptedText")).thenReturn("plainText");

		assertThat(formatter.getValue(), is((Object) "plainText"));
	}

	@Test
	public void shouldParseNullAsNull() {
		when(backingProperty.getValue()).thenReturn("bla");
		formatter.setValue(null);
		verify(backingProperty).setValue(null);
	}

	@Test
	public void shouldForwardEncryptedValue() {
		when(cryptorMock.encrypt("plainText")).thenReturn("encryptedText");
		formatter.setValue("plainText");
		verify(backingProperty).setValue("encryptedText");
	}

	@Test
	public void shouldReturnStringType() {
		assertEquals(String.class, formatter.getType());
	}
}

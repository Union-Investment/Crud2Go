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
package de.unioninvestment.eai.portal.portlet.crud.beans;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class J2EEConnectionPoolTest {

	@Mock
	private DataSource dataSourceMock;

	@Mock
	public Connection connectionMock;

	private J2EEConnectionPool pool;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		reset(InitialContextFactoryMock.getMock());
		System.setProperty("java.naming.factory.initial",
				"de.unioninvestment.eai.portal.portlet.crud.beans.InitialContextFactoryMock");
		pool = new J2EEConnectionPool("jdbc/{0}Ds", "my");
	}

	@Test(expected = IllegalStateException.class)
	public void testGenerateJndiNameMissingPattern() {
		new J2EEConnectionPool(null, "my");
	}

	@Test(expected = IllegalStateException.class)
	public void testGenerateJndiNameMissingShortName() {
		new J2EEConnectionPool("jdbc/{0}Ds", null);
	}

	@Test
	public void shouldGenerateJndiNameFromShortNameAndPattern() {
		assertEquals("jdbc/myDs", pool.getJndiName());
	}

	@Test
	public void shouldGenerateUseShortNameAsJndiNameIfStartingWithJava() {
		pool = new J2EEConnectionPool("jdbc/{0}Ds", "java:/jdbc/myDs");
		assertEquals("java:/jdbc/myDs", pool.getJndiName());
	}
	
	@Test
	public void testLookupDataSource() throws NamingException {
		when(InitialContextFactoryMock.getMock().lookup("jdbc/myDs"))
				.thenReturn(dataSourceMock);

		J2EEConnectionPool pool = new J2EEConnectionPool("jdbc/{0}Ds", "my");

		assertEquals(dataSourceMock, pool.lookupDataSource());
	}

	@Test(expected = NamingException.class)
	public void testLookupDataSourceNotExisting() throws NamingException {
		when(InitialContextFactoryMock.getMock().lookup("jdbc/myDs"))
				.thenThrow(new NameNotFoundException("Not bound"));

		pool.lookupDataSource();
	}

}

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

import static org.mockito.Mockito.mock;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Mocking {@link InitialContextFactory} für Unit Tests
 */
public class InitialContextFactoryMock implements InitialContextFactory {

	private static Context mock = mock(Context.class);

	/**
	 * {@inheritDoc}
	 */
	public Context getInitialContext(Hashtable<?, ?> environment)
			throws NamingException {
		return mock;
	}

	/**
	 * @return eine Referenz auf das Mockito-Mock der {@link Context} Klasse
	 */
	public static Context getMock() {
		return mock;
	}

}
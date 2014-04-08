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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class JolokiaJMXConnectorTest {

	private static String TEST_URL = "http://host:port/jolokia";
	private JolokiaJMXConnector jolokiaJMXConnector;

	@Test
	public void shouldCreateConnection() throws IOException {
		jolokiaJMXConnector = new JolokiaJMXConnector();
		jolokiaJMXConnector.connect();
		assertNotNull(jolokiaJMXConnector.getConnection());

	}

	@Test
	public void shouldSetUrl() throws IOException {
		jolokiaJMXConnector = new JolokiaJMXConnector();
		jolokiaJMXConnector.setUrl(TEST_URL);
		assertTrue(TEST_URL.equals(jolokiaJMXConnector.getUrl()));
	}

}

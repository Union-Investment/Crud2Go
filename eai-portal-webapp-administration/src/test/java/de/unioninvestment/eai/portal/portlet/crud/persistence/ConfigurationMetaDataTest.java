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
package de.unioninvestment.eai.portal.portlet.crud.persistence;


import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationMetaDataTest {

//	private ConfigurationMetaData configurationMetaData;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@Test
	public void sholdStoreAttributes() {
		String user = "user";
		Date created = new Date();
		Date updated = new Date();
		ConfigurationMetaData configurationMetaData = new ConfigurationMetaData(user, created, updated, null);
		
		assertEquals(user, configurationMetaData.getUser());
		assertEquals(created, configurationMetaData.getCreated());
		assertEquals(updated, configurationMetaData.getUpdated());
	}
}

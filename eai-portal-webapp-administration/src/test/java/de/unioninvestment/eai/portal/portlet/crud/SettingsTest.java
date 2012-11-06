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
package de.unioninvestment.eai.portal.portlet.crud;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {

	private Settings settings;

	@Before
	public void setUp() {
		settings = new Settings();
		settings.setPropertiesLocation(SettingsTest.class
				.getResource("settings-test.properties"));
	}

	@Test
	public void shouldReturnDatasourcePatternByCommunity() {
		assertThat(settings.getDatasourcePattern(12141L),
				is("java:/this/is/a/pattern"));
	}

	@Test
	public void shouldReturnInfoPatternByCommunity() {
		assertThat(settings.getDatasourceInfoPattern(12141L), is("a/pattern"));
	}

	@Test
	public void shouldShowWarningIfPropertiesFileDoesNotExist() {
		settings.setPropertiesLocation(SettingsTest.class
				.getResource("not-existing.properties"));
		assertThat(settings.getDatasourcePattern(12345L), nullValue());
	}

	@Test
	public void shouldReturnNullIfCommunityIsUnknown() {
		assertThat(settings.getDatasourcePattern(12345L), nullValue());
	}
}

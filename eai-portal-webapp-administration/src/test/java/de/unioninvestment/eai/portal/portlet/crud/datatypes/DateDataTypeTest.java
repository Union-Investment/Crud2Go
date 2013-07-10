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
package de.unioninvestment.eai.portal.portlet.crud.datatypes;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateDataTypeTest {

	@Test
	public void shouldSupportDisplayingDate() {
		DateDataType dataType = new DateDataType();
		boolean result = dataType.supportsDisplaying(Date.class);

		Assert.assertTrue(result);
	}

	@Test
	public void shouldSupportEditingDate() {
		DateDataType dataType = new DateDataType();
		boolean result = dataType.supportsEditing(Date.class);

		Assert.assertTrue(result);
	}

	@Test
	public void createFormatter() {
	
	}
}

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
package de.unioninvestment.eai.portal.support.vaadin.filter;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;

public class SQLFilterTest {

	private SQLFilter sqlFilter;
	
	@Mock
	private Object itemIdMock;
	
	@Mock
	private Item itemMock;
	
	@Mock
	private java.util.List<Object> paramsMock;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldPassesFilter() {
		sqlFilter = new SQLFilter("TestCOL", null, null);
		assertThat(sqlFilter.passesFilter(itemIdMock, itemMock), is(TRUE));
	}
	
	@Test
	public void shouldAppliesToProperty() {
		sqlFilter = new SQLFilter("TestCOL", null, null);
		assertThat(sqlFilter.appliesToProperty("TestCOL"), is(TRUE));
	}

	@Test
	public void shouldReturnWhereString() {
		sqlFilter = new SQLFilter("TestCOL", "1 = ?", null);
		assertThat(sqlFilter.getWhereString(), is("1 = ?"));
	}

	@Test
	public void shouldReturnParameters() {
		sqlFilter = new SQLFilter(null, null, paramsMock);
		assertThat(sqlFilter.getValues().size(), is(paramsMock.size()));
	}
}

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
package de.unioninvestment.eai.portal.support.vaadin.container;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MetaDataTest {

	private Column col1;
	private Column col2;
	private MetaData metaData;

	@Before
	public void setUp() {
		col1 = new Column("COL1", String.class, false, false, false, null);
		col2 = new Column("COL2", String.class, false, false, true, null);
		metaData = new MetaData(asList(col1, col2), false, false, false, false,
				false);
	}

	@Test
	public void shouldReturnImmutableListOfColumnNames() {
		assertThat((List<String>) metaData.getColumnNames(),
				is(asList("COL1", "COL2")));
	}

	@Test
	public void shouldReturnImmutableListOfPrimaryKeyColumnNames() {
		assertThat((List<String>) metaData.getPrimaryKeys(), is(asList("COL2")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnColumnTypeByName() {
		assertThat((Class<String>) metaData.getColumnType("COL2"),
				equalTo(String.class));
	}

	@Test
	public void shouldReturnColumnIndex() {
		assertThat(metaData.getIndex("COL1"), is(0));
		assertThat(metaData.getIndex("COL2"), is(1));
	}

	public static void mockType(MetaData metaDataMock, String string,
			final Class<?> clazz) {
		when(metaDataMock.getColumnType(string)).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return clazz;
					}
				});

	}
}

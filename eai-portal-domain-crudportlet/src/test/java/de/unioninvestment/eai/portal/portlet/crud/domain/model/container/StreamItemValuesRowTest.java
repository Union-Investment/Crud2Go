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

package de.unioninvestment.eai.portal.portlet.crud.domain.model.container;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class StreamItemValuesRowTest {

	@Mock
	private StreamItem itemMock;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldProvideItemValuesAsHashMap() {
		when(itemMock.getValue("A")).thenReturn("AValue");
		when(itemMock.getValue("B")).thenReturn("BValue");
		StreamItemValuesRow row = new StreamItemValuesRow(asList("A", "B"), itemMock);

		Map<String, Object> values = row.getValues();
		assertThat(values.size(), is(2));
		assertThat(values.get("A"), is((Object)"AValue"));
		assertThat(values.get("B"), is((Object)"BValue"));
	}
}

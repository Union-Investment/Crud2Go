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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class AdvancedStringFilterTest {

	@Mock
	private Object itemIdMock;

	@Mock
	private Item itemMock;

	@Mock
	private Property<String> propertyMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(itemMock.getItemProperty("COL1")).thenReturn(propertyMock);
		when(propertyMock.getValue()).thenReturn("Hello Horst");
	}

	@Test
	public void shouldFilterStartWithValue() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "Hel",
				false, true, false);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(true));
	}

	@Test
	public void shouldExcludeNullValue() {
		when(propertyMock.getValue()).thenReturn(null);
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "Hel",
				false, true, false);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(false));
	}

	@Test
	public void shouldNotFilterStartWithValue() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "Horst",
				false, true, false);
		assertThat(filter.passesFilter(itemIdMock, itemMock), not(true));
	}

	@Test
	public void shouldFilterEndsWithValue() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "Horst",
				false, false, true);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(true));
	}

	@Test
	public void shouldNotFilterEndsWithValue() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "llo H",
				false, false, true);
		assertThat(filter.passesFilter(itemIdMock, itemMock), not(true));
	}

	@Test
	public void shouldFilterContains() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1", "llo H",
				false, false, false);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(true));
	}

	@Test
	public void shouldNotFilterContains() {
		AdvancedStringFilter filter = new AdvancedStringFilter("COL1",
				"llo Ha", false, false, false);
		assertThat(filter.passesFilter(itemIdMock, itemMock), not(true));
	}
}

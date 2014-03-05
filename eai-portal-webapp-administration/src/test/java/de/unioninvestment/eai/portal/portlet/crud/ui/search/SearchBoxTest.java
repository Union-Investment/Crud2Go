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

package de.unioninvestment.eai.portal.portlet.crud.ui.search;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;
import com.vaadin.ui.Notification.Type;

import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.QuerySearchHandler;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class SearchBoxTest extends SpringPortletContextTest {

	@Mock
	private QuerySearchHandler searchHandlerMock;
	private SearchBox searchBox;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		searchBox = new SearchBox(searchHandlerMock);
		when(searchHandlerMock.isValidQuery(Mockito.anyString())).thenReturn(true);
	}

	@Test
	public void shouldTriggerSearchOnValidNewValueChangeByAPI() {
		searchBox.setValue("myterm");
		verify(searchHandlerMock).search("myterm");
	}

	@Test
	public void shouldTriggerSearchOnValidNewValueChangeByWidget() {
		Map<String, Object> variables = ImmutableMap.<String, Object> builder() //
				.put("newitem", "myterm") //
				.put("selected", new String[] { "myterm" }) //
				.build();
		searchBox.changeVariables(null, variables);
		verify(searchHandlerMock).search("myterm");
	}

	@Test
	public void shouldTriggerSearchOnAddingSpaceToFilterString() {
		searchBox
				.changeVariables(null, ImmutableMap.<String, Object> of(
						"filter", "myterm", "page", 0));
		searchBox.changeVariables(null, ImmutableMap.<String, Object> of(
				"filter", "myterm ", "page", 0));
		verify(searchHandlerMock).search("myterm");
	}

	@Test
	public void shouldTriggerSearchOnAddingSpacePlusAnythingToFilterString() {
		searchBox
				.changeVariables(null, ImmutableMap.<String, Object> of(
						"filter", "myterm", "page", 0));
		searchBox.changeVariables(null, ImmutableMap.<String, Object> of(
				"filter", "myterm s", "page", 0));
		verify(searchHandlerMock).search("myterm");
	}

	@Test
	public void shouldNotTriggerSearchOnAddingAnyCharacterToFilterString() {
		searchBox.changeVariables(null,
				ImmutableMap.<String, Object> of("filter", "myter", "page", 0));
		searchBox
				.changeVariables(null, ImmutableMap.<String, Object> of(
						"filter", "myterm", "page", 0));
		verify(searchHandlerMock, never()).search(Mockito.anyString());
	}

	@Test
	public void shouldJustNotTriggerSearchIfAddingSpaceToFilterStringLeadsToInvalidQuery() {
		when(searchHandlerMock.isValidQuery("[4711")).thenReturn(false);
		searchBox.changeVariables(null,
				ImmutableMap.<String, Object> of("filter", "[4711", "page", 0));
		searchBox
				.changeVariables(null, ImmutableMap.<String, Object> of(
						"filter", "[4711 ", "page", 0));
		verify(searchHandlerMock, never()).search(Mockito.anyString());
		liferayContext.shouldNotShowNotification("Ungültige Abfrage: '[4711'",
				null, Type.ERROR_MESSAGE);
	}

	@Test
	public void shouldShowNotificationIfQueryIsInvalid() {
		when(searchHandlerMock.isValidQuery("*")).thenReturn(false);
		searchBox.setValue("*");
		liferayContext.shouldShowNotification("Ungültige Abfrage: '*'", null,
				Type.ERROR_MESSAGE);
	}
}

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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.ReadOnlyException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class PreferencePropertyTest {

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReadValueFromPrefrences() {
		PreferenceProperty property = new PreferenceProperty("prefKey");
		when(
				liferayContext.getPortletPreferencesMock().getValue("prefKey",
						null)).thenReturn("prefValue");

		assertThat(property.getValue(), is((Object) "prefValue"));
	}

	@Test
	public void shouldWriteValueIntoPreferences() throws ReadOnlyException {
		PreferenceProperty property = new PreferenceProperty("prefKey");

		property.setValue("prefValue2");

		verify(liferayContext.getPortletPreferencesMock()).setValue("prefKey",
				"prefValue2");
	}

	@Test
	public void shouldResetPreferenceOnNullValue() throws ReadOnlyException {
		PreferenceProperty property = new PreferenceProperty("prefKey");

		property.setValue(null);

		verify(liferayContext.getPortletPreferencesMock()).reset("prefKey");
	}

	@Test
	public void shouldResetPreferenceOnEmptyValue() throws ReadOnlyException {
		PreferenceProperty property = new PreferenceProperty("prefKey");

		property.setValue("");

		verify(liferayContext.getPortletPreferencesMock()).reset("prefKey");
	}

	@Test
	public void shouldReturnTypeString() {
		assertThat((Class<String>) new PreferenceProperty("bla").getType(),
				sameInstance(String.class));
	}
}

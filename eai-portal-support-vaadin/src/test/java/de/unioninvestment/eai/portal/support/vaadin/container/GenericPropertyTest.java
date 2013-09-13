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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property.ReadOnlyException;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty.RequiredException;

public class GenericPropertyTest {

	@Mock
	private GenericVaadinContainer container;

	@Mock
	private GenericItem item;

	private Column allFalseColumn;
	private Column allTrueColumn;
	private Column requiredColumn;
	private GenericProperty<Object> property;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		allFalseColumn = new Column("TEST", String.class, false, false, false,
				null);
		allTrueColumn = new Column("TEST", String.class, true, true, false,
				null);
		requiredColumn = new Column("TEST", String.class, false, true, false,
				null);

		property = new GenericProperty<Object>(allFalseColumn, null);
		property.setItem(item);
		when(item.getContainer()).thenReturn(container);
	}

	@Test
	public void shouldReturnRequiredByDelegatingToMetadata() {
		assertThat(new GenericProperty<Object>(allTrueColumn, null).isRequired(),
				is(true));
	}

	@Test
	public void shouldReturnNotRequiredByDelegatingToMetadata() {
		assertThat(property.isRequired(), is(false));
	}

	@Test
	public void shouldReturnReadOnlyByDelegatingToMetadata() {
		assertThat(new GenericProperty<Object>(allTrueColumn, null).isReadOnly(),
				is(true));
	}

	@Test
	public void shouldReturnNotReadOnlyByDelegatingToMetadata() {
		assertThat(property.isReadOnly(), is(false));
	}

	@Test
	public void shouldReturnNameByDelegatingToMetadata() {
		assertThat(property.getName(), is("TEST"));
	}

	@Test
	public void shouldReturnReadOnlyIfSetOnInstance() {
		property.setReadOnly(true);
		assertThat(property.isReadOnly(), is(true));
	}

	@Test
	public void shouldNotAllowSettingAPropertyToWritable() {
		GenericProperty<Object> property = new GenericProperty<Object>(allTrueColumn, null);
		property.setReadOnly(false);
		assertThat(property.isReadOnly(), is(true));
	}

	@Test
	public void shouldStoreValue() {
		property.setValue("abc");
		assertThat((String) property.getValue(), is("abc"));
	}

	@Test
	public void shouldBeModifiedAfterSettingTheValue() {
		property.setValue("abc");
		assertThat(property.isModified(), is(true));
	}

	@Test
	public void shouldResetModifiedStateAfterCommit() {
		property.setValue("abc");
		property.commit();
		assertThat(property.isModified(), is(false));
	}

	@Test
	public void shouldNotBeModifiedAfterSettingEqualValue() {
		property.setValue("abc");
		property.commit();

		property.setValue("abc");

		assertThat(property.isModified(), is(false));
	}

	@Test
	public void shouldNotBeModifiedAfterSettingToDifferentAndThenToFormerValue() {
		property.setValue("abc");
		property.setValue(null);

		assertThat(property.isModified(), is(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnTypeFromMetaData() {
		assertThat((Class<String>) property.getType(), equalTo(String.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = Exception.class)
	public void shouldCheckForTypeOnUpdate() {
		((GenericProperty)property).setValue(1);
	}

	@Test(expected = ReadOnlyException.class)
	public void shouldCheckForReadOnlyOnUpdate() {
		GenericProperty<Object> property = new GenericProperty<Object>(allTrueColumn, null);
		property.setValue("abc");
	}

	@Test(expected = RequiredException.class)
	public void shouldCheckForRequiredOnUpdate() {
		GenericProperty<Object> property = new GenericProperty<Object>(requiredColumn, null);
		property.setValue(null);
	}

	@Test
	public void shouldInformContainerOfItemChange() {
		property.setValue("abc");
		verify(container).itemChangeNotification(item);
	}

	@Test
	public void shouldNotBeModifiedAfterInitializationWithNotNullValue() {
		GenericProperty<Object> property = new GenericProperty<Object>(requiredColumn, "abc");
		assertThat(property.isModified(), is(false));
	}
}

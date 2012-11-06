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

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.validation.FormattingValidator;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

public class ValidationFieldFactoryWrapperTest extends SpringPortletContextTest {

	@Mock
	private CrudFieldFactory delegateMock;
	@Mock
	private Component uiContextMock;
	@Mock
	private Container containerMock;
	@Mock
	private FormattedSelect textFieldMock;
	@Mock
	private Item itemMock;

	private TableColumns tableColumns;
	private TableColumn testColumn;

	@Mock
	private DataContainer databaseContainer;

	@Mock
	private ContainerRow containerRowMock;

	@Mock
	private ContainerField containerFieldMock;
	@Mock
	private PropertyFormatter formatterMock;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(textFieldMock.getPropertyDataSource()).thenReturn(null);
		when(delegateMock.createField(containerMock, 1, "test", uiContextMock))
				.thenReturn(textFieldMock);
		when(delegateMock.createField(itemMock, "test", uiContextMock))
				.thenReturn(textFieldMock);

		when(databaseContainer.convertItemToRow(itemMock, false, true))
				.thenReturn(containerRowMock);
		when(containerRowMock.getFields()).thenReturn(
				singletonMap("test", containerFieldMock));
	}

	private void createTestColumn(String columnName,
			List<FieldValidator> validators) {
		testColumn = new TableColumn.Builder().name(columnName)
				.hiddenStatus(Hidden.FALSE).editableDefault(true)
				.validators(validators).build();
		tableColumns = new TableColumns(Arrays.asList(testColumn));
	}

	@Test
	public void shouldDelegateRequestsToBackingFactory() {
		createTestColumn("test", null);

		containerReturnsPropertyWithFieldNullable(true);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(delegateMock).createField(containerMock, 1, "test",
				uiContextMock);
		assertEquals(textFieldMock, field);
	}

	@Test
	public void shouldDelegateFormFieldRequestsToBackingFactory() {
		createTestColumn("test", null);

		containerReturnsPropertyWithFieldNullable(true);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		Field field = wrapper.createField(itemMock, "test", uiContextMock);

		verify(delegateMock).createField(itemMock, "test", uiContextMock);
		assertEquals(textFieldMock, field);
	}

	@Test
	public void shouldAddValidatorsFromTableColumns() {
		final Validator vaadinValidator = new NullValidator("Fehler", false);

		createTestColumn("test",
				Arrays.asList((FieldValidator) new FieldValidator() {
					@Override
					public void apply(Field field) {
						field.addValidator(vaadinValidator);
					}
				}));

		containerReturnsPropertyWithFieldNullable(true);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(field).addValidator(vaadinValidator);
	}

	@Test
	public void shouldAddValidatorsFromContainer() {
		createTestColumn("test", null);

		containerReturnsPropertyWithFieldNullable(false);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(field).setRequired(true);
		verify(field).setRequiredError(
				"Es wird eine Eingabe im Feld test benötigt");
		verify((AbstractSelect) field).setNullSelectionAllowed(false);
	}

	@Test
	public void shouldAddFormatterValidatorFromContainer() {
		createTestColumn("test", null);

		containerReturnsPropertyWithFieldNullable(true);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		when(textFieldMock.getPropertyDataSource()).thenReturn(formatterMock);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(field).addValidator(any(FormattingValidator.class));
	}

	private void containerReturnsPropertyWithFieldNullable(boolean nullable) {
		ColumnProperty property = new ColumnProperty("test", true, false,
				nullable, "1", String.class);
		when(containerMock.getItem(1)).thenReturn(itemMock);
		when(itemMock.getItemProperty("test")).thenReturn(property);
		when(containerFieldMock.isRequired()).thenReturn(!nullable);
	}

	@Test
	public void shouldReturnNullWhenNoFieldIsGivenBackByDelegate() {
		// final Validator vaadinValidator = new NullValidator("Fehler", false);

		createTestColumn("test2",
				Arrays.asList((FieldValidator) new FieldValidator() {
					@Override
					public void apply(Field field) {
						fail("Should not be called");
					}
				}));

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, tableColumns);

		Field field = wrapper.createField(containerMock, 1, "test2",
				uiContextMock);

		assertNull(field);
	}

	@Test
	public void shouldGracefullyHandleNonExistingColumnsDefinition() {
		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, null);

		containerReturnsPropertyWithFieldNullable(true);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(field).isRequired();

	}

	@Test
	public void shouldGracefullyHandleNonExistingColumnDefinition() {
		createTestColumn("test2", null);

		containerReturnsPropertyWithFieldNullable(true);

		ValidationFieldFactoryWrapper wrapper = new ValidationFieldFactoryWrapper(
				databaseContainer, delegateMock, null);

		Field field = wrapper.createField(containerMock, 1, "test",
				uiContextMock);

		verify(field).isRequired();

	}
}

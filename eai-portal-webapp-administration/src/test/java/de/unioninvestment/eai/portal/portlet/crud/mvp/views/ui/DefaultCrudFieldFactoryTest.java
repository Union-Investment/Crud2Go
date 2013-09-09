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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.portlet.crud.datatypes.NumberDataType;
import de.unioninvestment.eai.portal.portlet.crud.datatypes.StringDataType;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.AbstractDatabaseContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

public class DefaultCrudFieldFactoryTest extends SpringPortletContextTest {

	private DefaultCrudFieldFactory crudTableFieldFactory;

	@Mock
	private Table vaadinTableMock;

	@Mock
	private de.unioninvestment.eai.portal.portlet.crud.domain.model.Table modelTableMock;

	@Mock
	private TableColumns tableColumnsMock;

	@Mock
	private TableColumn tableColumnMock;

	@Mock
	private Container containerMock;

	@Mock
	private Component componentMock;

	@Mock
	private Item itemMock;

	@Mock
	private Property propertyMock;

	@Mock
	private DisplaySupport displaySupportMock;

	@Mock
	private CheckBoxTableColumn modelCheckBoxMock;

	@Mock
	private TextField fieldMock;

	@Mock
	private TextArea textAreaMock;

	@Mock
	private AbstractDatabaseContainer databaseContainerMock;

	@Mock
	private OptionList selectionMock;

	@Mock
	private ContainerRow rowMock;

	@Mock
	private ContainerField containerFieldMock;

	// @Mock
	// private TableColumn testColumnMock;

	@Mock
	private Map<String, ContainerField> fieldsMapMock;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(containerMock.getItem(any())).thenReturn(itemMock);

		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList("1"));
		when(vaadinTableMock.getValue()).thenReturn(set);
		when(vaadinTableMock.getContainerDataSource())
				.thenReturn(containerMock);

		when(itemMock.getItemProperty(any())).thenReturn(propertyMock);
		when(displaySupportMock.supportsDisplaying(String.class)).thenReturn(
				true);
		when(modelTableMock.getContainer()).thenReturn(databaseContainerMock);

		crudTableFieldFactory = new DefaultCrudFieldFactory(vaadinTableMock,
				modelTableMock);

		when(databaseContainerMock.convertItemToRow(itemMock, false, true))
				.thenReturn(rowMock);
		when(databaseContainerMock.findDisplayer(anyString())).thenReturn(
				displaySupportMock);

		when(rowMock.getFields()).thenReturn(
				singletonMap("test", containerFieldMock));
		when(containerFieldMock.getText()).thenReturn("formatted text");
	}

	private void containerShouldReturnDataType(final Class<?> returningClass) {
		when(containerMock.getType(any())).thenAnswer(new Answer<Class<?>>() {
			@Override
			public Class<?> answer(InvocationOnMock invocation)
					throws Throwable {
				return returningClass;
			}
		});
	}

	@Test
	public void shouldReturnReadonlyFieldByContainerInfoIfNoTableColumns() {
		ColumnProperty property = new ColumnProperty("test", true, false, true,
				"1", String.class);
		when(containerFieldMock.isReadonly()).thenReturn(true);

		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);

		doReturn(fieldMock).when(displaySupportMock).createField(String.class,
				"test", false, null, null);

		TextField result = (TextField) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);

		assertThat(result, is(fieldMock));
		verify(result).setReadOnly(true);
	}

	@Test
	public void shouldCreateDropdown() {
		containerShouldReturnDataType(String.class);

		DisplaySupport displayer = new StringDataType();
		when(databaseContainerMock.findDisplayer("test")).thenReturn(displayer);

		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList("foo"));
		when(vaadinTableMock.getValue()).thenReturn(set);

		when(propertyMock.getType()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return String.class;
			}
		});
		when(propertyMock.getValue()).thenReturn("foo");
		when(propertyMock.isReadOnly()).thenReturn(false);

		when(containerMock.getItem(any())).thenReturn(itemMock);
		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		when(tableColumnsMock.isComboBox("test")).thenReturn(true);
		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		when(tableColumnMock.isEditable(rowMock)).thenReturn(true);
		Map<String, String> options = new HashMap<String, String>();
		options.put("1", "value1");
		options.put("2", "value2");
		when(selectionMock.getOptions(any(SelectionContext.class))).thenReturn(
				options);
		when(selectionMock.getTitle(eq("1"), any(SelectionContext.class)))
				.thenReturn("value1");
		when(selectionMock.getTitle(eq("2"), any(SelectionContext.class)))
				.thenReturn("value2");
		when(tableColumnsMock.getDropdownSelections(anyString())).thenReturn(
				selectionMock);

		Field result = crudTableFieldFactory.createField(containerMock, "foo",
				"test", componentMock);

		assertThat(result, new InstanceOf(AbstractSelect.class));

		AbstractSelect select = (AbstractSelect) result;
		assertThat(select.getItemIds().size(), is(2));
		assertThat(select.getItemCaption("1"), is("value1"));
		assertThat(select.getItemCaption("2"), is("value2"));
	}

	@Test
	public void shouldCreateMultilineField() {
		ColumnProperty property = new ColumnProperty("test", true, false, true,
				"1", String.class);
		when(containerFieldMock.isReadonly()).thenReturn(true);

		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);

		doReturn(textAreaMock).when(displaySupportMock).createField(
				String.class, "test", true, null, null);

		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		when(tableColumnsMock.isMultiline("test")).thenReturn(true);

		TextArea result = (TextArea) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);

		assertThat(result, is(textAreaMock));
	}

	@Test
	public void shouldCreateStringCheckBoxField() {
		when(containerFieldMock.isReadonly()).thenReturn(false);
		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		when(tableColumnMock.isEditable(rowMock)).thenReturn(true);

		prepareCheckBoxTest(String.class, "1", false);

		Field result = crudTableFieldFactory.createField(containerMock, "1",
				"test", componentMock);

		assertThat(result, new InstanceOf(com.vaadin.ui.CheckBox.class));
	}

	@Test
	public void shouldNotCreateDisabledStringCheckBoxField() {
		// when(containerFieldMock.isReadonly()).thenReturn(false);
		// when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		// when(tableColumnMock.isEditable(rowMock)).thenReturn(true);

		prepareCheckBoxTest(String.class, "1", true);

		Field result = crudTableFieldFactory.createField(containerMock, "1",
				"test", componentMock);

		// assertThat(result, instanceOf(com.vaadin.ui.CheckBox.class));
		// assertThat(result.isEnabled(), is(false));
		assertThat(result, is(nullValue()));
	}

	@Test
	public void shouldCreateNumberCheckBoxField() {
		when(containerFieldMock.isReadonly()).thenReturn(false);
		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		when(tableColumnMock.isEditable(rowMock)).thenReturn(true);

		prepareCheckBoxTest(Number.class, 1, false);

		Field result = crudTableFieldFactory.createField(containerMock, 1,
				"test", componentMock);

		assertThat(result, instanceOf(com.vaadin.ui.CheckBox.class));
	}

	@Test
	public void shouldCreateDisabledNumberCheckBoxField() {
		when(containerFieldMock.isReadonly()).thenReturn(false);
		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		when(tableColumnMock.isEditable(rowMock)).thenReturn(true);

		prepareCheckBoxTest(Number.class, 1, true);

		Field result = crudTableFieldFactory.createField(containerMock, 1,
				"test", componentMock);

		// assertThat(result, instanceOf(com.vaadin.ui.CheckBox.class));
		// assertThat(result.isEnabled(), is(false));
		assertThat(result, is(nullValue()));
	}

	private void prepareCheckBoxTest(Class<?> type, Object value,
			boolean readonly) {
		containerShouldReturnDataType(type);

		when(containerFieldMock.isReadonly()).thenReturn(readonly);
		ColumnProperty property = new ColumnProperty("test", readonly, false,
				true, value, Number.class);

		// set displayer support and value
		DisplaySupport displayer = null;
		if (type.equals(Number.class)) {
			displayer = new NumberDataType();

			Set<Number> set = new HashSet<Number>();
			set.addAll(Arrays.asList((Number) value));
			when(vaadinTableMock.getValue()).thenReturn(set);
		} else if (type.equals(String.class)) {
			displayer = new StringDataType();

			Set<String> set = new HashSet<String>();
			set.addAll(Arrays.asList((String) value));
			when(vaadinTableMock.getValue()).thenReturn(set);
		}
		// crudTableFieldFactory.setDisplays(displayers);
		when(databaseContainerMock.findDisplayer("test")).thenReturn(displayer);

		when(itemMock.getItemProperty(any())).thenReturn(property);
		when(containerMock.getItem(any())).thenReturn(itemMock);

		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.isComboBox("test")).thenReturn(false);
		when(tableColumnsMock.isCheckbox("test")).thenReturn(true);
		when(tableColumnsMock.getCheckBox("test"))
				.thenReturn(modelCheckBoxMock);

	}

	@Test
	public void shouldIgnoreNullItemId() {
		assertNull(crudTableFieldFactory.createField(containerMock, null,
				"test", componentMock));
	}

	@Test
	public void shouldNotCreateFieldWithoutDisplaySupport() {
		ColumnProperty property = new ColumnProperty("test", false, false,
				true, "1", String.class);
		containerShouldReturnDataType(List.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);
		when(databaseContainerMock.findDisplayer(anyString())).thenReturn(null);

		Field result = crudTableFieldFactory.createField(containerMock, "1",
				"test", componentMock);
		assertNull(result);
	}

	@Test
	public void shouldCreateInputFieldWithCommonSettingsWhenDisplaySupportIsAvailable() {
		ColumnProperty property = new ColumnProperty("test", false, false,
				true, "1", String.class);
		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);
		when(tableColumnMock.isEditable(rowMock)).thenReturn(true);

		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		when(tableColumnsMock.isMultiline("test")).thenReturn(true);
		when(tableColumnsMock.getInputPrompt("test")).thenReturn("testPrompt");
		doReturn(fieldMock).when(displaySupportMock).createField(String.class,
				"test", true, "testPrompt", null);

		TextField result = (TextField) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);

		assertThat(result, is(fieldMock));
		verify(fieldMock).setCaption("Test");
		verify(fieldMock).setWidth(100, Sizeable.UNITS_PERCENTAGE);
		verify(fieldMock).setBuffered(true); // enable buffering
	}

	@Test
	public void shouldUseFieldNameAsCaptionWhenUnconfigured() {
		ColumnProperty property = new ColumnProperty("test", true, false, true,
				"1", String.class);
		when(containerFieldMock.isReadonly()).thenReturn(true);

		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);

		doReturn(fieldMock).when(displaySupportMock).createField(String.class,
				"test", false, null, null);

		TextField result = (TextField) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);
		verify(result).setCaption("Test");
	}

	@Test
	public void shouldUseConfiguredTitleAsCaption() {
		ColumnProperty property = new ColumnProperty("test", true, false, true,
				"1", String.class);
		when(containerFieldMock.isReadonly()).thenReturn(true);

		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);

		doReturn(fieldMock).when(displaySupportMock).createField(String.class,
				"test", false, null, null);

		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		when(tableColumnMock.getTitle()).thenReturn("myTitle");

		TextField result = (TextField) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);
		verify(result).setCaption("myTitle");
	}

	@Test
	public void shouldUseLongTitleAsDescriptionTooltip() {
		ColumnProperty property = new ColumnProperty("test", true, false, true,
				"1", String.class);
		when(containerFieldMock.isReadonly()).thenReturn(true);

		containerShouldReturnDataType(String.class);

		when(itemMock.getItemProperty(any())).thenReturn(property);

		when(modelTableMock.isRowEditable(rowMock)).thenReturn(true);

		doReturn(fieldMock).when(displaySupportMock).createField(String.class,
				"test", false, null, null);

		when(modelTableMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.get("test")).thenReturn(tableColumnMock);
		when(tableColumnMock.getLongTitle()).thenReturn("myLongTitle");

		TextField result = (TextField) crudTableFieldFactory.createField(
				containerMock, "1", "test", componentMock);
		verify(result).setDescription("myLongTitle");
	}

}

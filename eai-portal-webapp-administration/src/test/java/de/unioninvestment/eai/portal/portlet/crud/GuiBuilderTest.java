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
package de.unioninvestment.eai.portal.portlet.crud;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.CustomComponentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DialogPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelContentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.RowEditingFormPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TablePresenter;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;

public class GuiBuilderTest extends SpringPortletContextTest {

	@Mock
	private PortletPresenter portletPresenterMock;

	@Mock
	private PanelContentPresenter panelContentPresenterMock;

	@Mock
	private TablePresenter tablePresenterMock;

	@Mock
	private PresenterFactory presenterFactoryMock;

	@Mock
	private Portlet portletMock;

	@Mock
	private Table tableMock;

	@Mock
	private Page pageMock;

	@Mock
	private Form formMock;

	@Mock
	private CustomComponent customComponentMock;

	@Mock
	private PanelPresenter panelPresenterMock;

	@Mock
	private RowEditingFormPresenter rowEditingFormPresenterMock;

	@Mock
	private CustomComponentPresenter customComponentPresenterMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(portletMock.getPage()).thenReturn(pageMock);

		when(presenterFactoryMock.portletPresenter(portletMock)).thenReturn(
				portletPresenterMock);
		@SuppressWarnings("unchecked")
		Map<String, DialogPresenter> dialogPresenterMapMatcher = (Map<String, DialogPresenter>) anyObject();
		when(
				presenterFactoryMock.panelPresenter((Panel) anyObject(),
						dialogPresenterMapMatcher)).thenReturn(
				panelPresenterMock);
		when(presenterFactoryMock.panelContentPresenter(pageMock)).thenReturn(
				panelContentPresenterMock);
		when(presenterFactoryMock.tablePresenter(tableMock)).thenReturn(
				tablePresenterMock);
	}

	@Test
	public void shouldCreateTablePresenter() throws Exception {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { tableMock }));

		GuiBuilder builder = new GuiBuilder(presenterFactoryMock);

		PortletPresenter portletPresenter = builder.build(portletMock);

		verify(presenterFactoryMock).panelContentPresenter(pageMock);
		verify(presenterFactoryMock).tablePresenter(tableMock);

		assertEquals(portletPresenterMock, portletPresenter);
	}

	@Test
	public void shouldCreateCustomComponentPresenter() throws Exception {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { customComponentMock }));

		GuiBuilder builder = new GuiBuilder(presenterFactoryMock);
		when(presenterFactoryMock.customComponentPresenter(customComponentMock))
				.thenReturn(customComponentPresenterMock);

		builder.build(portletMock);

		verify(panelContentPresenterMock).addComponent(
				customComponentPresenterMock);
	}

	@Test
	public void shouldCreateFormPresenter() throws Exception {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { formMock, tableMock }));

		GuiBuilder builder = new GuiBuilder(presenterFactoryMock);
		PortletPresenter portletPresenter = builder.build(portletMock);

		verify(presenterFactoryMock).panelContentPresenter(pageMock);
		verify(presenterFactoryMock).formPresenter(formMock);
		verify(presenterFactoryMock).tablePresenter(tableMock);

		assertEquals(portletPresenterMock, portletPresenter);
	}

	@Test
	public void shouldBuildDialogPresenter() throws Exception {
		Dialog dialogMock = Mockito.mock(Dialog.class);

		Map<String, Dialog> dialogs = new HashMap<String, Dialog>();
		dialogs.put("id", dialogMock);

		when(portletMock.getDialogsById()).thenReturn(dialogs);

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(presenterFactoryMock).dialogPresenter(dialogMock);
	}

	@Test
	public void shouldCreateRegionPresenter() throws Exception {
		Region regionMock = Mockito.mock(Region.class);

		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { regionMock }));

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(presenterFactoryMock).regionPresenter(regionMock);
	}

	@Test
	public void shouldBuildEditingFormPresenter() {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { tableMock }));
		when(tableMock.isFormEditEnabled()).thenReturn(true);

		when(
				presenterFactoryMock.rowEditingFormPresenter(any(Dialog.class),
						any(Panel.class), any(String.class), any(Table.class),
						any(TablePresenter.class))).thenReturn(
				rowEditingFormPresenterMock);

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(presenterFactoryMock).rowEditingFormPresenter(any(Dialog.class),
				any(Panel.class), any(String.class), any(Table.class),
				any(TablePresenter.class));
	}

	@Test
	public void shouldSetReferenceToEditingFormPresenter() {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { tableMock }));
		when(tableMock.isFormEditEnabled()).thenReturn(true);

		when(
				presenterFactoryMock.rowEditingFormPresenter(any(Dialog.class),
						any(Panel.class), any(String.class), any(Table.class),
						any(TablePresenter.class))).thenReturn(
				rowEditingFormPresenterMock);

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(tablePresenterMock).setRowEditingFormPresenter(
				rowEditingFormPresenterMock);
	}
}

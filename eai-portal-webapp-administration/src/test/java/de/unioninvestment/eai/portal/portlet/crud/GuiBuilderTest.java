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

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
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
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CompoundSearch;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TextArea;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.ComponentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.CompoundSearchPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.CustomComponentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DialogPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.FormPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelContentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.RegionPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.RowEditingFormPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TablePresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TabsPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TextAreaPresenter;
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

	@Mock
	private CompoundSearch compoundSearchMock;

	@Mock
	private CompoundSearchPresenter compoundSearchPresenterMock;

	@Mock
	private TextArea textAreaMock;

	@Mock
	private TextAreaPresenter textAreaPresenterMock;

	@Mock
	private Region regionMock;

	@Mock
	private RegionPresenter regionPresenterMock;

	@Mock
	private FormPresenter formPresenterMock;

	@Mock
	private Tabs tabsMock;

	@Mock
	private TabsPresenter tabsPresenterMock;

	@Mock
	private Tab tabMock;

	@Mock
	private PanelContentPresenter tabContentPresenterMock;

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
	}

	@Test
	public void shouldAddTablePresenter() throws Exception {
		when(presenterFactoryMock.tablePresenter(tableMock)).thenReturn(
				tablePresenterMock);
		shouldAddPresenterFromFactoryToPanel(tableMock, tablePresenterMock);
	}

	@Test
	public void shouldAddTabsPresenter() throws Exception {
		when(presenterFactoryMock.tabsPresenter(tabsMock)).thenReturn(
				tabsPresenterMock);
		shouldAddPresenterFromFactoryToPanel(tabsMock, tabsPresenterMock);
	}

	@Test
	public void shouldAddComponentPresenterToTabs() throws Exception {
		when(presenterFactoryMock.tabsPresenter(tabsMock)).thenReturn(
				tabsPresenterMock);
		when(presenterFactoryMock.tablePresenter(tableMock)).thenReturn(
				tablePresenterMock);
		when(presenterFactoryMock.panelContentPresenter(tabMock)).thenReturn(
				tabContentPresenterMock);

		when(portletMock.getPage()).thenReturn(null);
		when(portletMock.getTabs()).thenReturn(tabsMock);
		when(tabsMock.getElements()).thenReturn(asList(tabMock));
		when(tabMock.getElements()).thenReturn(asList((Component) tableMock));

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(tabsPresenterMock).addPanel(panelPresenterMock);
		verify(panelPresenterMock).setDefaultPresenter(tabContentPresenterMock);
	}

	@Test
	public void shouldAddCustomComponentPresenter() throws Exception {
		when(presenterFactoryMock.customComponentPresenter(customComponentMock))
				.thenReturn(customComponentPresenterMock);
		shouldAddPresenterFromFactoryToPanel(customComponentMock,
				customComponentPresenterMock);
	}

	@Test
	public void shouldAddFormPresenter() throws Exception {
		when(presenterFactoryMock.formPresenter(formMock)).thenReturn(
				formPresenterMock);
		shouldAddPresenterFromFactoryToPanel(formMock, formPresenterMock);
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
	public void shouldAddRegionPresenter() throws Exception {
		when(presenterFactoryMock.regionPresenter(regionMock)).thenReturn(
				regionPresenterMock);
		shouldAddPresenterFromFactoryToPanel(regionMock, regionPresenterMock);
	}

	@Test
	public void shouldAddCompoundSearchPresenter() throws Exception {
		when(presenterFactoryMock.compoundSearchPresenter(compoundSearchMock))
				.thenReturn(compoundSearchPresenterMock);
		shouldAddPresenterFromFactoryToPanel(compoundSearchMock,
				compoundSearchPresenterMock);
	}

	@Test
	public void shouldAddTextAreaPresenter() throws Exception {
		when(presenterFactoryMock.textAreaPresenter(textAreaMock)).thenReturn(
				textAreaPresenterMock);

		shouldAddPresenterFromFactoryToPanel(textAreaMock,
				textAreaPresenterMock);
	}

	@Test
	public void shouldAddEditingFormPresenter() {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { tableMock }));
		when(tableMock.isFormEditEnabled()).thenReturn(true);

		when(
				presenterFactoryMock.rowEditingFormPresenter(any(Dialog.class),
						any(Panel.class), any(String.class), any(Table.class)))
				.thenReturn(rowEditingFormPresenterMock);

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(presenterFactoryMock).rowEditingFormPresenter(any(Dialog.class),
				any(Panel.class), any(String.class), any(Table.class));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldFailOnUnknownComponent() throws Exception {
		when(pageMock.getElements()).thenReturn(
				Arrays.asList(new Component[] { mock(Component.class) }));

		new GuiBuilder(presenterFactoryMock).build(portletMock);
	}

	private void shouldAddPresenterFromFactoryToPanel(Component modelMock,
			ComponentPresenter presenterMock) {
		when(pageMock.getElements()).thenReturn(asList((Component) modelMock));

		new GuiBuilder(presenterFactoryMock).build(portletMock);

		verify(panelContentPresenterMock).addComponent(presenterMock);
	}

}

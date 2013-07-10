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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CollapsibleRegionView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DefaultRowEditingFormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.FormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.RegionView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ViewFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class PresenterFactoryTest {

	private PresenterFactory presenterFactory = new PresenterFactory();

	@Mock
	private ViewFactory viewFactoryMock;

	@Mock
	private ConfigurationService configurationServiceMock;

	@Mock
	private EventBus eventBusMock;

	@Mock
	private FormFields formFieldsMock;

	@Mock
	private PanelView panelViewMock;

	private PanelContentView panelContentViewMock;

	@Mock
	private Panel panelMock;

	@Mock
	private Dialog dialogMock;

	@Mock
	private Table tableMock;

	@Mock
	private TablePresenter tablePresenterMock;

	@Mock
	private DefaultRowEditingFormView rowEditingFormViewMock;

	@Mock
	private Button backButtonMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		panelContentViewMock = (PanelContentView) mock(VerticalLayout.class,
				Mockito.withSettings().extraInterfaces(PanelContentView.class));

		presenterFactory.setViewFactory(viewFactoryMock);
		presenterFactory.setEventBus(eventBusMock);
		presenterFactory.setConfigurationService(configurationServiceMock);
	}

	@Test
	public void shouldInstancePortletPresenter() {
		Portlet portletMock = mock(Portlet.class);
		PortletView portletViewMock = mock(PortletView.class);
		when(viewFactoryMock.portletView()).thenReturn(portletViewMock);

		PortletPresenter portletPresenter = presenterFactory
				.portletPresenter(portletMock);

		assertThat(portletPresenter.getView(), is((View) portletViewMock));
	}

	@Test
	public void shouldInstancePagePresenter() {
		Panel pageMock = mock(Page.class);
		PanelContentView pageViewMock = mock(PanelContentView.class);
		when(
				viewFactoryMock.panelContentView(anyBoolean(), anyBoolean(),
						anyString(), anyString())).thenReturn(pageViewMock);

		PanelContentPresenter pagePresenter = presenterFactory
				.panelContentPresenter(pageMock);

		assertThat(pagePresenter.getView(), is((View) pageViewMock));
	}

	@Test
	public void shouldRequestPanelViewWithMarginForTab() {
		Panel tabMock = mock(Tab.class);
		PanelContentView tabViewMock = mock(PanelContentView.class);
		when(
				viewFactoryMock.panelContentView(anyBoolean(), eq(false),
						anyString(), anyString())).thenReturn(tabViewMock);

		PanelContentPresenter pagePresenter = presenterFactory
				.panelContentPresenter(tabMock);

		assertThat(pagePresenter.getView(), is((View) tabViewMock));
	}

	@Test
	public void shouldInstanceFormPresenter() {
		Form formMock = mock(Form.class);
		FormView formViewMock = mock(FormView.class);
		when(viewFactoryMock.formView()).thenReturn(formViewMock);
		when(formMock.getFields()).thenReturn(formFieldsMock);

		FormPresenter formPresenter = presenterFactory.formPresenter(formMock);

		assertThat(formPresenter.getView(), is((View) formViewMock));
	}

	@Test
	public void shouldCreatePanelPresenterWithGivenView() throws Exception {
		// given
		Map<String, DialogPresenter> dialogPresenterMap = new HashMap<String, DialogPresenter>();
		when(viewFactoryMock.panelView()).thenReturn(panelViewMock);

		// when
		PanelPresenter panelPresenter = presenterFactory.panelPresenter(
				panelMock, dialogPresenterMap);

		// then
		assertSame(panelViewMock, panelPresenter.getView());
	}

	@Test
	public void shouldCreateDialogPresenterWithGivenView() throws Exception {
		// given
		when(
				viewFactoryMock.panelContentView(anyBoolean(), anyBoolean(),
						anyString(), anyString())).thenReturn(
				panelContentViewMock);

		// when
		DialogPresenter dialogPresenter = presenterFactory
				.dialogPresenter(dialogMock);

		// then
		assertSame(panelContentViewMock, dialogPresenter.getView());
	}

	@Test
	public void shouldCreatePanelContentPresenterWithGivenView()
			throws Exception {
		// given
		when(
				viewFactoryMock.panelContentView(anyBoolean(), anyBoolean(),
						anyString(), anyString())).thenReturn(
				panelContentViewMock);

		// when
		PanelContentPresenter panelContentPresenter = presenterFactory
				.panelContentPresenter(panelMock);

		// then
		assertSame(panelContentViewMock, panelContentPresenter.getView());
	}

	@Test
	public void shouldCreateRegionPresenter() throws Exception {
		Region regionMock = Mockito.mock(Region.class);
		RegionView regionViewMock = Mockito.mock(RegionView.class);

		when(regionMock.isCollapsible()).thenReturn(false);
		when(
				viewFactoryMock.regionView(anyBoolean(), anyBoolean(),
						anyString(), anyString())).thenReturn(regionViewMock);

		RegionPresenter presenter = presenterFactory
				.regionPresenter(regionMock);

		assertTrue(presenter instanceof RegionPresenter);
	}

	@Test
	public void shouldCreateCollapsibleRegionPresenter() throws Exception {
		Region regionMock = Mockito.mock(Region.class);
		CollapsibleRegionView regionViewMock = Mockito
				.mock(CollapsibleRegionView.class);

		when(regionMock.isCollapsible()).thenReturn(true);
		when(viewFactoryMock.collapsibleRegionView(anyString(), anyString()))
				.thenReturn(regionViewMock);

		RegionPresenter presenter = presenterFactory
				.regionPresenter(regionMock);

		assertTrue(presenter instanceof CollapsibleRegionPresenter);
	}

	@Test
	public void shouldInitializeViewWithPresenter() {
		// given
		when(
				viewFactoryMock.rowEditingFormView(anyBoolean(), anyBoolean(),
						anyString(), anyString())).thenReturn(
				rowEditingFormViewMock);
		when(
				rowEditingFormViewMock.addBackButton(anyString(),
						any(ClickListener.class))).thenReturn(backButtonMock);

		// when
		RowEditingFormPresenter presenter = presenterFactory
				.rowEditingFormPresenter(dialogMock, panelMock, "id1",
						tableMock, tablePresenterMock);

		// then
		verify(rowEditingFormViewMock).initialize(presenter);
	}
}

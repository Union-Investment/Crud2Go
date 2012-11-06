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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TabsView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class PortletPresenterTest {
	@Mock
	private PanelContentView pageViewMock;

	@Mock
	private TabsPresenter tabsPresenterMock;

	@Mock
	private PortletView portletViewMock;
	@Mock
	private PanelPresenter pageMock;
	@Mock
	private Portlet portletMock;

	private PortletPresenter portletPresenter;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		portletPresenter = new PortletPresenter(portletViewMock, portletMock);
	}

	@Test
	public void shouldAddPageToView() {

		when(pageMock.getView()).thenReturn(pageViewMock);

		portletPresenter.setPage(pageMock);

		verify(portletViewMock).setContent(pageViewMock);
		assertEquals(pageMock, portletPresenter.getMainPage());
	}

	@Test
	public void shouldSetContentToView() {
		View tabsViewMock = mock(TabsView.class);
		when(tabsPresenterMock.getView()).thenReturn(tabsViewMock);
		portletPresenter.setTabs(tabsPresenterMock);

		verify((PortletView) portletPresenter.getView()).setContent(
				tabsViewMock);
	}
}

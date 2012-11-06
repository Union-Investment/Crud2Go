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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.FormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView;

public class PanelContentPresenterTest {

	@Mock
	private PanelContentView pageViewMock;

	@Mock
	private TablePresenter tableMock;

	@Mock
	private TableView tableViewMock;

	@Mock
	private FormPresenter formPresenterMock;

	@Mock
	private FormView formViewMock;

	@Mock
	private Panel panelMock;

	@Mock
	private Tab tabMock;

	private PanelContentPresenter pagePresenter;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		pagePresenter = new PanelContentPresenter(pageViewMock, panelMock);
	}

	@Test
	public void shouldStoreParameters() {
		assertEquals(pageViewMock, pagePresenter.getView());
	}

	@Test
	public void shouldAddFormToView() {
		when(formPresenterMock.getView()).thenReturn(formViewMock);

		pagePresenter.addComponent(formPresenterMock);

		assertEquals(formPresenterMock, pagePresenter.getComponents().get(0));
		verify(pageViewMock).addComponent(formViewMock);
	}

	@Test
	public void shouldAddTableToView() {
		when(tableMock.getView()).thenReturn(tableViewMock);

		pagePresenter.addComponent(tableMock);
		assertEquals(pageViewMock, pagePresenter.getView());
		assertEquals(tableMock, pagePresenter.getComponents().get(0));
		verify(pageViewMock).addComponent(tableViewMock);
	}

	@Test
	public void shouldReturnNullAsTitle() {
		assertNull(pagePresenter.getTitle());
	}

	@Test
	public void shouldReturnTitleIfTab() {
		String title = "test";
		when(tabMock.getTitle()).thenReturn(title);
		
		pagePresenter = new PanelContentPresenter(pageViewMock, tabMock);
		assertEquals(title, pagePresenter.getTitle());
	}
}

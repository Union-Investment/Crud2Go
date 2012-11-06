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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponentGenerator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CustomComponentView;

public class CustomComponentPresenterTest {

	@Mock
	private CustomComponentView viewMock;

	private CustomComponentPresenter presenter;

	@Mock
	private Tab tabMock;

	@Mock
	private Component componentMock;

	@Mock
	private CustomComponentGenerator generatorMock;

	@Mock
	private CustomComponent modelMock;

	@Mock
	private Page pageMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(modelMock.getPanel()).thenReturn(tabMock);
		when(modelMock.getGenerator()).thenReturn(generatorMock);
		presenter = new CustomComponentPresenter(viewMock, modelMock);
	}

	@Test
	public void shouldReturnView() {
		assertThat(presenter.getView(), is(viewMock));
	}

	@Test
	public void shouldDisplayNothingOnInitialization() {
		verifyZeroInteractions(viewMock);
	}

	@Test
	public void shouldInitializeViewEagerly() {
		when(modelMock.getPanel()).thenReturn(pageMock);
		when(generatorMock.generate()).thenReturn(componentMock);

		presenter = new CustomComponentPresenter(viewMock, modelMock);

		verify(viewMock).setComponent(componentMock);
	}

	@Test
	public void shouldInitializeViewOnFirstShowEvent() {
		when(generatorMock.generate()).thenReturn(componentMock);

		presenter.onShow(new ShowEvent<Tab>(tabMock));

		verify(viewMock).setComponent(componentMock);
	}

	@Test
	public void shouldDoNothingOnSubsequentShowEvents() {
		when(generatorMock.generate()).thenReturn(componentMock);

		presenter.onShow(new ShowEvent<Tab>(tabMock));
		presenter.onShow(new ShowEvent<Tab>(tabMock));

		verify(viewMock).setComponent(componentMock);
		verifyNoMoreInteractions(viewMock);
	}
}

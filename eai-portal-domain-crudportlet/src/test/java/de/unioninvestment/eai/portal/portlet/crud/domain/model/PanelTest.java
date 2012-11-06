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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel.Presenter;

public class PanelTest {

	private Panel panel;

	@Mock
	private Presenter presenterMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		panel = new Panel();
	}

	@Test
	public void shouldAddComponentToPanel() {
		Form formMock = mock(Form.class);
		Table tableMock = mock(Table.class);

		panel.addComponent(formMock);
		panel.addComponent(tableMock);

		verify(formMock).setPanel(panel);
		verify(tableMock).setPanel(panel);

		assertThat(panel.getElements().size(), is(2));
	}

	@Test
	public void shouldFindNextCompontent() {
		Form formMock = mock(Form.class);
		Table tableMock = mock(Table.class);

		panel.addComponent(formMock);
		panel.addComponent(tableMock);

		Table resultTable = panel.findNextElement(Table.class, formMock);

		assertThat(resultTable, is(tableMock));
	}

	@Test
	public void shouldNotFindItself() {
		Form formMock = mock(Form.class);
		Form form2Mock = mock(Form.class);

		panel.addComponent(formMock);
		panel.addComponent(form2Mock);

		Form resultForm = panel.findNextElement(Form.class, formMock);

		assertThat(resultForm, is(form2Mock));
	}

	@Test
	public void shouldFindFirstForm() {
		Form formMock = mock(Form.class);
		Form form2Mock = mock(Form.class);

		panel.addComponent(formMock);
		panel.addComponent(form2Mock);

		Form resultForm = panel.findNextElement(Form.class);

		assertThat(resultForm, is(formMock));
	}

	@Test
	public void shouldReturnNullOnEmptyList() {
		assertThat(panel.findNextElement(Form.class), nullValue());
	}

	@Test
	public void shouldRememberPresenter() {
		Presenter presenter = createDummyPresenter();
		panel.setPresenter(presenter);
		assertThat(panel.getPresenter(), sameInstance(presenter));
	}

	private Presenter createDummyPresenter() {
		Presenter presenter = new Presenter() {

			@Override
			public void attachDialog(String id, boolean withMargin) {
				// Nothing to do
			}

			@Override
			public void detachDialog() {
				// Nothing to do
			}
		};
		return presenter;
	}

	@Test
	public void shouldNotifyEventHandlerOnAttachDialog() {
		panel.setPresenter(presenterMock);
		panel.attachDialog("dialogId");
		verify(presenterMock).attachDialog("dialogId", false);
	}

	@Test
	public void shouldNotifyEventHandlerOnDetachDialog() {
		panel.setPresenter(presenterMock);
		panel.detachDialog();
		verify(presenterMock).detachDialog();
	}
}

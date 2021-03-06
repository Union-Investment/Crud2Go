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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ConfirmationDialogProvider.Result;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class ConfirmationDialogProviderTest {

	@Mock
	private Closure<?> actionClosure;

	@Mock
	private Window window;
	
	@Captor
	private ArgumentCaptor<ConfirmDialog> dialogCaptor;

	@Captor
	private ArgumentCaptor<Result> result;

	@Rule
	public LiferayContext vaadinContext = new LiferayContext();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateDialog() {
		createDialog();
		
		assertThat(dialogCaptor.getValue().getCaption(), is("Title"));
		assertThat(dialogCaptor.getValue().getOkButton().getCaption(), is("Accept"));
		assertThat(dialogCaptor.getValue().getCancelButton().getCaption(), is("Decline"));
	}

	@Test
	public void shouldDisableOkButtonOnClick() {
		createDialog();
		assertThat(dialogCaptor.getValue().getOkButton().isDisableOnClick(), is(true));
	}

	@Test
	public void shouldDisableCancelButtonOnClick() {
		createDialog();
		assertThat(dialogCaptor.getValue().getCancelButton().isDisableOnClick(), is(true));
	}
	
	@SuppressWarnings("serial")
	@Test
	public void shouldCallClosure() {
		createDialog();
		Listener listener = dialogCaptor.getValue().getListener();
		listener.onClose(new ConfirmDialog() {{
			setConfirmed(true);
		}});

		verify(actionClosure).call(result.capture());
		assertThat(result.getValue().isConfirmed(), is(true));
	}

	private void createDialog() {
		ConfirmationDialogProvider provider = new ConfirmationDialogProvider(
				null);
		provider.doCall("Title", "Question", "Accept", "Decline", actionClosure);

		verify(UI.getCurrent()).addWindow(dialogCaptor.capture());
	}
}

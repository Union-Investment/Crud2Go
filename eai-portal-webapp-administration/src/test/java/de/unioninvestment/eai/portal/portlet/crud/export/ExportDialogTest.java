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
package de.unioninvestment.eai.portal.portlet.crud.export;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import de.unioninvestment.eai.portal.portlet.crud.CrudPortletApplication;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;

public class ExportDialogTest extends SpringPortletContextTest {

	@Mock
	private com.vaadin.ui.Table vaadinTableMock;

	@Mock
	private ExportTask exportTaskMock;

	@Mock
	private CrudPortletApplication applicationMock;

	@Mock
	private Window mainWindowMock;

	private ExportDialog dialog;

	protected CloseListener closeListener;

	@Mock
	private CloseListener closeListenerMock;

	@Captor
	private ArgumentCaptor<ComponentErrorEvent> capturedException;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(vaadinTableMock.getApplication()).thenReturn(applicationMock);
		when(applicationMock.getMainWindow()).thenReturn(mainWindowMock);

		dialog = new ExportDialog(vaadinTableMock, exportTaskMock, true) {
			private static final long serialVersionUID = 1L;

			public void addListener(CloseListener listener) {
				closeListener = listener;
				super.addListener(listener);
			};
		};
	}

	@Test
	public void shouldRegisterCloseListener() {
		assertThat(closeListener, instanceOf(CloseListener.class));
	}

	@Test
	public void shouldCancelTaskOnCloseBeforeFinish() {
		closeListener.windowClose(null);
		verify(exportTaskMock).cancel();
	}

	@Test
	public void shouldNotCancelTaskOnCloseAfterFinish() {
		when(exportTaskMock.isFinished()).thenReturn(true);
		closeListener.windowClose(null);
		verify(exportTaskMock, never()).cancel();
	}

	@Test
	public void shouldReenableTableOnClose() {
		closeListener.windowClose(null);
		verify(vaadinTableMock).setEnabled(true);
	}

	@Test
	public void shouldUpdateIndicatorOnUpdateFromTask() {
		dialog.updateProgress(0.5f);
		assertThat(dialog.getIndicator().getValue(), is((Object) 0.5f));
	}

	@Test
	public void shouldAutomaticallySendFileToBrowserOnFinish() {
		dialog.finished();
		verify(exportTaskMock).sendToClient("_blank");
	}

	@Test
	public void shouldCloseDialogOnFinish() {
		dialog.addListener(closeListenerMock);
		dialog.finished();
		verify(closeListenerMock).windowClose(any(CloseEvent.class));
	}

	@Test
	public void shouldDisplayErrorsOnExportException() {
		TechnicalCrudPortletException myException = new TechnicalCrudPortletException(
				"Test");
		dialog.handleException(myException);

		verify(applicationMock).terminalError(capturedException.capture());
		assertThat(capturedException.getValue().getThrowable(),
				is((Throwable) myException));
	}

	@Test
	public void shouldCloseOnExportException() {
		dialog.addListener(closeListenerMock);
		TechnicalCrudPortletException myException = new TechnicalCrudPortletException(
				"Test");
		dialog.handleException(myException);

		verify(closeListenerMock).windowClose(any(CloseEvent.class));
	}

	@Test
	public void shouldReenableTableOnFinish() {
		dialog.finished();
		verify(vaadinTableMock).setEnabled(true);
	}

}

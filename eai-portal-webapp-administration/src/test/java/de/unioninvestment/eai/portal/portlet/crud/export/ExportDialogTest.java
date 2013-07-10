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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window.CloseListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class ExportDialogTest extends SpringPortletContextTest {

	@Mock
	private com.vaadin.ui.Table vaadinTableMock;

	@Mock
	private ExportTask exportTaskMock;

	private ExportDialog dialog;

	protected CloseListener closeListener;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void setUp() {
		liferayContext.initialize();

		MockitoAnnotations.initMocks(this);
		when(vaadinTableMock.getUI()).thenReturn(UI.getCurrent());

		dialog = new ExportDialog(vaadinTableMock, exportTaskMock, true) {
			private static final long serialVersionUID = 1L;

			public void addListener(CloseListener listener) {
				closeListener = listener;
				super.addListener(listener);
			};
		};
		dialog.setParent(UI.getCurrent());
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
		dialog.finished();
		verify(UI.getCurrent()).removeWindow(dialog);
	}

	@Test
	public void shouldDisplayErrorsOnExportException() {
		TechnicalCrudPortletException myException = new TechnicalCrudPortletException(
				"Test");
		dialog.handleException(myException);

		liferayContext.shouldShowNotification("Test", null, Type.ERROR_MESSAGE);
	}

	@Test
	public void shouldCloseOnExportException() {
		TechnicalCrudPortletException myException = new TechnicalCrudPortletException(
				"Test");
		dialog.handleException(myException);

		verify(UI.getCurrent()).removeWindow(dialog);
	}

	@Test
	@Ignore
	public void shouldReenableTableOnFinish() {
		dialog.finished();
		verify(vaadinTableMock).setEnabled(true);
	}

}

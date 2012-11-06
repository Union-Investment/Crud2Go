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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel.Presenter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelView;

public class PanelPresenterTest {

	private PanelPresenter panelPresenter;

	@Mock
	private PanelView view;

	@Mock
	private Panel model;

	@Mock
	private PanelContentPresenter defaultPanelContentPresenter;

	@Mock
	PanelContentView defaultView = mock(PanelContentView.class);

	Map<String, DialogPresenter> dialogPresenterMap = new HashMap<String, DialogPresenter>();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(defaultPanelContentPresenter.getView()).thenReturn(defaultView);
		panelPresenter = new PanelPresenter(view, model, dialogPresenterMap);
	}

	@Test
	public void shouldSetCaptionOnViewWhenTab() throws Exception {
		Tab tab = mock(Tab.class);
		String title = "Title";
		when(tab.getTitle()).thenReturn(title);
		panelPresenter = new PanelPresenter(view, tab, dialogPresenterMap);
		verify(view).setCaption(title);
	}

	@Test
	public void shouldSetDefaultView() {
		panelPresenter.setDefaultPresenter(defaultPanelContentPresenter);
		verify(view).setContent(defaultView);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenDialogDoesNotExist() {
		panelPresenter.attachDialog("dialogId", false);
	}

	@Test
	public void shouldSetPresenter() {
		verify(model).setPresenter(Matchers.isA(Presenter.class));
	}

	@Test
	public void shouldAttachDialog() {

		// Given a PanelPresenter with a dialog map
		PanelContentView dialogView = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter = new DialogPresenter(dialogView,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy = spy(dialogPresenter);
		dialogPresenterMap.put("dialogId", dialogPresenterSpy);

		panelPresenter = new PanelPresenter(view, model, dialogPresenterMap);

		// when attachDialog is called with the id of an existing dialog
		panelPresenter.attachDialog("dialogId", false);

		// then this dialog should be set
		verify(view).setContent(dialogPresenter.getView());
		// and notified about being attached
		verify(dialogPresenterSpy).notifyAboutBeingAttached(panelPresenter);
	}

	@Test
	public void shouldDetachDialog() {

		// Given a PanelPresenter with a dialog map
		PanelContentView dialogView = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter = new DialogPresenter(dialogView,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy = spy(dialogPresenter);
		dialogPresenterMap.put("dialogId", dialogPresenterSpy);

		panelPresenter = new PanelPresenter(view, model, dialogPresenterMap);
		panelPresenter.setDefaultPresenter(defaultPanelContentPresenter);
		// ansonsten würde das implizite view.setContent aus dem obigen
		// setDefaultPresenter unten bei
		// verify(view).setContent(defaultPanelContentPresenter.getView());
		// mitgezählt
		reset(view);

		// when attachDialog is called first with the id of an existing dialog
		panelPresenter.attachDialog("dialogId", false);

		// and also detachDialog is called afterwards
		panelPresenter.detachDialog();

		// then the default view should have been restored
		verify(view).setContent(dialogView);
		// default view is set directly after creation (first time) and during
		// detach (second time)
		verify(view).setContent(defaultPanelContentPresenter.getView());

		// and this dialog should be notified about being detached
		verify(dialogPresenterSpy).notifyAboutBeingDetached();
	}

	@Test
	public void shouldNavigateToFormerDialogOnBackButton() {

		// Given a PanelPresenter with two potential dialogs

		PanelContentView dialogView1 = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter1 = new DialogPresenter(dialogView1,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy1 = spy(dialogPresenter1);
		dialogPresenterMap.put("dialogId1", dialogPresenterSpy1);

		PanelContentView dialogView2 = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter2 = new DialogPresenter(dialogView2,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy2 = spy(dialogPresenter2);
		dialogPresenterMap.put("dialogId2", dialogPresenterSpy2);

		panelPresenter = new PanelPresenter(view, model, dialogPresenterMap);
		panelPresenter.setDefaultPresenter(defaultPanelContentPresenter);

		// and given that all three dialogs are attached one after the other
		// as if user goes to dialog 1, from there to dialog 2 and from there to
		// dialog 3
		panelPresenter.attachDialog("dialogId1", false);
		panelPresenter.attachDialog("dialogId2", false);

		// reset Mockito's method-counters
		reset(view);
		reset(dialogPresenterSpy1);
		reset(dialogPresenterSpy2);

		// and if the user goes back once (from dialog 2 to dialog 1)
		panelPresenter.detachDialog();

		// then dialog 2 should be detached
		verify(dialogPresenterSpy2).notifyAboutBeingDetached();
		// and dialog 1 should be restored
		verify(dialogPresenterSpy1).notifyAboutBeingAttached(panelPresenter);
		verify(view).setContent(dialogView1);
		assertSame(dialogPresenterSpy1,
				panelPresenter.getCurrentContentPresenter());

		// and if the user goes back for the third time (from dialog 1 to the
		// main page)
		panelPresenter.detachDialog();

		// then dialog 1 should be detached
		verify(dialogPresenterSpy1).notifyAboutBeingDetached();
		// and the main page should be restored
		assertSame(defaultPanelContentPresenter,
				panelPresenter.getCurrentContentPresenter());
		verify(view).setContent(defaultPanelContentPresenter.getView());
	}

	@Test
	public void shouldNavigateToFormerDialogOnBackButtonExtended() {

		// Given a PanelPresenter with three potential dialogs

		PanelContentView dialogView1 = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter1 = new DialogPresenter(dialogView1,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy1 = spy(dialogPresenter1);
		dialogPresenterMap.put("dialogId1", dialogPresenterSpy1);

		PanelContentView dialogView2 = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter2 = new DialogPresenter(dialogView2,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy2 = spy(dialogPresenter2);
		dialogPresenterMap.put("dialogId2", dialogPresenterSpy2);

		PanelContentView dialogView3 = (PanelContentView) mock(
				VerticalLayout.class,
				withSettings().extraInterfaces(PanelContentView.class));
		DialogPresenter dialogPresenter3 = new DialogPresenter(dialogView3,
				mock(Dialog.class));
		DialogPresenter dialogPresenterSpy3 = spy(dialogPresenter3);
		dialogPresenterMap.put("dialogId3", dialogPresenterSpy3);

		panelPresenter = new PanelPresenter(view, model, dialogPresenterMap);
		panelPresenter.setDefaultPresenter(defaultPanelContentPresenter);

		// and given that all three dialogs are attached one after the other
		// as if user goes to dialog 1, from there to dialog 2 and from there to
		// dialog 3
		panelPresenter.attachDialog("dialogId1", false);
		panelPresenter.attachDialog("dialogId2", false);
		panelPresenter.attachDialog("dialogId3", false);

		// reset Mockito's method-counters
		reset(view);
		reset(dialogPresenterSpy1);
		reset(dialogPresenterSpy2);
		reset(dialogPresenterSpy3);

		// and the user goes back the first time (from dialog 3 to dialog 2)
		panelPresenter.detachDialog();

		// then dialog 3 should be detached
		verify(dialogPresenterSpy3).notifyAboutBeingDetached();
		// and dialog 2 should be restored
		verify(dialogPresenterSpy2).notifyAboutBeingAttached(panelPresenter);
		verify(view).setContent(dialogView2);
		assertSame(dialogPresenterSpy2,
				panelPresenter.getCurrentContentPresenter());

		// and if the user goes back once more (from dialog 2 to dialog 1)
		panelPresenter.detachDialog();

		// then dialog 2 should be detached
		verify(dialogPresenterSpy2).notifyAboutBeingDetached();
		// and dialog 1 should be restored
		verify(dialogPresenterSpy1).notifyAboutBeingAttached(panelPresenter);
		verify(view).setContent(dialogView1);
		assertSame(dialogPresenterSpy1,
				panelPresenter.getCurrentContentPresenter());

		// and if the user goes back for the third time (from dialog 1 to the
		// main page)
		panelPresenter.detachDialog();

		// then dialog 1 should be detached
		verify(dialogPresenterSpy1).notifyAboutBeingDetached();
		// and the main page should be restored
		assertSame(defaultPanelContentPresenter,
				panelPresenter.getCurrentContentPresenter());
		// default view is set directly after creation (first time) and during
		// detach (second time)
		verify(view).setContent(defaultPanelContentPresenter.getView());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionOnDetachWhenNoDefaultPresenterHasBeenSet() {
		DialogPresenter dialogPresenterMock = mock(DialogPresenter.class);
		when(dialogPresenterMock.getView()).thenReturn(defaultView);
		dialogPresenterMap.put("dialogId", dialogPresenterMock);
		panelPresenter.attachDialog("dialogId", false);

		panelPresenter.detachDialog();
	}

}

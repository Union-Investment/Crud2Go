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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.portlet.MockPortalContext;
import org.springframework.mock.web.portlet.MockPortletURL;
import org.vaadin.peter.contextmenu.ContextMenu;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.Terminal.ErrorEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletConfigurationPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.BusinessExceptionMessage;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class CrudPortletApplicationTest extends SpringPortletContextTest {

	private URL applicationUrl;

	@InjectMocks
	private CrudPortletApplication app = new CrudPortletApplication();

	private Window windowSpy;
	@Mock
	private ResourceResponse response;
	@Mock
	private ResourceRequest request;
	@Mock
	private ApplicationContext contextMock;
	@Mock
	private Settings settingsMock;
	@Mock
	private Principal principalMock;
	@Mock
	private Portlet portletMock;
	@Mock
	private PortletPresenter portletPresenterMock;
	@Mock
	private PortletView portletViewMock;
	@Mock
	private ConfigurationService configurationServiceMock;
	@Mock
	private GuiBuilder guiBuilderMock;

	@Mock
	@SuppressWarnings("unused")
	private EventBus eventBusMock;

	@Mock
	private ModelBuilder modelBuilderMock;
	@Mock
	private PresenterFactory presenterFactoryMock;
	@Mock
	private PortletConfigurationPresenter portletConfigurationPresenterMock;
	@Mock
	private View portletConfigurationViewMock;

	@Mock(name = "viewPage")
	private VerticalLayout viewPageMock;

	@Mock(name = "editPage")
	@SuppressWarnings("unused")
	private ComponentContainer editPageMock;

	@Mock
	private transient ScriptModelFactory scriptModelFactoryMock;

	@Mock
	ScriptModelBuilder scriptModelBuilderMock;

	@Mock
	private ModelFactory modelFactoryMock;

	@Mock
	private Config configMock;

	@Mock
	private ThemeDisplay themeDisplayMock;

	@Before
	public void setUp() throws MalformedURLException {
		applicationUrl = new URL("http://xxx");

		MockitoAnnotations.initMocks(this);

		app.onRequestStart(request, response);

		when(settingsMock.getHelpUrl()).thenReturn("http://help.us");
		when(response.createRenderURL()).thenReturn(
				new MockPortletURL(new MockPortalContext(), "myurl"));
		when(modelFactoryMock.getBuilder(isA(Config.class))).thenReturn(
				modelBuilderMock);
		when(modelBuilderMock.build()).thenReturn(portletMock);

		when(presenterFactoryMock.portletConfigurationPresenter()).thenReturn(
				portletConfigurationPresenterMock);
		when(portletConfigurationPresenterMock.getView()).thenReturn(
				portletConfigurationViewMock);
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);
		when(configurationServiceMock.getPortletConfig("4711", 14008))
				.thenReturn(configMock);

		when(request.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(
				themeDisplayMock);
		when(themeDisplayMock.getScopeGroupId()).thenReturn(14008L);

	}

	private void initializeWindowSpy() {
		windowSpy = spy(new Window());
		app.setMainWindow(windowSpy);
		windowSpy.setApplication(app);
	}

	/**
	 * Bereitet die UserPrincipals und das Userrepository für die Tests vor,
	 * sodass ein NamedUser mit den übergebenen Rollen ausgestattet ist.
	 * 
	 * @param roles
	 */
	private void provideUserWithRoles(String... roles) {

		when(request.getUserPrincipal()).thenReturn(principalMock);

		final Set<String> roleSet = roles == null ? null : new HashSet<String>(
				Arrays.asList(roles));
		when(request.isUserInRole(isA(String.class))).thenAnswer(
				new Answer<Boolean>() {
					@Override
					public Boolean answer(InvocationOnMock invocation)
							throws Throwable {
						return roleSet != null
								&& roleSet.contains(invocation.getArguments()[0]);
					}
				});
		when(principalMock.getName()).thenReturn("carsten");
	}

	@Test
	public void shouldDisplayErrorsAsNotifications() {
		provideUserWithRoles();
		app.start(applicationUrl, null, contextMock);
		initializeWindowSpy();

		RuntimeException rootCause = new RuntimeException("MyMessage");
		ErrorEvent event = app.new ApplicationError(rootCause);

		app.getErrorHandler().terminalError(event);

		verify(windowSpy).showNotification("MyMessage",
				Notification.TYPE_ERROR_MESSAGE);
	}

	@Test
	public void shouldDisplayRootCauseErrorsAsNotifications() {
		provideUserWithRoles();
		app.start(applicationUrl, null, contextMock);
		initializeWindowSpy();

		RuntimeException rootCause = new RuntimeException("MyMessage");
		RuntimeException higherCause = new RuntimeException(rootCause);
		ErrorEvent event = app.new ApplicationError(higherCause);

		app.getErrorHandler().terminalError(event);

		verify(windowSpy).showNotification("MyMessage",
				Notification.TYPE_ERROR_MESSAGE);
	}

	@Test
	public void shouldDisplayUnconfiguredMessage() {
		app.onRequestStart(request, response);
		app.start(applicationUrl, null, contextMock);

	}

	@Test
	public void shouldDisplayMessageOnConfigurationError() {
		app.onRequestStart(request, response);

		PortletConfig portletConfig = new PortletConfig();

		when(configMock.getPortletConfig()).thenReturn(portletConfig);
		when(request.getAttribute(WebKeys.PORTLET_ID)).thenReturn("4711");

		when(modelBuilderMock.build()).thenThrow(new RuntimeException("bla"));

		ArgumentCaptor<Component> components = ArgumentCaptor
				.forClass(Component.class);

		app.start(applicationUrl, null, contextMock);

		verify(viewPageMock, times(3)).addComponent(components.capture());
		// PortletId
		assertThat(components.getAllValues().get(0), instanceOf(Label.class));
		// CommunityId
		assertThat(components.getAllValues().get(1), instanceOf(Label.class));
		assertThat(components.getAllValues().get(2),
				instanceOf(BusinessExceptionMessage.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldDisplayPortletViewForExistingConfiguration() {
		app.onRequestStart(request, response);
		app.start(applicationUrl, null, contextMock);

		PortletConfig portletConfig = new PortletConfig();

		when(configMock.getPortletConfig()).thenReturn(portletConfig);
		when(request.getAttribute(WebKeys.PORTLET_ID)).thenReturn("4711");
		when(modelBuilderMock.build()).thenReturn(portletMock);
		when(guiBuilderMock.build(portletMock))
				.thenReturn(portletPresenterMock);
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);

		when(
				scriptModelFactoryMock.getBuilder(any(Portlet.class),
						any((new HashMap<Object, Object>()).getClass())))
				.thenReturn(scriptModelBuilderMock);

		app.refreshViews();

		verify(viewPageMock).addComponent(portletViewMock);
	}

	@Test
	public void testHandleResourceRequest() {
		app.start(applicationUrl, null, contextMock);
		initializeWindowSpy();
		expectWindowContentChange(app, PortletMode.EDIT, app.getEditContent());
		expectWindowContentChange(app, PortletMode.VIEW, app.getViewContent());
		expectWindowContentChange(app, PortletMode.HELP, app.getHelpContent());
	}

	private void expectWindowContentChange(CrudPortletApplication app,
			PortletMode targetMode, ComponentContainer expectedContent) {
		reset(windowSpy);
		when(request.getPortletMode()).thenReturn(targetMode);
		app.handleResourceRequest(request, response, windowSpy);
		verify(windowSpy).setContent(expectedContent);
	}

	@Test
	public void shouldRefreshEditPageBeforeEntering() {
		app.start(applicationUrl, null, contextMock);
		app.setPortletDomain(portletMock);
		initializeWindowSpy();

		when(request.getPortletMode()).thenReturn(PortletMode.VIEW);
		app.handleResourceRequest(request, response, windowSpy);
		reset(portletConfigurationPresenterMock);

		when(request.getPortletMode()).thenReturn(PortletMode.EDIT);
		app.handleResourceRequest(request, response, windowSpy);
		verify(portletConfigurationPresenterMock).refresh(
				app.getPortletDomain());
	}

	@Test
	public void shouldNotRefreshIfAlreadyOnEditPage() {
		app.start(applicationUrl, null, contextMock);
		app.setPortletDomain(portletMock);
		initializeWindowSpy();

		when(request.getPortletMode()).thenReturn(PortletMode.EDIT);
		app.handleResourceRequest(request, response, windowSpy);
		reset(portletConfigurationPresenterMock);

		app.handleResourceRequest(request, response, windowSpy);
		verify(portletConfigurationPresenterMock, never()).refresh(
				app.getPortletDomain());
	}

	@Test
	public void shouldRegisterPopupEventHandler() {
		app.start(applicationUrl, null, contextMock);
		initializeWindowSpy();

		verify(eventBusMock).addHandler(ShowPopupEvent.class, app);
	}

	@Test
	public void shouldAllowAddingComponentsToTheEndOfViewPage() {
		app.start(applicationUrl, null, contextMock);

		ContextMenu menu = new ContextMenu();
		app.addToView(menu);

		verify(viewPageMock).addComponent(menu);
	}

	@Test
	public void shouldNotAllowAddingComponentsTwice() {
		app.start(applicationUrl, null, contextMock);

		ContextMenu menu = new ContextMenu();
		app.addToView(menu);
		app.addToView(menu);

		verify(viewPageMock, times(1)).addComponent(menu);
	}

	@Test
	public void shouldRemoveAddedComponents() {
		app.start(applicationUrl, null, contextMock);

		ContextMenu menu = new ContextMenu();
		app.addToView(menu);
		app.removeAddedComponentsFromView();

		verify(viewPageMock).removeComponent(menu);

	}

	@Test
	public void shouldProvideInformationIfInitializationIsInProgress() {

		assertThat(app.isInitializing(), is(true));

		app.onRequestStart(request, response);
		app.start(applicationUrl, null, contextMock);

		final PortletConfig portletConfig = new PortletConfig();

		when(configMock.getPortletConfig()).thenAnswer(
				new Answer<PortletConfig>() {
					@Override
					public PortletConfig answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(app.isInitializing(), is(true));
						return portletConfig;
					}
				});
		when(request.getAttribute(WebKeys.PORTLET_ID)).thenReturn("4711");
		when(modelBuilderMock.build()).thenAnswer(new Answer<Portlet>() {
			@Override
			public Portlet answer(InvocationOnMock invocation) throws Throwable {
				assertThat(app.isInitializing(), is(true));
				return portletMock;
			}
		});

		when(guiBuilderMock.build(portletMock)).thenAnswer(
				new Answer<PortletPresenter>() {
					@Override
					public PortletPresenter answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(app.isInitializing(), is(true));
						return portletPresenterMock;
					}

				});
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);

		when(
				scriptModelFactoryMock.getBuilder(any(Portlet.class),
						any((new HashMap<Object, Object>()).getClass())))
				.thenAnswer(new Answer<ScriptModelBuilder>() {
					@Override
					public ScriptModelBuilder answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(app.isInitializing(), is(true));
						return scriptModelBuilderMock;
					}
				});

		app.refreshViews();

		verify(viewPageMock).addComponent(portletViewMock);
		assertThat(app.isInitializing(), is(false));

	}
}

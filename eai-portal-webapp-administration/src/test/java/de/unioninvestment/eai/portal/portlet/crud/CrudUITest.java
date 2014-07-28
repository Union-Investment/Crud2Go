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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.portlet.PortletMode;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.portlet.MockPortalContext;
import org.springframework.mock.web.portlet.MockPortletURL;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelPreferences;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.validation.ModelValidator;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration.PortletConfigurationPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletConfigurationView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.BusinessExceptionMessage;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortlet;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class CrudUITest extends SpringPortletContextTest {

	@InjectMocks
	private CrudUI ui = new CrudUI() {
		@Override
		public void accessSynchronously(Runnable runnable)
				throws com.vaadin.ui.UIDetachedException {
			runnable.run();
		};

		@Override
		public Future<Void> access(Runnable runnable) {
			runnable.run();
			return null;
		};

		protected org.springframework.context.ApplicationContext getSpringContext(
				com.vaadin.server.VaadinRequest request) {
			return null;
		};

		public VaadinSession getSession() {
			return vaadinSession;
		};

		public Page getPage() {
			return liferayContext.getPageMock();
		};
		
		
	};

	@Mock
	private ConnectorTracker connectorTrackerMock;
	
	@Mock
	private UiHistory uiHistoryMock;
	@Mock
	private Settings settingsMock;
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
	private EventBus eventBusMock;

	@Mock
	private ModelBuilder modelBuilderMock;
	@Mock
	private PresenterFactory presenterFactoryMock;
	@Mock
	private PortletConfigurationPresenter portletConfigurationPresenterMock;
	@Mock
	private PortletConfigurationView portletConfigurationViewMock;

	@Mock(name = "viewPage")
	private VerticalLayout viewPageMock;

	@Mock(name = "editPage")
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

	@Mock
	private RenderRequest renderRequestMock;

	@Mock
	private RenderResponse renderResponseMock;

	@Mock
	private WebBrowser browserMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext("4711", 14008L);

	private VaadinSession vaadinSession;

	@Mock
	private VaadinService vaadinServiceMock;

	private PortletConfig portletConfig;

	@Before
	public void setUp() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);

		when(settingsMock.getHelpUrl()).thenReturn("http://help.us");
		when(
				((ResourceResponse) liferayContext.getPortletResponseMock())
						.createRenderURL()).thenReturn(
				new MockPortletURL(new MockPortalContext(), "myurl"));
		when(
				modelFactoryMock.getBuilder(isA(EventBus.class),
						isA(Config.class), isA(ModelPreferences.class))).thenReturn(modelBuilderMock);
		when(modelBuilderMock.build()).thenReturn(portletMock);

		when(presenterFactoryMock.portletConfigurationPresenter()).thenReturn(
				portletConfigurationPresenterMock);
		when(portletConfigurationPresenterMock.getView()).thenReturn(
				portletConfigurationViewMock);
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);
		when(configurationServiceMock.getPortletConfig("4711", 14008))
				.thenReturn(configMock);

		when(liferayContext.getPageMock().getLocation()).thenReturn(
				URI.create("http://localhost"));
		when(
				liferayContext.getPortletRequestMock().getAttribute(
						WebKeys.PORTLET_ID)).thenReturn("4711");
		when(
				liferayContext.getPortletRequestMock().getAttribute(
						WebKeys.THEME_DISPLAY)).thenReturn(themeDisplayMock);
		when(themeDisplayMock.getScopeGroupId()).thenReturn(14008L);

		when(Page.getCurrent().getWebBrowser()).thenReturn(browserMock);
		when(browserMock.getLocale()).thenReturn(Locale.GERMANY);
	}

	/**
	 * Bereitet die UserPrincipals und das Userrepository für die Tests vor,
	 * sodass ein NamedUser mit den übergebenen Rollen ausgestattet ist.
	 * 
	 * @param roles
	 */
	private void provideUserWithRoles(String... roles) {

		when(liferayContext.getVaadinPortletRequestMock().getRemoteUser())
				.thenReturn("carsten");

		final Set<String> roleSet = roles == null ? null : new HashSet<String>(
				Arrays.asList(roles));
		when(
				liferayContext.getVaadinPortletRequestMock().isUserInRole(
						isA(String.class))).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return roleSet != null
						&& roleSet.contains(invocation.getArguments()[0]);
			}
		});
	}

	@Test
	public void shouldApplyPreferredBrowserLocale() {
		ui.init(liferayContext.getVaadinPortletRequestMock());

		assertThat(ui.getLocale(), is(Locale.GERMANY));
	}

	@Test
	public void shouldAddItselfToUiHistory() {
		ui.init(liferayContext.getVaadinPortletRequestMock());
		
		verify(uiHistoryMock).add(ui);
	}
	
	@Test
	public void shouldReturnDefaultHistoryLimitFromSettings() {
		initializeUI();
		ui.portletConfig = null;		
		when(settingsMock.getUiHistoryLimit()).thenReturn(3);
		
		assertThat(ui.getHistoryLimit(), is(3));
	}
	
	@Test
	public void shouldReturnSpecificHistoryLimitIfSet() {
		initializeUI();
		stubPortletInitialization();
		when(settingsMock.getUiHistoryLimit()).thenReturn(3);
		portletConfig.setHistoryLimit(4);
		
		assertThat(ui.getHistoryLimit(), is(4));
	}
	
	@Test
	public void shouldRegisterFallbackErrorHandler() {
		provideUserWithRoles();
		initializeUI();

		verify(liferayContext.getVaadinSessionMock()).setErrorHandler(
				isA(CrudErrorHandler.class));
	}

	private void initializeUI() {
		ui.init(liferayContext.getVaadinPortletRequestMock());
	}

	@Test
	@Ignore
	public void shouldDisplayUnconfiguredMessage() {
		initializeUI();

	}

	@Test
	public void shouldDisplayMessageOnConfigurationError() {
		PortletConfig portletConfig = new PortletConfig();

		when(configMock.getPortletConfig()).thenReturn(portletConfig);
		when(
				configurationServiceMock.isConfigured(configMock,
						liferayContext.getPortletPreferencesMock()))
				.thenReturn(true);

		when(modelBuilderMock.build()).thenThrow(new RuntimeException("bla"));

		ArgumentCaptor<Component> components = ArgumentCaptor
				.forClass(Component.class);

		initializeUI();

		verify(viewPageMock, times(3)).addComponent(components.capture());
		// PortletId
		assertThat(components.getAllValues().get(0), instanceOf(Label.class));
		// CommunityId
		assertThat(components.getAllValues().get(1), instanceOf(Label.class));
		assertThat(components.getAllValues().get(2),
				instanceOf(BusinessExceptionMessage.class));
	}

	@Test
	public void shouldDisplayPortletViewForExistingConfiguration() {
		initializeUI();

		stubPortletInitialization();

		ui.refreshViews();

		verify(viewPageMock, never()).addComponent(
				isA(BusinessExceptionMessage.class));
		verify(viewPageMock).addComponent(portletViewMock);
	}

	@Test
	public void shouldUpdatePortletTitleInPreferences()
			throws ReadOnlyException, ValidatorException, IOException {
		initializeUI();
		stubPortletInitialization();
		when(portletMock.getTitle()).thenReturn("newTitle");

		ui.refreshViews();

		verify(viewPageMock, never()).addComponent(
				isA(BusinessExceptionMessage.class));
		verify(liferayContext.getPortletPreferencesMock()).setValue(
				CrudVaadinPortlet.PORTLET_TITLE_PREF_KEY, "newTitle");
		verify(liferayContext.getPortletPreferencesMock()).store();
	}

	@Test
	public void shouldNotUpdateUnchangedPortletTitleInPreferences()
			throws ReadOnlyException, ValidatorException, IOException {
		initializeUI();
		stubPortletInitialization();
		when(
				liferayContext.getPortletPreferencesMock().getValue(
						CrudVaadinPortlet.PORTLET_TITLE_PREF_KEY, null))
				.thenReturn("sameTitle");
		when(portletMock.getTitle()).thenReturn("sameTitle");

		ui.refreshViews();

		verify(viewPageMock, never()).addComponent(
				isA(BusinessExceptionMessage.class));
		verify(liferayContext.getPortletPreferencesMock()).getValue(
				CrudVaadinPortlet.PORTLET_TITLE_PREF_KEY, null);
		verify(liferayContext.getPortletPreferencesMock(), never()).store();
	}

	@SuppressWarnings("unchecked")
	private void stubPortletInitialization() {
		portletConfig = new PortletConfig();
		when(
				configurationServiceMock.isConfigured(configMock,
						liferayContext.getPortletPreferencesMock()))
				.thenReturn(true);

		when(configMock.getPortletConfig()).thenReturn(portletConfig);
		when(modelBuilderMock.build()).thenReturn(portletMock);
		when(guiBuilderMock.build(portletMock))
				.thenReturn(portletPresenterMock);
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);

		when(
				scriptModelFactoryMock.getBuilder(any(EventBus.class),
						any(Portlet.class),
						any((new HashMap<Object, Object>()).getClass())))
				.thenReturn(scriptModelBuilderMock);
	}

	@Test
	public void testHandleResourceRequest() {
		initializeUI();
		expectWindowContentChange(PortletMode.EDIT, ui.getEditContent());
		expectWindowContentChange(PortletMode.VIEW, ui.getViewContent());
		expectWindowContentChange(PortletMode.HELP, ui.getHelpContent());
	}

	private void expectWindowContentChange(PortletMode targetMode,
			ComponentContainer expectedContent) {
		when(liferayContext.getPortletRequestMock().getPortletMode())
				.thenReturn(targetMode);
		callHandleResourceRequest();
		assertThat(ui.getContent(), is((Component) expectedContent));
	}

	private void callHandleResourceRequest() {
		ui.handleResourceRequest(
				(ResourceRequest) liferayContext.getPortletRequestMock(),
				(ResourceResponse) liferayContext.getPortletResponseMock(), ui);
	}

	@Test
	public void shouldRefreshEditPageBeforeEntering() {
		initializeUI();
		ui.setPortletDomain(portletMock);

		when(liferayContext.getPortletRequestMock().getPortletMode())
				.thenReturn(PortletMode.VIEW);
		callHandleResourceRequest();
		reset(portletConfigurationPresenterMock);

		when(liferayContext.getPortletRequestMock().getPortletMode())
				.thenReturn(PortletMode.EDIT);
		callHandleResourceRequest();
		verify(portletConfigurationPresenterMock).refresh(configMock);
	}

	@Test
	public void shouldNotRefreshIfAlreadyOnEditPage() {
		initializeUI();
		ui.setPortletDomain(portletMock);

		when(liferayContext.getPortletRequestMock().getPortletMode())
				.thenReturn(PortletMode.EDIT);
		callHandleResourceRequest();
		reset(portletConfigurationPresenterMock);

		callHandleResourceRequest();
		verify(portletConfigurationPresenterMock, never()).refresh(
				any(Config.class));
	}

	@Test
	public void shouldRegisterPopupEventHandler() {
		initializeUI();

		verify(eventBusMock).addHandler(ShowPopupEvent.class, ui);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldProvideInformationIfInitializationIsInProgress() {

		assertThat(ui.isInitializing(), is(true));

		initializeUI();

		final PortletConfig portletConfig = new PortletConfig();
		when(
				configurationServiceMock.isConfigured(configMock,
						liferayContext.getPortletPreferencesMock()))
				.thenReturn(true);

		when(configMock.getPortletConfig()).thenAnswer(
				new Answer<PortletConfig>() {
					@Override
					public PortletConfig answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(ui.isInitializing(), is(true));
						return portletConfig;
					}
				});
		when(modelBuilderMock.build()).thenAnswer(new Answer<Portlet>() {
			@Override
			public Portlet answer(InvocationOnMock invocation) throws Throwable {
				assertThat(ui.isInitializing(), is(true));
				return portletMock;
			}
		});

		when(guiBuilderMock.build(portletMock)).thenAnswer(
				new Answer<PortletPresenter>() {
					@Override
					public PortletPresenter answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(ui.isInitializing(), is(true));
						return portletPresenterMock;
					}

				});
		when(portletPresenterMock.getView()).thenReturn(portletViewMock);

		when(
				scriptModelFactoryMock.getBuilder(any(EventBus.class),
						any(Portlet.class), any((Map.class)))).thenAnswer(
				new Answer<ScriptModelBuilder>() {
					@Override
					public ScriptModelBuilder answer(InvocationOnMock invocation)
							throws Throwable {
						assertThat(ui.isInitializing(), is(true));
						return scriptModelBuilderMock;
					}
				});

		ui.refreshViews();

		verify(viewPageMock).addComponent(portletViewMock);
		assertThat(ui.isInitializing(), is(false));

	}

	@Test
	public void shouldInformPortletDomainAboutReloadOnRenderRequest() {
		initializeUI();
		ui.setPortletDomain(portletMock);
		ui.initializing = false;
		when(renderRequestMock.getPortletMode()).thenReturn(PortletMode.VIEW);
		vaadinSession = new VaadinSession(vaadinServiceMock);

		ui.handleRenderRequest(renderRequestMock, renderResponseMock, ui);

		verify(portletMock, times(1)).handleReload();
	}

	@Test
	public void shouldCreateModelPreferencesFromPortletPreferences() {
		when(liferayContext.getPortletPreferencesMock().getValue("portlet.page.height", null)).thenReturn("fit");
		when(liferayContext.getPortletPreferencesMock().getValue("portlet.page.minimum-height", null)).thenReturn("333");
		
		initializeUI();
		ModelPreferences prefs = ui.createModelPreferences();
		assertThat(prefs.getPageHeight(), is("fit"));
		assertThat(prefs.getPageMinimumHeight(), is(333));
	}

	@Test
	public void shouldIgnoreRenderRequestsWithoutSession() {
		initializeUI();
		ui.setPortletDomain(portletMock);
		ui.initializing = false;
		vaadinSession = null;
		when(renderRequestMock.getPortletMode()).thenReturn(PortletMode.VIEW);

		ui.handleRenderRequest(renderRequestMock, renderResponseMock, ui);

		verify(portletMock, never()).handleReload();
	}

	@Test
	public void shouldNotInformPortletDomainAboutReloadIfPortletModeIsNotVIEW() {
		initializeUI();
		ui.setPortletDomain(portletMock);
		ui.initializing = false;
		when(renderRequestMock.getPortletMode()).thenReturn(PortletMode.EDIT);
		vaadinSession = new VaadinSession(vaadinServiceMock);
		try {
			ui.handleRenderRequest(renderRequestMock, renderResponseMock, ui);
			ui.handleRenderRequest(renderRequestMock, renderResponseMock, ui);

		} catch (Exception e) {
			// ignore exceptions
		}

		verify(portletMock, never()).handleReload();
	}

	@Test
	public void shouldNotInformPortletDomainAboutReloadOnInitializingRenderRequest() {
		vaadinSession = new VaadinSession(vaadinServiceMock);
		ui.handleRenderRequest(renderRequestMock, renderResponseMock, ui);
		verify(portletMock, never()).handleReload();
	}

	@Test
	public void shouldValidateConfigurationOnlyWhenSettingsAreSet(){
		when(settingsMock.isValidateConfiguration()).thenReturn(true);
		stubPortletInitialization();
		ModelValidator modelValidatorMock = mock(ModelValidator.class);
		
		ui.modelValidator = modelValidatorMock;
		
		when(configurationServiceMock.isConfigured(any(Config.class), any(javax.portlet.PortletPreferences.class))).thenReturn(true);
		when(configurationServiceMock.getPortletConfig(any(String.class), anyLong())).thenReturn(configMock);
		
		ui.refreshViews();
		verify(modelValidatorMock,times(1)).validateModel(any(ModelBuilder.class), any(Portlet.class), any(PortletConfig.class));
	}
	
	@Test
	public void shouldNotValidateConfiguration_SettingsNotSet(){
		when(settingsMock.isValidateConfiguration()).thenReturn(false);
		stubPortletInitialization();
		ModelValidator modelValidatorMock = mock(ModelValidator.class);
		
		ui.modelValidator = modelValidatorMock;
		
		when(configurationServiceMock.isConfigured(any(Config.class), any(javax.portlet.PortletPreferences.class))).thenReturn(true);
		when(configurationServiceMock.getPortletConfig(any(String.class), anyLong())).thenReturn(configMock);
		
		ui.refreshViews();
		verify(modelValidatorMock,never()).validateModel(any(ModelBuilder.class), any(Portlet.class), any(PortletConfig.class));
	}

	
}

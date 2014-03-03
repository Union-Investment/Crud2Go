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

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinPortletSession.PortletListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfos;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.InitializingUI;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DatasourceInfoPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration.PortletConfigurationPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DatasourceInfoView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.BusinessExceptionMessage;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.Popup;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.PortletUriFragmentUtility;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.RequestProcessingLabel;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.crud.services.RequestProcessingLogService;
import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortlet;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.support.UnconfiguredMessage;
import de.unioninvestment.eai.portal.support.vaadin.timing.TimingPortletListener;
import de.unioninvestment.eai.portal.support.vaadin.validation.ValidationException;

/**
 * CRUD PortletPresenter Applikationsklasse. Enthaelt das Management der
 * verschiedenen PortletPresenter-Modus.
 * 
 * 
 * @author carsten.mjartan
 * 
 */
@Theme("crud2go")
@PreserveOnRefresh
@SuppressWarnings("deprecation")
public class CrudUI extends LiferayUI implements PortletListener,
		ShowPopupEventHandler, InitializingUI {


	private static final long serialVersionUID = 1L;

	private static final String LIFECYCLE_DATEFORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
	private static final Logger LIFECYCLE_LOGGER = LoggerFactory
			.getLogger(CrudUI.class.getPackage().getName() + ".lifecycle");
	
	private static final Logger LOG = LoggerFactory.getLogger(CrudUI.class);

	public static final String ROLE_ADMIN = "portlet-crud-adm";

	private Portlet portletDomain;
	private PortletPresenter portletGui;

	private DatasourceInfoPresenter datasourceInfo;

	private VerticalLayout viewPage = new VerticalLayout();
	private ComponentContainer editPage = new VerticalLayout();
	private ComponentContainer helpPage;

	@Autowired
	private transient Settings settings;

	@Autowired
	private transient EventBus eventBus;

	@Autowired
	private transient ConfigurationService configurationService;

	@Autowired
	private transient PresenterFactory presenterFactory;

	@Autowired
	private transient ModelFactory modelFactory;

	@Autowired
	private transient ScriptModelFactory scriptModelFactory;

	@Autowired
	private transient GuiBuilder guiBuilder;

	private PortletUriFragmentUtility portletUriFragmentUtility;

	private PortletConfigurationPresenter configurationPresenter;

	private Set<Component> registeredComponents = new HashSet<Component>();

	public enum ConfigStatus {
		START, UNKNOWN, NO_CONFIG, UNCONFIGURED, CONFIGURED
	}

	ConfigStatus status = ConfigStatus.START;
	boolean initializing = true;
	boolean configChanged = false;

	Config portletConfig;

	private RequestProcessingLabel requestProcessingLabel;

	private TimingPortletListener timingPortletListener;

	public enum LifecycleEvent {
		CRUD2GO_INIT,
		CRUD2GO_UI_INIT,
		CRUD2GO_UI_DETACH,
		CRUD2GO_SHUTDOWN
	}
	
	/**
	 * Initialisierung des PortletPresenter.
	 * 
	 */
	@Override
	public void doInit(VaadinRequest request) {
		setId(UUID.randomUUID().toString());
		logLifecycleEvent(LifecycleEvent.CRUD2GO_UI_INIT);

		autowireUiDependencies(request);

		VaadinSession.getCurrent().setErrorHandler(new CrudErrorHandler());

		applyBrowserLocale();

		helpPage = initializeHelpPage();

		setContent(viewPage);

		tryToSetPortletTitle(Context.getMessage("portlet.crud.window.name"));

		recreateEditPage();
		refreshViews();

		getPortletSession().addPortletListener(this);
	}

	public static void logLifecycleEvent(LifecycleEvent event) {
		String now = new SimpleDateFormat(LIFECYCLE_DATEFORMAT)
				.format(new Date());
		
		String uiId = "";
		String url = "";
		UI ui = UI.getCurrent();
		if (ui != null) {
			url = ui.getPage().getLocation().toString();
			uiId = ui.getId();
		}
		
		String sessionId = "";
		PortletRequest portletRequest = VaadinPortletService
				.getCurrentPortletRequest();
		if (portletRequest != null) {
			PortletSession session = portletRequest.getPortletSession(false);
			sessionId = session == null ? portletRequest.getRequestedSessionId() : session.getId();
		}
		
		LIFECYCLE_LOGGER.info(MessageFormat.format("{0};{1};{2};{3};{4}", now,
				uiId, url, sessionId, event.name()));
	}

	private void autowireUiDependencies(VaadinRequest request) {
		ApplicationContext context = getSpringContext(request);
		if (context != null) {
			context.getAutowireCapableBeanFactory().autowireBean(this);
		}
	}

	protected ApplicationContext getSpringContext(VaadinRequest request) {
		PortletRequest currentRequest = VaadinPortletService
				.getCurrentPortletRequest();
		if (currentRequest != null) {
			PortletContext pc = currentRequest.getPortletSession()
					.getPortletContext();
			org.springframework.context.ApplicationContext springContext = PortletApplicationContextUtils
					.getRequiredWebApplicationContext(pc);
			return springContext;

		} else {
			throw new IllegalStateException(
					"Found no current portlet request. Did you subclass PortletApplication in your Vaadin Application?");
		}
	}

	private void tryToSetPortletTitle(String title) {
		String realTitle = title != null ? title : Context
				.getMessage("portlet.crud.window.name");
		String escapedTitle = StringEscapeUtils.escapeJavaScript(realTitle);

		PortletResponse portletResponse = VaadinPortletService
				.getCurrentResponse().getPortletResponse();
		if (portletResponse instanceof RenderResponse) {
			((RenderResponse) portletResponse).setTitle(realTitle);
		} else {
			// document.querySelectorAll("#portlet_crudportlet_WAR_eaiadministration_INSTANCE_qeH6QK9czlb6 .portlet-title-text")[0].childNodes[0].nodeValue
			// = 'Releaseplaner - Applikationen'
			String ie8plusUpdate = "document.querySelectorAll('#portlet_"
					+ getPortletId() + " .portlet-title-text')[0]"
					+ ".childNodes[0].nodeValue = '" + escapedTitle + "'";
			String defaultUpdate = "document.getElementById('portlet_"
					+ getPortletId()
					+ "').getElementsByClassName('portlet-title-text')[0]"
					+ ".childNodes[0].nodeValue = '" + escapedTitle + "'";
			String all = "if (document.querySelectorAll) { " + ie8plusUpdate
					+ " } else { " + defaultUpdate + " };";
			JavaScript.getCurrent().execute(all);
		}
	}

	private void cachePortletTitle(String title) {
		PortletPreferences preferences = VaadinPortletService
				.getCurrentPortletRequest().getPreferences();
		String oldTitle = preferences.getValue(
				CrudVaadinPortlet.PORTLET_TITLE_PREF_KEY, null);
		if (oldTitle == null || !oldTitle.equals(title)) {
			try {
				preferences.setValue(CrudVaadinPortlet.PORTLET_TITLE_PREF_KEY,
						title);
				preferences.store();

			} catch (ReadOnlyException e) {
				LOG.error("Failed to update portlet title in preferences", e);
			} catch (ValidatorException e) {
				LOG.error("Failed to update portlet title in preferences", e);
			} catch (IOException e) {
				LOG.error("Failed to update portlet title in preferences", e);
			}
		}

	}

	private void applyBrowserLocale() {
		setLocale(Page.getCurrent().getWebBrowser().getLocale());
	}

	private void initializeEventBus() {
		// remove old references to domain handlers from the Event Bus
		eventBus.reset();

		eventBus.addHandler(ShowPopupEvent.class, this);
		eventBus.addHandler(ConfigurationUpdatedEvent.class,
				new ConfigurationUpdatedEventHandler() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onConfigurationUpdated(
							ConfigurationUpdatedEvent event) {
						handleConfigurationUpdatedEvent(event);
					}
				});
	}

	private void handleConfigurationUpdatedEvent(ConfigurationUpdatedEvent event) {
		cleanupViewPage();
		initializeEventBus();
		configChanged = true;

		if (!event.isConfigurable()) {
			PortletUtils.switchPortletMode(PortletMode.VIEW);
		}
	}

	private ComponentContainer initializeHelpPage() {

		DatasourceInfos datasourceInfos = new DatasourceInfos();
		DatasourceInfoView datasourceInfoView = new DatasourceInfoView(
				datasourceInfos.getContainer());
		this.datasourceInfo = new DatasourceInfoPresenter(datasourceInfoView,
				datasourceInfos);

		VerticalLayout help = new VerticalLayout();
		help.setSpacing(true);
		help.addComponent(new Link(Context
				.getMessage("portlet.crud.page.help.message"),
				new ExternalResource(settings.getHelpUrl())));
		help.addComponent(new Label(Context.getMessage(
				"portlet.crud.page.help.buildNumberLabel",
				settings.getBuildNumber())));
		help.addComponent(this.datasourceInfo.getView());

		return help;
	}

	private Config getConfiguration() {
		return configurationService.getPortletConfig(getPortletId(),
				getCommunityId());
	}

	public void addToView(Component component) {
		if (registeredComponents.add(component)) {
			logAddToView(component);
			viewPage.addComponent(component);
		}
	}

	public void removeAddedComponentsFromView() {
		for (Component component : registeredComponents) {
			logRemoveFromView(component);
			viewPage.removeComponent(component);
		}
	}

	private void logAddToView(Component component) {
		logViewChange(component, "Füge Komponente", " hinzu");
	}

	private void logRemoveFromView(Component component) {
		logViewChange(component, "Entferne Komponente", "");
	}

	private void logViewChange(Component component, String msg1, String msg2) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Application ["
					+ this.hashCode()
					+ "]: "
					+ msg1
					+ " ["
					+ component.getClass().getName()
					+ "@"
					+ Integer.toHexString(component.hashCode())
					+ "]"
					+ msg2
					+ (component.getCaption() != null ? " ("
							+ component.getCaption() + ")" : "")
					+ "; ViewPage: [" + viewPage + "].");
		}
	}

	/**
	 * Callback-Methode, die im Fall eines Commits des Konfigurationsformulars
	 * aufgerufen wird.
	 */
	public void refreshViews() {
		try {
			LOG.info("Refreshing views");
			initializing = true;

			cleanupViewPage();
			initializeEventBus();

			LOG.debug("Loading configuration");
			portletConfig = getConfiguration();
			status = getConfigStatus(portletConfig);

			if (status == ConfigStatus.CONFIGURED) {
				initializeModelAndViews(portletConfig);
				addServerProcessingInfo();
			} else {
				viewPage.addComponent(new UnconfiguredMessage());
			}
		} catch (BusinessException e) {
			viewPage.addComponent(new BusinessExceptionMessage(e));
		} catch (Exception e) {
			LOG.error("Error refreshing configuration", e);
			viewPage.addComponent(new BusinessExceptionMessage(
					"portlet.crud.error.internal"));
		} finally {
			initializing = false;
		}
	}

	private void addServerProcessingInfo() {
		if (settings.isDisplayRequestProcessingInfo()
				|| settings.isRequestLogEnabled()) {
			if (requestProcessingLabel == null) {
				RequestProcessingLogService requestProcessingLogService = Context
						.getBean(RequestProcessingLogService.class);
				requestProcessingLabel = new RequestProcessingLabel(
						requestProcessingLogService,
						settings.isDisplayRequestProcessingInfo());
				timingPortletListener = new TimingPortletListener(
						requestProcessingLabel);
				getPortletSession().addPortletListener(timingPortletListener);
			}
			viewPage.addComponent(requestProcessingLabel);
			viewPage.setComponentAlignment(requestProcessingLabel,
					Alignment.MIDDLE_RIGHT);
		}
	}

	private ConfigStatus getConfigStatus(Config portletConfig) {
		if (portletConfig == null) {
			return ConfigStatus.NO_CONFIG;
		} else {
			if (configurationService.isConfigured(portletConfig,
					VaadinPortletService.getCurrentPortletRequest()
							.getPreferences())) {
				return ConfigStatus.CONFIGURED;
			} else {
				return ConfigStatus.UNCONFIGURED;
			}
		}
	}

	private void initializeModelAndViews(Config portletConfig) {
		try {
			String portletId = getPortletId();

			LOG.debug("Building domain model");
			ModelBuilder modelBuilder = modelFactory.getBuilder(eventBus,
					portletConfig);
			portletDomain = modelBuilder.build();

			LOG.debug("Building scripting model");
			ScriptModelBuilder scriptModelBuilder = scriptModelFactory
					.getBuilder(eventBus, portletDomain,
							modelBuilder.getModelToConfigMapping());

			scriptModelBuilder.build();

			LOG.debug("Building GUI");
			portletGui = guiBuilder.build(portletDomain);

			viewPage.addComponent(portletGui.getView());

			provideBackButtonFunctionality(portletId);

			tryToSetPortletTitle(portletDomain.getTitle());
			cachePortletTitle(portletDomain.getTitle());

		} catch (ValidationException ve) {
			throw new BusinessException(ve.getCode(), ve.getArgs());
		}

		this.datasourceInfo.setPortletConfig(portletConfig.getPortletConfig());
	}

	private void provideBackButtonFunctionality(String portletId) {
		WebBrowser browser = getPage().getWebBrowser();
		if (browser != null && !browser.isIE()) {
			portletUriFragmentUtility = new PortletUriFragmentUtility(eventBus,
					portletDomain, portletId);
		} else {
			LOG.info("Browser not detected or is Internet Explorer. Disabling back button functionality");
			// ...weil sie zu einem unschönen Refresh des Browsers
			// führt
		}
	}

	private void cleanupViewPage() {
		dropConfiguration();
		destroyDomain();

		removeTimingPortletListener();

		removeAddedComponentsFromView();

		viewPage.removeAllComponents();

		addHiddenPortletId(viewPage);
	}

	private void removeTimingPortletListener() {
		if (timingPortletListener != null) {
			getPortletSession().removePortletListener(timingPortletListener);
			timingPortletListener = null;
		}

	}

	private void recreateEditPage() {
		editPage.removeAllComponents();
		configurationPresenter = presenterFactory
				.portletConfigurationPresenter();
		editPage.addComponent(configurationPresenter.getView());
	}

	/**
	 * Drops reference to PortletConfiguration.
	 */
	private void dropConfiguration() {
		this.portletConfig = null;
		this.status = ConfigStatus.UNKNOWN;
	}

	private void destroyDomain() {
		portletDomain = null;
		portletGui = null;
	}

	/**
	 * Erzeugt ein verstecktes HTML-Element mit der PortletPresenter-Window-ID
	 * für die Akzeptanztests.
	 * 
	 * @param container
	 */
	private void addHiddenPortletId(ComponentContainer container) {
		Label labelForPortletId = new Label(getPortletId());
		labelForPortletId.setStyleName("crudPortletId");
		labelForPortletId.setHeight(0, AbstractComponent.UNITS_PIXELS);

		container.addComponent(labelForPortletId);

		Label labelForCommunityId = new Label(String.valueOf(getCommunityId()));
		labelForCommunityId.setStyleName("crudPortletCommunityId");
		labelForCommunityId.setHeight(0, AbstractComponent.UNITS_PIXELS);

		container.addComponent(labelForCommunityId);
	}

	@Override
	public void detach() {
		getPortletSession().removePortletListener(this);
		removeTimingPortletListener();
		logLifecycleEvent(LifecycleEvent.CRUD2GO_UI_DETACH);
		super.detach();
	}

	private VaadinPortletSession getPortletSession() {
		// deprecated, but currently the only way
		VaadinPortletSession portletSession = (VaadinPortletSession) VaadinPortletSession
				.getCurrent();
		return portletSession;
	}

	/**
	 * Setzt den konfigurierten PortletPresenter-Title während des
	 * Render-Request. Erkennung von Reloads / Seitenwechseln.
	 * 
	 * @param ui
	 *            ist hier immer NULL. Da die PortletSession aber nur pro
	 *            Portletinstanz gilt kann <code>this</code> verwendet werden.
	 */
	@Override
	public void handleRenderRequest(final RenderRequest request,
			RenderResponse response, UI ui) {
		LOG.debug("Handling render request...");
		if (getSession() != null) {
			if (portletDomain != null) {
				if (portletDomain.getTitle() != null) {
					response.setTitle(portletDomain.getTitle());
				}
				if (!configChanged
						&& request.getPortletMode() == PortletMode.VIEW) {
					accessSynchronously(new Runnable() {
						@Override
						public void run() {
							portletDomain.handleReload();
						}
					});
				}
			}

			if (portletUriFragmentUtility != null) {
				// portletUriFragmentUtility.setInitialFragment();
			}
			accessSynchronously(new Runnable() {
				@Override
				public void run() {
					handleViewChange(request);
				}
			});

			configChanged = false;

		} else {
			// TODO check production logs and then remove query
			// should not happen any more as listener is removed on detach
			LOG.warn("Session is NULL during render request");
		}
	}

	@Override
	public void handleActionRequest(ActionRequest request,
			ActionResponse response, UI ui) {
		// keine Aktion
		LOG.debug("Handling action request...");
	}

	@Override
	public void handleEventRequest(EventRequest request,
			EventResponse response, UI ui) {
		// keine Aktion
	}

	/**
	 * Hier wird auf den Wechsel des {@link PortletMode} reagiert, der durch den
	 * Container an das PortletPresenter gemeldet wird.
	 */
	@Override
	public void handleResourceRequest(ResourceRequest request,
			ResourceResponse response, UI ui) {

		LOG.debug("Handling resource request...");
		handleViewChange(request);
	}

	private void handleViewChange(PortletRequest request) {
		UI oldUI = UI.getCurrent();
		try {
			UI.setCurrent(this);
			// if (ui == null) {
			// return;
			// }
			if (request.getPortletMode() == PortletMode.VIEW) {
				if (getContent() != viewPage && status == ConfigStatus.UNKNOWN) {
					refreshViews();
				}
				setContent(viewPage);
			} else if (request.getPortletMode() == PortletMode.EDIT) {
				LOG.info("Request on EDIT page!");
				if (getContent() != editPage) {
					configurationPresenter.refresh(portletConfig);
				}
				setContent(editPage);
			} else if (request.getPortletMode() == PortletMode.HELP) {
				this.datasourceInfo.refresh();
				setContent(helpPage);
			}

		} finally {
			UI.setCurrent(oldUI);
		}
	}

	@Override
	public void showPopup(ShowPopupEvent event) {
		Popup popup = new Popup(event.getSource().getTitle(), event.getSource()
				.getBody(), event.getSource().getContentType());

		addWindow(popup);
	}

	ComponentContainer getViewContent() {
		return viewPage;
	}

	ComponentContainer getEditContent() {
		return editPage;
	}

	ComponentContainer getHelpContent() {
		return helpPage;
	}

	void setConfigurationPresenter(
			PortletConfigurationPresenter configurationPresenter) {
		this.configurationPresenter = configurationPresenter;
	}

	Portlet getPortletDomain() {
		return portletDomain;
	}

	void setPortletDomain(Portlet portletDomain) {
		this.portletDomain = portletDomain;
	}

	public static CrudUI getCurrent() {
		return (CrudUI) UI.getCurrent();
	}

	public boolean isInitializing() {
		return initializing;
	}

	public Config getPortletConfig() {
		return portletConfig;
	}

}

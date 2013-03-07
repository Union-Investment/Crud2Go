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

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.util.HashSet;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfos;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.CrudApplication;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DatasourceInfoPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletConfigurationPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DatasourceInfoView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.BusinessExceptionMessage;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.Popup;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.PortletUriFragmentUtility;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.SpringPortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.support.UnconfiguredMessage;
import de.unioninvestment.eai.portal.support.vaadin.validation.ValidationException;

/**
 * CRUD PortletPresenter Applikationsklasse. Enthaelt das Management der
 * verschiedenen PortletPresenter-Modus.
 * 
 * 
 * @author carsten.mjartan
 * 
 */
@Configurable(preConstruction = true, dependencyCheck = true)
public class CrudPortletApplication extends SpringPortletApplication implements
		PortletListener, ShowPopupEventHandler, CrudApplication {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_ADMIN = "portlet-crud-adm";

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(CrudPortletApplication.class);

	private Portlet portletDomain;
	private PortletPresenter portletGui;

	private DatasourceInfoPresenter datasourceInfo;

	private ComponentContainer viewPage = new VerticalLayout();
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

	private Window mainWindow;

	private Set<Component> registeredComponents = new HashSet<Component>();

	public enum ConfigStatus {
		UNKNOWN, NO_CONFIG, UNCONFIGURED, CONFIGURED
	}

	ConfigStatus status = ConfigStatus.UNKNOWN;
	boolean initializing = true;
	boolean firstLoad = true;

	Config portletConfig;

	/**
	 * Initialisierung des PortletPresenter.
	 * 
	 */
	@Override
	public void doInit() {
		helpPage = initializeHelpPage();
		mainWindow = new Window(getMessage("portlet.crud.window.name"));
		mainWindow.setContent(viewPage);
		setMainWindow(mainWindow);

		refreshViews();
		registerAsPortletListener();
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
						refreshViews();
						if (status == ConfigStatus.CONFIGURED) {
							firstLoad = true;
							PortletUtils.switchPortletMode(getMainWindow()
									.getApplication(), PortletMode.VIEW);
						} else {
							configurationPresenter.refresh(status,
									portletConfig,
									portletDomain);
						}
					}
				});
	}

	private ComponentContainer initializeHelpPage() {

		DatasourceInfos datasourceInfos = new DatasourceInfos();
		DatasourceInfoView datasourceInfoView = new DatasourceInfoView(
				datasourceInfos.getContainer());
		this.datasourceInfo = new DatasourceInfoPresenter(datasourceInfoView,
				datasourceInfos);

		VerticalLayout help = new VerticalLayout();
		help.setSpacing(true);
		help.addComponent(new Link(
				getMessage("portlet.crud.page.help.message"),
				new ExternalResource(settings.getHelpUrl())));
		help.addComponent(new Label(getMessage(
				"portlet.crud.page.help.buildNumberLabel",
				settings.getBuildNumber())));
		help.addComponent(this.datasourceInfo.getView());

		return help;
	}

	@Override
	public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
		Throwable throwable = event.getThrowable();
		LOG.error("Internal error", throwable);
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		String message = throwable.getMessage();
		if (message != null) {
			getMainWindow().showNotification(message,
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private Config getConfiguration() {
		String portletId = (String) getCurrentRequest().getAttribute(
				WebKeys.PORTLET_ID);
		ThemeDisplay themeDisplay = (ThemeDisplay) getCurrentRequest()
				.getAttribute(WebKeys.THEME_DISPLAY);
		long communityId = themeDisplay.getScopeGroupId();
		return configurationService.getPortletConfig(portletId, communityId);
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
			initializing = true;

			cleanupViews();
			initializeEventBus();

			LOG.debug("Loading configuration");
			portletConfig = getConfiguration();
			status = getConfigStatus(portletConfig);

			if (status == ConfigStatus.CONFIGURED) {
				initializeModelAndViews(portletConfig);
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

	private ConfigStatus getConfigStatus(Config portletConfig) {
		if (portletConfig == null) {
			return ConfigStatus.NO_CONFIG;
		} else {
			if (configurationService.isConfigured(portletConfig,
					getCurrentRequest().getPreferences())) {
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

			scriptModelBuilder.setApplication(this);
			scriptModelBuilder.build();

			LOG.debug("Building GUI");
			portletGui = guiBuilder.build(portletDomain);

			viewPage.addComponent(portletGui.getView());

			provideBackButtonFunctionality(portletId);

		} catch (ValidationException ve) {
			throw new BusinessException(ve.getCode(), ve.getArgs());
		}

		this.datasourceInfo.setPortletConfig(portletConfig.getPortletConfig());
	}

	private void provideBackButtonFunctionality(String portletId) {
		WebBrowser browser = (WebBrowser) mainWindow.getTerminal();
		if (browser != null && !browser.isIE()) {
			portletUriFragmentUtility = new PortletUriFragmentUtility(
					portletDomain, portletId);
			viewPage.addComponent(portletUriFragmentUtility);
		} else {
			LOG.info("Browser not detected or is Internet Explorer. Disabling back button functionality");
			// ...weil sie zu einem unschönen Refresh des Browsers
			// führt
		}
	}

	private void cleanupViews() {
		dropConfiguration();
		destroyDomain();

		removeAddedComponentsFromView();

		viewPage.removeAllComponents();

		editPage.removeAllComponents();
		configurationPresenter = presenterFactory
				.portletConfigurationPresenter();
		editPage.addComponent(configurationPresenter.getView());

		addHiddenPortletId(viewPage);
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

	private void registerAsPortletListener() {
		if (getContext() instanceof PortletApplicationContext2) {
			((PortletApplicationContext2) getContext()).addPortletListener(
					this, this);
		} else {
			getMainWindow().showNotification(
					getMessage("portlet.crud.error.noPortlet2Container"),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	/**
	 * Setzt den konfigurierten PortletPresenter-Title während des
	 * Render-Request.
	 * 
	 * {@inheritDoc}
	 */
	public void handleRenderRequest(RenderRequest request,
			RenderResponse response, Window window) {
		if (portletDomain != null) {
			if (portletDomain.getTitle() != null) {
				response.setTitle(portletDomain.getTitle());
			}
			if (firstLoad) {
				firstLoad = false;
			} else if (request.getPortletMode() == PortletMode.VIEW) {
				portletDomain.handleReload();
			}
		}
		if (portletUriFragmentUtility != null) {
			portletUriFragmentUtility.setInitialFragment(getMainWindow());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleActionRequest(ActionRequest request,
			ActionResponse response, Window window) {
		// keine Aktion
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleEventRequest(EventRequest request,
			EventResponse response, Window window) {
		// keine Aktion
	}

	/**
	 * Hier wird auf den Wechsel des {@link PortletMode} reagiert, der durch den
	 * Container an das PortletPresenter gemeldet wird.
	 * 
	 * {@inheritDoc}
	 */
	public void handleResourceRequest(ResourceRequest request,
			ResourceResponse response, Window window) {

		if (window == null) {
			return;
		}
		if (request.getPortletMode() == PortletMode.VIEW) {
			window.setContent(viewPage);
		} else if (request.getPortletMode() == PortletMode.EDIT) {
			if (window.getContent() != editPage) {
				configurationPresenter.refresh(status, portletConfig,
						portletDomain);
			}
			window.setContent(editPage);
		} else if (request.getPortletMode() == PortletMode.HELP) {
			this.datasourceInfo.refresh();
			window.setContent(helpPage);
		}
	}

	@Override
	public void showPopup(ShowPopupEvent event) {
		Popup popup = new Popup(event.getSource().getTitle(), event.getSource()
				.getBody(), event.getSource().getContentType());

		getMainWindow().addWindow(popup);
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

	public static CrudPortletApplication getCurrentApplication() {
		return (CrudPortletApplication) PortletApplication
				.getCurrentApplication();
	}

	public boolean isInitializing() {
		return initializing;
	}
}

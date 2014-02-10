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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.AbstractActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.BinaryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DialogConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ExportTypeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormSelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.OrderConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PanelConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectDisplayType;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TextAreaConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TriggerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TriggersConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.JmxDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Init;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.util.Util;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory;

/**
 * Erstellt und initialisiert anhand einer gegebenen
 * PortletPresenter-Konfiguration die entsprechenden Domain-Objekte.
 * 
 * @author carsten.mjartan
 */
public class ModelBuilder {

	private static final Logger LOG = LoggerFactory
			.getLogger(ModelBuilder.class);

	private static final String MESSAGE_KEY_EDITABLE_TABLE_WITHOUT_PRIMARY_KEY = "portlet.crud.unsupported.tableconfig.editable.without.primary.key";

	private static final String MESSAGE_KEY_EXPORTABLE_TABLE_WITHOUT_PRIMARY_KEY = "portlet.crud.unsupported.tableconfig.export.without.primary.key";

	private final Config config;

	private final ResetFormAction resetFormAction;

	private final FieldValidatorFactory fieldValidatorFactory;

	private final int defaultSelectWidth;

	private final ModelFactory factory;
	private final UserFactory userFactory;

	private Map<Object, Object> mappings = new HashMap<Object, Object>();

	private Portlet portlet;

	private CurrentUser currentUser;

	private List<Form> forms = new ArrayList<Form>();

	private final EventBus eventBus;

	private boolean directEditDefault;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param eventBus
	 *            der Event-Bus
	 * @param factory
	 *            Model-Factory
	 * @param resetFormAction
	 *            Resetbutton
	 * @param fieldValidatorFactory
	 *            FieldValidator-Factory
	 * @param defaultSelectWidth
	 *            Breite der Selectboxen
	 * @param config
	 *            Portlet Konfiguration
	 * @param separateEditMode
	 *            default setting for tables
	 */
	public ModelBuilder(EventBus eventBus, ModelFactory factory,
			UserFactory userFactory, ResetFormAction resetFormAction,
			FieldValidatorFactory fieldValidatorFactory,
			int defaultSelectWidth, Config config, boolean directEditDefault) {
		this.eventBus = eventBus;
		this.factory = factory;
		this.userFactory = userFactory;
		this.resetFormAction = resetFormAction;
		this.fieldValidatorFactory = fieldValidatorFactory;
		this.defaultSelectWidth = defaultSelectWidth;
		this.config = config;
		this.directEditDefault = directEditDefault;
	}

	/**
	 * Die PortletPresenter-Konfiguration.
	 * 
	 * @return eine Portlet-Instanz
	 */
	public Portlet build() {

		PortletContext context = new PortletContext();

		portlet = new Portlet(eventBus, config.getPortletConfig(), context);
		mappings.put(portlet, config);

		buildRoles();
		buildAuthenticationRealms();

		currentUser = userFactory.getCurrentUser(portlet);
		context.setCurrentUser(currentUser);

		if (config.getPortletConfig().getPage() != null) {
			Page page = (Page) buildPanel(config.getPortletConfig().getPage());
			portlet.setPage(page);
		} else {
			Tabs tabs = (Tabs) buildComponent(config.getPortletConfig()
					.getTabs());
			portlet.setTabs(tabs);
		}

		buildDialogs();

		return portlet;
	}

	private void buildAuthenticationRealms() {
		if (config.getPortletConfig().getAuthentication() != null) {
			for (AuthenticationRealmConfig realmConfig : config
					.getPortletConfig().getAuthentication().getRealm()) {

				Realm realm = factory.getAuthenticationRealm(realmConfig);
				portlet.addRealm(realmConfig.getName(), realm);
			}
		}
	}

	private void buildRoles() {
		if (config.getPortletConfig().getRoles() != null) {
			LiferayUI application = LiferayUI.getCurrent();
			String portletId = application.getPortletId();
			long communityId = application.getCommunityId();
			for (RoleConfig roleConfig : config.getPortletConfig().getRoles()
					.getRole()) {
				if (roleConfig.getPortalRole() == null) {
					String resourceId = PortletRole.createRoleResourceId(
							portletId, communityId, roleConfig.getName());
					portlet.addRole(new PortletRole(roleConfig.getName(),
							config.getRoleResourceIDs().get(resourceId)));
				} else {
					portlet.addRole(new PortalRole(roleConfig.getName(),
							roleConfig.getPortalRole()));
				}
			}
		}
	}

	/**
	 * Erzeugt alle Dialog-Modell-Elemente.
	 */
	private void buildDialogs() {
		List<DialogConfig> dialogConfigList = config.getPortletConfig()
				.getDialog();
		if (dialogConfigList != null) {
			for (DialogConfig dialogConfig : dialogConfigList) {
				Panel panel = createPanelInstance(dialogConfig);
				buildComponentsInPanel(dialogConfig, panel);
			}
		}
	}

	private Panel buildPanel(PanelConfig panelConfig) {
		Panel panel = createPanelInstance(panelConfig);
		buildComponentsInPanel(panelConfig, panel);
		return panel;
	}

	private void buildComponentsInPanel(PanelConfig panelConfig, Panel panel) {
		for (ComponentConfig elementConfig : panelConfig.getElements()) {
			Component component = buildComponent(elementConfig);
			if (component != null) {
				panel.addComponent(component);
			}
		}
	}

	private Component buildComponent(ComponentConfig componentConfig) {
		if (currentUser.hasPermission(componentConfig,
				Component.Permission.BUILD, true)) {
			if (componentConfig instanceof FormConfig) {
				return buildForm((FormConfig) componentConfig);
			} else if (componentConfig instanceof TableConfig) {
				return buildTable((TableConfig) componentConfig);
			} else if (componentConfig instanceof TextAreaConfig) {
				return buildTextArea((TextAreaConfig) componentConfig);
			} else if (componentConfig instanceof TabsConfig) {
				return buildTabs((TabsConfig) componentConfig);
			} else if (componentConfig instanceof ScriptComponentConfig) {
				return buildCustomComponent((ScriptComponentConfig) componentConfig);
			} else if (componentConfig instanceof RegionConfig) {
				return buildPanel((RegionConfig) componentConfig);
			} else {
				throw new IllegalArgumentException("Component of type '"
						+ componentConfig.getClass().getName()
						+ "' not supported!");
			}
		}
		return null;
	}

	private Component buildTextArea(TextAreaConfig componentConfig) {
		if (currentUser.hasPermission(componentConfig,
				TextArea.Permission.BUILD, true)) {

			boolean editable = currentUser.hasPermission(componentConfig,
					TextArea.Permission.EDIT, componentConfig.isEditable());
			TextArea textarea = new TextArea(componentConfig, editable);
			mappings.put(textarea, componentConfig);
			return textarea;
		} else {
			return null;
		}
	}

	private Component buildCustomComponent(ScriptComponentConfig componentConfig) {
		if (currentUser.hasPermission(componentConfig,
				CustomComponent.Permission.BUILD, true)
				&& portlet.allowsDisplayGeneratedContent()) {

			CustomComponent scriptComponent = new CustomComponent(
					componentConfig);
			String componentId = componentConfig.getId();
			if (StringUtils.isNotEmpty(componentId)) {
				portlet.addElementById(componentId, scriptComponent);
			}
			mappings.put(scriptComponent, componentConfig);
			return scriptComponent;

		} else {
			return null;
		}
	}

	private Component buildTabs(TabsConfig config) {
		Tabs tabs = new Tabs(config);
		mappings.put(tabs, config);
		for (TabConfig tabConfig : config.getTab()) {
			if (currentUser
					.hasPermission(tabConfig, Tab.Permission.BUILD, true)) {
				Tab tab = (Tab) buildPanel(tabConfig);
				tabs.addElement(tab);
			}
		}
		return tabs;
	}

	private Panel createPanelInstance(PanelConfig panelConfig) {
		if (panelConfig instanceof PageConfig) {
			return new Page((PageConfig) panelConfig);
		} else if (panelConfig instanceof TabConfig) {
			TabConfig tabConfig = (TabConfig) panelConfig;
			Tab tab = new Tab(tabConfig);
			mappings.put(tab, tabConfig);
			portlet.addElementById(tabConfig.getId(), tab);
			return tab;
		} else if (panelConfig instanceof DialogConfig) {
			DialogConfig dialogConfig = (DialogConfig) panelConfig;
			Dialog dialog = new Dialog(dialogConfig);
			mappings.put(dialog, dialogConfig);
			portlet.addElementById(dialogConfig.getId(), dialog);
			return dialog;
		} else if (panelConfig instanceof RegionConfig) {
			RegionConfig regionConfig = (RegionConfig) panelConfig;
			Region region = new Region(regionConfig);
			mappings.put(region, regionConfig);
			portlet.addElementById(regionConfig.getId(), region);
			return region;
		} else {
			throw new IllegalArgumentException("Panel of type '"
					+ panelConfig.getClass().getName() + "' not supported!");
		}
	}

	private Form buildForm(FormConfig formConfig) {
		FormFields fields = buildFormFields(formConfig);
		FormActions actions = buildFormActions(formConfig);
		Form form = new Form(formConfig, fields, actions);
		forms.add(form);
		return form;
	}

	private FormActions buildFormActions(FormConfig formConfig) {
		List<FormAction> actionList = new ArrayList<FormAction>();
		for (FormActionConfig config : formConfig.getAction()) {

			if (currentUser.hasPermission(config, Form.Permission.BUILD, true)) {
				Triggers triggers = buildTriggers(config);

				FormAction formAction = new FormAction(portlet, config,
						buildActionHandler(config), triggers);

				actionList.add(formAction);
				mappings.put(formAction, config);
				portlet.addElementById(config.getId(), formAction);
			}
		}
		return new FormActions(actionList);
	}

	private Triggers buildTriggers(AbstractActionConfig config) {
		Triggers triggers = new Triggers();
		TriggersConfig triggersConfig = config.getTriggers();
		if (triggersConfig != null) {
			for (TriggerConfig tConfig : triggersConfig.getTrigger()) {
				if (tConfig.getAction() instanceof FormActionConfig) {
					FormActionConfig formAction = (FormActionConfig) tConfig
							.getAction();
					triggers.addTrigger(new Trigger(formAction.getId()));

				} else {
					throw new IllegalArgumentException(
							"Referenzierte Action ist kein Formaction.");
				}
			}
		}
		return triggers;
	}

	private FormFields buildFormFields(FormConfig formConfig) {
		FormField[] fieldList = new FormField[formConfig.getField().size()];
		int i = 0;
		for (FormFieldConfig config : formConfig.getField()) {
			FormField formField = null;
			if (config.getSelect() != null) {
				OptionList optionList = buildOptionList(config.getSelect(),
						null);

				FormSelectConfig formSelectConfig = (FormSelectConfig) config
						.getSelect();

				if (formSelectConfig.isMultiSelect()) {
					formField = new MultiOptionListFormField(config, optionList);

				} else {
					formField = new OptionListFormField(config, optionList);
				}

			} else if (config.getCheckbox() != null) {
				formField = new CheckBoxFormField(config);

			} else if (config.getDate() != null) {
				formField = new DateFormField(config);

			} else {
				formField = new FormField(config);
			}

			List<FieldValidator> validators = fieldValidatorFactory
					.createValidators(config.getValidate(),
							config.getValidationMessage());
			formField.setValidators(validators);

			mappings.put(formField, config);

			fieldList[i++] = formField;
		}
		return new FormFields(fieldList);
	}

	private OptionList buildOptionList(SelectConfig config, String ds) {
		if (config.getQuery() != null) {
			String datasource;
			if (StringUtils.isEmpty(config.getQuery().getDatasource())) {
				datasource = ds;
			} else {
				datasource = config.getQuery().getDatasource();
			}
			QueryOptionList queryOptionList = factory.getQueryOptionList(
					eventBus, config, datasource);

			String id = config.getId();
			if (StringUtils.isNotEmpty(id)) {
				portlet.addElementById(id, queryOptionList);
			}

			return queryOptionList;
		} else if (config.getDynamic() == null) {
			return new StaticOptionList(config);
		}
		return null;
	}

	private ActionHandler buildActionHandler(FormActionConfig actionConfig) {
		if (actionConfig.getSearch() != null) {
			return new SearchFormAction(actionConfig, portlet);
		} else if (actionConfig.getReset() != null) {
			return resetFormAction;
		} else {
			return null;
		}
	}

	private Table buildTable(TableConfig tableConfig) {
		if (currentUser
				.hasPermission(tableConfig, Table.Permission.BUILD, true)) {
			verifyPrimaryKeyColumnsArePresentIfNeccessary(tableConfig);
			String dataSource = getDataSource(tableConfig);
			TableColumns tableColumns = buildColumns(tableConfig.getColumns(),
					dataSource);

			boolean editable = currentUser.hasPermission(tableConfig,
					Table.Permission.EDIT, tableConfig.isEditable());

			boolean directEdit = calculateDirectEditFlag(directEditDefault,
					tableConfig.isDirectEdit(), tableConfig.getOnDoubleClick());

			Table table = new Table(tableConfig, tableColumns, editable,
					directEdit);
			mappings.put(table, tableConfig);
			portlet.addElementById(tableConfig.getId(), table);

			table.setActions(buildTableActions(tableConfig.getAction(), table));
			DataContainer container = buildContainer(tableConfig, tableColumns);

			table.setContainer(container);
			return table;
		}

		return null;
	}

	static boolean calculateDirectEditFlag(boolean directEditDefault,
			Boolean directEditOnTable, GroovyScript onDoubleClick) {
		boolean directEdit = directEditOnTable != null ? directEditOnTable
				: directEditDefault;
		if (directEdit && onDoubleClick != null) {
			if (directEditOnTable != null) {
				throw new IllegalStateException(
						"Direct editing not possible in combination with onDoubleClick event");
			} else {
				LOG.warn("Direct editing disabled in favor of onDoubleClick event handling");
				return false;
			}
		}
		return directEdit;
	}

	private void verifyPrimaryKeyColumnsArePresentIfNeccessary(
			TableConfig tableConfig) {
		// Beim Query-Backend ist es nicht zulässig, eine editierbare oder
		// exportierbare Tabelle ohne Primary-Key-Columns zu definieren.
		// Beim Table-Backend werden column-Tags ohnehin offiziell nicht
		// unterstützt.
		if (tableConfig.getDatabaseQuery() != null) {
			if (!isWriteProtected(tableConfig)) {
				verifyPrimaryKeyColumnsArePresent(tableConfig,
						MESSAGE_KEY_EDITABLE_TABLE_WITHOUT_PRIMARY_KEY,
						"editierbar");
			} else if (tableConfig.getExport() != null) {
				verifyPrimaryKeyColumnsArePresent(tableConfig,
						MESSAGE_KEY_EXPORTABLE_TABLE_WITHOUT_PRIMARY_KEY,
						"exportierbar");
			}
		}
	}

	/**
	 * Überprüft, ob die Tabelle explizit oder implizit schreibgeschützt ist.
	 * 
	 * @param tableConfig
	 *            die TableConfig
	 * @return {@code true}, genau dann wenn die Tabelle als editable=false
	 *         markiert ist oder sie ein Query-Backend hat, aber keinen Insert-,
	 *         Update-, Delete-Block
	 */
	private boolean isWriteProtected(TableConfig tableConfig) {
		if (!tableConfig.isEditable()) {
			return true;
		} else if (tableConfig.getDatabaseQuery() != null) {
			DatabaseQueryConfig query = tableConfig.getDatabaseQuery();
			return query.getInsert() == null && query.getUpdate() == null
					&& query.getDelete() == null;
		} else {
			return false;
		}
	}

	private void verifyPrimaryKeyColumnsArePresent(TableConfig tableConfig,
			String messageKey, String reasonForLog) {
		if (!hasPrimaryKeyColumn(tableConfig)) {
			LOG.warn(
					messageKey
							+ ": Das Element 'table' [ID: {}] ist fehlerhaft konfiguriert. Die Tabelle ist {}, dennoch ist keines der 'column'-Elemente als Primary Key markiert.",
					tableConfig.getId(), reasonForLog);
			throw new BusinessException(messageKey);
		}
	}

	private boolean hasPrimaryKeyColumn(TableConfig tableConfig) {
		ColumnsConfig columns = tableConfig.getColumns();
		if (columns != null) {
			for (ColumnConfig column : columns.getColumn()) {
				if (column.isPrimaryKey()) {
					return true;
				}
			}
		}
		return false;
	}

	private List<TableAction> buildTableActions(
			List<TableActionConfig> tableActionConfigs, Table table) {
		List<TableAction> tableActions = new ArrayList<TableAction>();

		if (tableActionConfigs != null) {
			for (TableActionConfig tac : tableActionConfigs) {

				if (currentUser.hasPermission(tac,
						TableAction.Permission.BUILD, true)) {
					Triggers triggers = buildTriggers(tac);

					TableAction tableAction = new TableAction(portlet, tac,
							table, triggers);
					mappings.put(tableAction, tac);
					portlet.addElementById(tac.getId(), tableAction);

					tableActions.add(tableAction);
				}
			}
		}
		return tableActions;
	}

	private String getDataSource(TableConfig tableConfig) {
		String dataSource = null;
		if (tableConfig.getDatabaseTable() != null) {
			dataSource = tableConfig.getDatabaseTable().getDatasource();
		} else if (tableConfig.getDatabaseQuery() != null) {
			dataSource = tableConfig.getDatabaseQuery().getDatasource();
		}
		return dataSource;
	}

	private TableColumns buildColumns(ColumnsConfig columnsConfig,
			String dataSource) {

		List<TableColumn> result = new ArrayList<TableColumn>();

		if (columnsConfig == null || columnsConfig.getColumn() == null) {
			return null;
		}

		for (ColumnConfig c : columnsConfig.getColumn()) {
			List<FieldValidator> validators = fieldValidatorFactory
					.createValidators(c.getValidate(), c.getValidationMessage());

			Integer width = c.getWidth();

			Hidden hs = Hidden.valueOf(c.getHidden().toString());
			if (!currentUser.hasPermission(c, TableColumn.Permission.DISPLAY,
					true)) {
				hs = Hidden.TRUE;
			}

			// ColumnConfig.getEditable() kann entweder ein closure oder einfach
			// true/false sein. Boolesche Werte werden hier behandelt, Closures
			// im ScriptModelBuilder.
			boolean isEditableDefault = false;
			GroovyScript editableClosure = c.getEditable();
			if (Util.isPlainBoolean(editableClosure)) {
				isEditableDefault = Boolean
						.valueOf(editableClosure.getSource());
			}
			boolean isEditable = currentUser.hasPermission(c,
					TableColumn.Permission.EDIT, isEditableDefault);

			FileMetadata fileMetadata = buildFileMetadata(c.getBinary());

			Class<?> generatedType = classOfGeneratedType(c.getGeneratedType());

			// Build

			Init<?> builder = null;

			if (c.getSelect() != null) {
				OptionList optionList = null;
				if (c.getSelect().getDynamic() == null) {
					try {
						optionList = buildOptionList(c.getSelect(), dataSource);
						if (width == null) {
							width = defaultSelectWidth;
						}
					} catch (Exception e) {
						LOG.warn(
								"Fehler beim erzeugen einer Auswahl für die Spalte: "
										+ c.getName(), e);
					}
				}

				SelectionTableColumn.Builder selectionBuilder = new SelectionTableColumn.Builder();
				selectionBuilder //
						.displayType(c.getSelect().getDisplay()) //
						.separator(c.getSelect().getSeparator()) //
						.multiselect(
								c.getSelect().getDisplay() == SelectDisplayType.TOKENS) //
						.optionList(optionList);
				builder = selectionBuilder;
			} else if (c.getCheckbox() != null) {
				CheckBoxTableColumn.Builder checkboxBuilder = new CheckBoxTableColumn.Builder();
				checkboxBuilder //
						.checkedValue(c.getCheckbox().getCheckedValue()) //
						.uncheckedValue(c.getCheckbox().getUncheckedValue());
				builder = checkboxBuilder;
			} else if (c.getDate() != null) {
				DateTableColumn.Builder dateBuilder = new DateTableColumn.Builder();
				dateBuilder.dateDisplayType(c.getDate().getDisplay());
				builder = dateBuilder;
			} else {
				builder = new TableColumn.Builder();
			}

			TableColumn tableColumn = builder //
					.name(c.getName()) //
					.title(c.getTitle()) //
					.longTitle(c.getLongtitle()) //
					.hiddenStatus(hs) //
					.editableDefault(isEditable) //
					.primaryKey(c.isPrimaryKey()) //
					.multiline(c.isMultiline()) //
					.rows(c.getRows()) //
					.width(width) //
					.inputPrompt(c.getInputPrompt()) //
					.validators(validators) //
					.displayFormat(c.getDisplayFormat()) //
					.excelFormat(c.getExcelFormat()) //
					.fileMetadata(fileMetadata) //
					.generatedType(generatedType) //
					.build();

			mappings.put(tableColumn, c);

			result.add(tableColumn);
		}
		return new TableColumns(result);
	}

	private Class<?> classOfGeneratedType(ExportTypeConfig generatedType) {
		try {
			if (generatedType == null) {
				return null;
			} else {
				return Class.forName(generatedType.value());
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Type '" + generatedType
					+ "' is not instantiable");
		}
	}

	private FileMetadata buildFileMetadata(BinaryConfig binary) {
		if (binary != null) {
			return new FileMetadata(binary.getFilename(), binary.getMineType(),
					binary.getDownloadCaption(), binary.getUploadCaption(),
					binary.getMaxFileSize());
		}
		return null;
	}

	private DataContainer buildContainer(TableConfig tableConfig,
			TableColumns columns) {
		DataContainer container;
		Map<String, String> formatPattern;
		if (columns != null) {
			formatPattern = columns.getFormatPattern();
		} else {
			formatPattern = new HashMap<String, String>();
		}
		if (tableConfig.getDatabaseQuery() != null) {
			container = buildQueryContainer(tableConfig.getDatabaseQuery(),
					columns.getPrimaryKeyNames(), formatPattern);
			mappings.put(container, tableConfig.getDatabaseQuery());
		} else if (tableConfig.getDatabaseTable() != null) {
			container = buildTableContainer(tableConfig.getDatabaseTable(),
					formatPattern);
			mappings.put(container, tableConfig.getDatabaseTable());
		} else if (tableConfig.getScriptContainer() != null) {
			container = buildScriptContainer(tableConfig.getScriptContainer(),
					formatPattern);
			mappings.put(container, tableConfig.getScriptContainer());
		} else if (tableConfig.getRestContainer() != null) {
			container = buildReSTContainer(tableConfig.getRestContainer(),
					formatPattern);
			mappings.put(container, tableConfig.getRestContainer());
		} else if (tableConfig.getJmxContainer() != null) {
			container = buildJmxContainer(tableConfig.getJmxContainer(),
					formatPattern);
			mappings.put(container, tableConfig.getJmxContainer());
		} else {
			throw new BusinessException("portlet.crud.unsupported.tableconfig");
		}

		return container;
	}

	private DataContainer buildReSTContainer(ReSTContainerConfig restContainer,
			Map<String, String> formatPattern) {

		List<ContainerOrder> defaultOrder = getDefaultOrder(restContainer);

		ReSTContainer container = factory
				.getReSTContainer(eventBus, formatPattern, defaultOrder,
						extractFilterPolicy(restContainer));

		// delegate is unset, will be set in ScriptModelBuilder later

		return container;
	}

	private DataContainer buildJmxContainer(JmxContainerConfig config,
			Map<String, String> formatPattern) {

		List<ContainerOrder> defaultOrder = getDefaultOrder(config);

		JMXContainer jmxContainer = factory.getJmxContainer(eventBus,
				formatPattern, defaultOrder, extractFilterPolicy(config));

		JmxDelegate jmxDelegate = new JmxDelegate(config, currentUser);
		jmxContainer.setDelegate(jmxDelegate);

		return jmxContainer;
	}

	private FilterPolicy extractFilterPolicy(ContainerConfig jmxContainer) {
		FilterPolicy filterPolicy = null;
		switch (jmxContainer.getFilterPolicy()) {
		case ALL:
			filterPolicy = FilterPolicy.ALL;
			break;
		case NOTHING:
			filterPolicy = FilterPolicy.NOTHING;
			break;
		case NOTHING_AT_ALL:
			filterPolicy = FilterPolicy.NOTHING_AT_ALL;
			break;
		}
		return filterPolicy;
	}

	private DataContainer buildScriptContainer(
			ScriptContainerConfig scriptContainer,
			Map<String, String> formatPattern) {

		List<ContainerOrder> defaultOrder = getDefaultOrder(scriptContainer);

		return factory.getGenericDataContainer(eventBus, formatPattern,
				defaultOrder, extractFilterPolicy(scriptContainer));
	}

	private DataContainer buildQueryContainer(
			DatabaseQueryConfig databaseQuery, List<String> primaryKeys,
			Map<String, String> displayPattern) {
		boolean insertable = currentUser
				.hasPermission(databaseQuery, DataContainer.Permission.INSERT,
						databaseQuery.getInsert() != null
								&& databaseQuery.getInsert().getStatement()
										.getSource() != null);
		boolean updateable = currentUser
				.hasPermission(databaseQuery, DataContainer.Permission.UPDATE,
						databaseQuery.getUpdate() != null
								&& databaseQuery.getUpdate().getStatement()
										.getSource() != null);
		boolean deleteable = currentUser
				.hasPermission(databaseQuery, DataContainer.Permission.DELETE,
						databaseQuery.getDelete() != null
								&& databaseQuery.getDelete().getStatement()
										.getSource() != null);
		List<ContainerOrder> orderBys = getDefaultOrder(databaseQuery);
		return factory.getDatabaseQueryContainer(eventBus,
				databaseQuery.getDatasource(), databaseQuery.getQuery(),
				insertable, updateable, deleteable, primaryKeys,
				currentUser.getName(), displayPattern, orderBys,
				extractFilterPolicy(databaseQuery),
				databaseQuery.getPagelength(),
				databaseQuery.getExportPagelength(),
				databaseQuery.getSizeValid(),
				databaseQuery.isOrderByPrimarykeys());
	}

	private DataContainer buildTableContainer(DatabaseTableConfig config,
			Map<String, String> formatPattern) {
		boolean insertable = currentUser.hasPermission(config,
				DataContainer.Permission.INSERT, true);
		boolean updateable = currentUser.hasPermission(config,
				DataContainer.Permission.UPDATE, true);
		boolean deleteable = currentUser.hasPermission(config,
				DataContainer.Permission.DELETE, true);

		List<ContainerOrder> orderBys = getDefaultOrder(config);

		return factory.getDatabaseTableContainer(eventBus,
				config.getDatasource(), config.getTablename(), insertable,
				updateable, deleteable, currentUser, formatPattern, orderBys,
				extractFilterPolicy(config), config.getPagelength(),
				config.getExportPagelength(), config.getSizeValid());
	}

	private List<ContainerOrder> getDefaultOrder(ContainerConfig containerConfig) {
		List<ContainerOrder> defaultOrder = new ArrayList<ContainerOrder>();
		if (containerConfig.getDefaultOrder() != null) {
			for (OrderConfig order : containerConfig.getDefaultOrder()
					.getOrder()) {
				defaultOrder.add(new ContainerOrder(order.getColumn(), order
						.getDirection().value().equals("desc") ? false : true));
			}
		}
		return defaultOrder;
	}

	/**
	 * Gibt die Map zurück, die zu jedem Modell-Element das entsprechende
	 * Konfigurations-Objekt enthält.
	 * 
	 * @return die Map, die zu jedem Modell-Element das entsprechende
	 *         Konfigurations-Objekt enthält
	 */
	public Map<Object, Object> getModelToConfigMapping() {
		return mappings;
	}

	CurrentUser getCurrentUser() {
		return currentUser;
	}

}

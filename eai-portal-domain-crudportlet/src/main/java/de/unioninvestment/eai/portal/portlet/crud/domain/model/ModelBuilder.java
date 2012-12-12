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
import de.unioninvestment.eai.portal.portlet.crud.config.BinaryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CheckboxConfig;
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
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TriggerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TriggersConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.JmxDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.util.Util;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplication;
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

	private static final String DISPLAY_ACTION = "display";

	private static final String EDIT_ACTION = "edit";

	private static final String BUILD_ACTION = "build";

	private static final String DELETE_ACTION = "delete";

	private static final String UPDATE_ACTION = "update";

	private static final String INSERT_ACTION = "insert";

	private static final String MESSAGE_KEY_EDITABLE_TABLE_WITHOUT_PRIMARY_KEY = "portlet.crud.unsupported.tableconfig.editable.without.primary.key";

	private static final String MESSAGE_KEY_EXPORTABLE_TABLE_WITHOUT_PRIMARY_KEY = "portlet.crud.unsupported.tableconfig.export.without.primary.key";

	private final Config config;

	private final ConnectionPoolFactory connectionPoolFactory;

	private final ResetFormAction resetFormAction;

	private final FieldValidatorFactory fieldValidatorFactory;

	private final int defaultSelectWidth;

	private final ModelFactory factory;

	private Map<Object, Object> mappings = new HashMap<Object, Object>();

	private Portlet portlet;

	private CurrentUser currentUser;

	private List<Form> forms = new ArrayList<Form>();

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param factory
	 *            Model-Factory
	 * @param connectionPoolFactory
	 *            ConnectionPool-Factory
	 * @param resetFormAction
	 *            Resetbutton
	 * @param fieldValidatorFactory
	 *            FieldValidator-Factory
	 * @param defaultSelectWidth
	 *            Breite der Selectboxen
	 * @param config
	 *            Portlet Konfiguration
	 * @param portletId
	 *            Id des Portlets
	 */
	public ModelBuilder(ModelFactory factory,
			ConnectionPoolFactory connectionPoolFactory,
			ResetFormAction resetFormAction,
			FieldValidatorFactory fieldValidatorFactory,
			int defaultSelectWidth, Config config) {
		this.factory = factory;
		this.connectionPoolFactory = connectionPoolFactory;
		this.resetFormAction = resetFormAction;
		this.fieldValidatorFactory = fieldValidatorFactory;
		this.defaultSelectWidth = defaultSelectWidth;
		this.config = config;

	}

	/**
	 * Die PortletPresenter-Konfiguration.
	 * 
	 * @return eine Portlet-Instanz
	 */
	public Portlet build() {

		portlet = new Portlet(config.getPortletConfig());
		mappings.put(portlet, config);

		if (config.getPortletConfig().getRoles() != null) {
			LiferayApplication application = LiferayApplication
					.getCurrentApplication();
			String portletId = application.getPortletId();
			long communityId = application.getCommunityId();
			for (RoleConfig roleConfig : config.getPortletConfig().getRoles()
					.getRole()) {
				portlet.addRole(new Role(roleConfig.getName(), config
						.getRoleResourceIDs().get(
								portletId + "_" + communityId + "_"
										+ roleConfig.getName())));
			}
		}

		currentUser = new CurrentUser(portlet.getRoles());

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
		if (currentUser.hasPermissions(componentConfig, "build", true)) {
			if (componentConfig instanceof FormConfig) {
				return buildForm((FormConfig) componentConfig);
			} else if (componentConfig instanceof TableConfig) {
				return buildTable((TableConfig) componentConfig);
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

	private Component buildCustomComponent(ScriptComponentConfig componentConfig) {
		CustomComponent scriptComponent = new CustomComponent(componentConfig);
		String componentId = componentConfig.getId();
		if (StringUtils.isNotEmpty(componentId)) {
			portlet.addElementById(componentId, scriptComponent);
		}
		mappings.put(scriptComponent, componentConfig);
		return scriptComponent;
	}

	private Component buildTabs(TabsConfig config) {
		Tabs tabs = new Tabs();
		mappings.put(tabs, config);
		for (TabConfig tabConfig : config.getTab()) {
			if (currentUser.hasPermissions(tabConfig, BUILD_ACTION, true)) {
				Tab tab = (Tab) buildPanel(tabConfig);
				tabs.addElement(tab);
			}
		}
		return tabs;
	}

	private Panel createPanelInstance(PanelConfig panelConfig) {
		if (panelConfig instanceof PageConfig) {
			return new Page();
		} else if (panelConfig instanceof TabConfig) {
			TabConfig tabConfig = (TabConfig) panelConfig;
			Tab tab = new Tab(tabConfig);
			mappings.put(tab, tabConfig);
			portlet.addElementById(tabConfig.getId(), tab);
			return tab;
		} else if (panelConfig instanceof DialogConfig) {
			DialogConfig dialogConfig = (DialogConfig) panelConfig;
			Dialog dialog = new Dialog(dialogConfig.getId(),
					dialogConfig.getBackButtonCaption());
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

			if (currentUser.hasPermissions(config, BUILD_ACTION, true)) {
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
		ConnectionPool connectionPool = null;
		if (config.getQuery() != null) {
			String datasource;
			if (StringUtils.isEmpty(config.getQuery().getDatasource())) {
				datasource = ds;
			} else {
				datasource = config.getQuery().getDatasource();
			}
			connectionPool = connectionPoolFactory.getPool(datasource);
			QueryOptionList queryOptionList = new QueryOptionList(
					connectionPool, config);

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
		if (currentUser.hasPermissions(tableConfig, BUILD_ACTION, true)) {
			verifyPrimaryKeyColumnsArePresentIfNeccessary(tableConfig);
			String dataSource = getDataSource(tableConfig);
			TableColumns tableColumns = buildColumns(tableConfig.getColumns(),
					dataSource);

			boolean editable = currentUser.hasPermissions(tableConfig,
					EDIT_ACTION, tableConfig.isEditable());
			Table table = new Table(tableConfig, tableColumns, editable);
			mappings.put(table, tableConfig);
			portlet.addElementById(tableConfig.getId(), table);

			table.setActions(buildTableActions(tableConfig.getAction(), table));
			DataContainer container = buildContainer(tableConfig, tableColumns);

			table.setContainer(container);
			return table;
		}

		return null;
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

				if (currentUser.hasPermissions(tac, BUILD_ACTION, true)) {
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

			OptionList optionList = null;
			if (c.getSelect() != null && c.getSelect().getDynamic() == null) {
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

			CheckBoxTableColumn checkBox = null;

			if (c.getCheckbox() != null) {
				try {
					checkBox = buildCheckBox(c.getCheckbox());
				} catch (Exception e) {
					LOG.warn(
							"Fehler beim erzeugen einer Checkbox für die Spalte: "
									+ c.getName(), e);
				}
			}

			Hidden hs = Hidden.valueOf(c.getHidden().toString());
			if (!currentUser.hasPermissions(c, DISPLAY_ACTION, true)) {
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
			boolean isEditable = currentUser.hasPermissions(c, EDIT_ACTION,
					isEditableDefault);

			FileMetadata fileMetadata = buildFileMetadata(c.getBinary());

			Class<?> generatedType = classOfGeneratedType(c.getGeneratedType());

			TableColumn tableColumn = new TableColumn(c.getName(),
					c.getTitle(), c.getLongtitle(), hs, isEditable,
					c.isPrimaryKey(), c.isMultiline(), c.getRows(), width,
					c.getInputPrompt(), validators, optionList, checkBox,
					c.getDisplayFormat(), fileMetadata, generatedType);

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

	private CheckBoxTableColumn buildCheckBox(CheckboxConfig checkboxConfig) {
		return new CheckBoxTableColumn(checkboxConfig);
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
		} else if (tableConfig.getJmxContainer() != null) {
			container = buildJmxContainer(tableConfig.getJmxContainer(),
					formatPattern);
			mappings.put(container, tableConfig.getJmxContainer());
		} else {
			throw new BusinessException("portlet.crud.unsupported.tableconfig");
		}

		return container;
	}

	private DataContainer buildJmxContainer(JmxContainerConfig jmxContainer,
			Map<String, String> formatPattern) {

		List<ContainerOrder> defaultOrder = getDefaultOrder(jmxContainer);

		GenericDataContainer genericDataContainer = factory
				.getGenericDataContainer(formatPattern, defaultOrder,
						extractFilterPolicy(jmxContainer));

		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainer, currentUser);

		genericDataContainer.setDelegate(jmxDelegate);
		return genericDataContainer;
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

		return factory.getGenericDataContainer(formatPattern, defaultOrder,
				extractFilterPolicy(scriptContainer));
	}

	private DataContainer buildQueryContainer(
			DatabaseQueryConfig databaseQuery, List<String> primaryKeys,
			Map<String, String> displayPattern) {
		boolean insertable = currentUser
				.hasPermissions(databaseQuery, INSERT_ACTION,
						databaseQuery.getInsert() != null
								&& databaseQuery.getInsert().getStatement()
										.getSource() != null);
		boolean updateable = currentUser
				.hasPermissions(databaseQuery, UPDATE_ACTION,
						databaseQuery.getUpdate() != null
								&& databaseQuery.getUpdate().getStatement()
										.getSource() != null);
		boolean deleteable = currentUser
				.hasPermissions(databaseQuery, DELETE_ACTION,
						databaseQuery.getDelete() != null
								&& databaseQuery.getDelete().getStatement()
										.getSource() != null);
		List<ContainerOrder> orderBys = getDefaultOrder(databaseQuery);
		return factory.getDatabaseQueryContainer(databaseQuery.getDatasource(),
				databaseQuery.getQuery(), insertable, updateable, deleteable,
				primaryKeys, currentUser.getName(), displayPattern, orderBys,
				extractFilterPolicy(databaseQuery),
				databaseQuery.getPagelength(),
				databaseQuery.getExportPagelength(),
				databaseQuery.getSizeValid());
	}

	private DataContainer buildTableContainer(DatabaseTableConfig config,
			Map<String, String> formatPattern) {
		boolean insertable = currentUser.hasPermissions(config, INSERT_ACTION,
				true);
		boolean updateable = currentUser.hasPermissions(config, UPDATE_ACTION,
				true);
		boolean deleteable = currentUser.hasPermissions(config, DELETE_ACTION,
				true);

		List<ContainerOrder> orderBys = getDefaultOrder(config);

		return factory.getDatabaseTableContainer(config.getDatasource(),
				config.getTablename(), insertable, updateable, deleteable,
				currentUser, formatPattern, orderBys,
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

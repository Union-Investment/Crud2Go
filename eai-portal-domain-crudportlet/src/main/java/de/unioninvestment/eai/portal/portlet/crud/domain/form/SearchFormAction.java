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
package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.config.AllFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AnyFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ContainsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EndsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EqualsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.IncludeFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.LessFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.LessOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.NotFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.NothingFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegExpFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchTablesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StartsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.EndsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Not;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Nothing;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.RegExpFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLWhereFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.CrudApplication;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.support.NumberFormatter;

/**
 * Implementiert die Suche in einer Tabelle abhängig von einer
 * Formulareinstellung.
 * 
 * @author carsten.mjartan
 */
public class SearchFormAction implements ActionHandler {

	private final FormActionConfig actionConfig;
	private final Portlet portlet;
	private SQLWhereFactory whereFactory;
	private boolean requiresFilter;

	private int timeout = 0;
	private CustomFilterFactory customFilterFactory;

	/**
	 * @param actionConfig
	 *            die Konfiguration zur Suche
	 * @param portlet
	 *            das Portlet für die Auflösung von Element-IDs
	 */
	public SearchFormAction(FormActionConfig actionConfig, Portlet portlet) {
		this.actionConfig = actionConfig;
		this.portlet = portlet;
		SearchConfig searchConfig = actionConfig.getSearch();
		if (searchConfig != null) {
			this.timeout = searchConfig.getTimeoutInSeconds();
			this.requiresFilter = searchConfig.isRequiresFilter();
		}
	}

	/**
	 * @param seconds
	 *            die Timeoutzeit in Sekunden.
	 */
	public void setTimeout(int seconds) {
		if (seconds >= 0) {
			this.timeout = seconds;
		}
	}

	/**
	 * @param whereFactory
	 *            Factory für SQL-Where-Filter. Wird bei der Initialisierung des
	 *            Scripting-Modells gesetzt.
	 */
	public void setWhereFactory(SQLWhereFactory whereFactory) {
		this.whereFactory = whereFactory;
	}

	@Override
	public void execute(Form form) {
		boolean hasFilter = form.hasFilter();
		if (!requiresFilter || hasFilter) {
			List<Table> tables = findSearchableTables(form);

			for (Table table : tables) {
				if (policyAllowsFiltering(table)) {
					applyFiltersToTable(form, table);
				}
			}
		} else {
			throw new BusinessException("portlet.crud.search.filter.mandatory");
		}
	}

	private boolean policyAllowsFiltering(Table table) {
		CrudApplication application = (CrudApplication) PortletApplication
				.getCurrentApplication();
		if (application != null && application.isInitializing()) {
			FilterPolicy policy = table.getContainer().getFilterPolicy();
			if (policy == FilterPolicy.NOTHING
					|| policy == FilterPolicy.NOTHING_AT_ALL) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gibt an, ob ein Where-Filter existiert.
	 * 
	 * @return ob ein Where-Filter existiert
	 */
	public boolean whereFilterExists() {
		if (actionConfig.getSearch() != null) {
			for (FilterConfig config : actionConfig.getSearch()
					.getApplyFilters().getFilters()) {
				if (config instanceof SQLFilterConfig) {
					return true;
				}
			}
		}
		return false;
	}

	private void applyFiltersToTable(Form form, Table table) {
		List<Filter> filters = createFilters(form, table);
		DataContainer container = table.getContainer();
		if (this.timeout > 0) {
			container.replaceFilters(filters, false, this.timeout);
		} else {
			table.getContainer().replaceFilters(filters, false);
		}
	}

	/**
	 * Erzeugt zu einer Tabelle passenden Filter.
	 * 
	 * @param form
	 *            Formularmodel
	 * @param table
	 *            Tabellenmodel
	 * @return Filterliste
	 */
	List<Filter> createFilters(Form form, Table table) {
		List<Filter> filters;
		List<FilterConfig> filterConfigs;
		if (actionConfig.getSearch() != null
				&& actionConfig.getSearch().getApplyFilters() != null) {
			filterConfigs = actionConfig.getSearch().getApplyFilters()
					.getFilters();
		} else {
			filterConfigs = createDefaultConfig(form, table);
		}
		filters = getExplicitFiltersForTable(form, table, filterConfigs);
		return filters;
	}

	private List<FilterConfig> createDefaultConfig(Form form, Table table) {
		List<FilterConfig> result = new ArrayList<FilterConfig>();
		DataContainer container = table.getContainer();
		for (FormField field : form.getFields()) {
			String fieldName = field.getName();
			Class<?> columnType = container.getType(fieldName);
			if (columnType == null) {
				continue;
			} else if (field instanceof DateFormField
					|| field instanceof OptionListFormField
					|| field instanceof CheckBoxFormField
					|| Number.class.isAssignableFrom(columnType)) {
				EqualsFilterConfig config = new EqualsFilterConfig();
				config.setField(fieldName);
				config.setColumn(fieldName);
				result.add(config);
			} else {
				StartsWithFilterConfig config = new StartsWithFilterConfig();
				config.setField(fieldName);
				config.setColumn(fieldName);
				result.add(config);
			}
		}
		return result;
	}

	private List<Filter> getExplicitFiltersForTable(Form form, Table table,
			List<FilterConfig> filterConfigs) {
		List<Filter> result = new ArrayList<Filter>();
		DataContainer container = table.getContainer();
		for (FilterConfig config : filterConfigs) {
			if (!filterMatchesTable(table, config)) {
				continue;
			}

			if (config instanceof ComparisonFilterConfig) {
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) config;

				FormField formField = form.getFields().get(
						comparisonFilterConfig.getField());

				if (formField instanceof MultiOptionListFormField) {
					if (config instanceof EqualsFilterConfig) {
						EqualsFilterConfig equalsFilterConfig = (EqualsFilterConfig) config;
						MultiOptionListFormField multiSelectionFormField = (MultiOptionListFormField) form
								.getFields().get(equalsFilterConfig.getField());
						List<Filter> filterList = new ArrayList<Filter>();
						for (String value : multiSelectionFormField.getValues()) {
							filterList.add(new Equal(equalsFilterConfig
									.getColumn(), value));
						}
						result.add(new Any(filterList));
						continue;

					} else {
						throw new IllegalArgumentException(
								"Filter des Typs '"
										+ config.getClass().getSimpleName()
										+ "' kann nicht auf Multiselect-Felder angewendet werden");
					}
				}

				String fieldValue = formField.getValue();
				if (fieldValue == null || fieldValue.trim().equals("")) {
					continue;
				}

				String columnName = comparisonFilterConfig.getColumn();
				Class<?> columnType = container.getType(columnName);
				if (columnType == null) {
					continue;
				} else if (String.class.isAssignableFrom(columnType)) {
					addGeneralFilterByConfig(result, config, fieldValue,
							columnName);
				} else if (Number.class.isAssignableFrom(columnType)) {
					@SuppressWarnings("unchecked")
					Number numberValue = NumberFormatter.parseNumber(
							fieldValue, (Class<? extends Number>) columnType,
							(NumberFormat) container.getFormat(columnName));
					if (!addGeneralFilterByConfig(result, config, numberValue,
							columnName)) {
						throw new IllegalArgumentException(
								"Filter des Typs '"
										+ config.getClass().getSimpleName()
										+ "' kann nicht auf Number-Spalten angewendet werden");
					}
				} else if (Date.class.isAssignableFrom(columnType)
						&& formField instanceof DateFormField) {
					DateFormField dateFormField = (DateFormField) formField;
					addTemporalFilterByConfig(result, config,
							convert(dateFormField.getBeginDate(), columnType),
							convert(dateFormField.getEndDate(), columnType),
							columnName);
				} else {
					throw new IllegalArgumentException("Cannot filter column '"
							+ columnName + ": Unkown column type '"
							+ columnType + "'");
				}

			} else if (config instanceof AnyFilterConfig) {
				List<Filter> explicitFiltersForTable = getExplicitFiltersForTable(
						form, table, ((AnyFilterConfig) config).getFilters());
				if (explicitFiltersForTable.size() > 0) {
					result.add(new Any(explicitFiltersForTable));
				}
			} else if (config instanceof AllFilterConfig) {
				List<Filter> subfilters = getExplicitFiltersForTable(form,
						table, ((AllFilterConfig) config).getFilters());
				if (subfilters.size() > 0) {
					result.add(new All(subfilters));
				}
			} else if (config instanceof NotFilterConfig) {
				List<Filter> subfilters = getExplicitFiltersForTable(form,
						table, ((NotFilterConfig) config).getFilters());
				if (subfilters.size() > 0) {
					result.add(new Not(subfilters));
				}
			} else if (config instanceof CustomFilterConfig) {
				CustomFilterConfig customFilterConfig = (CustomFilterConfig) config;
				CustomFilter filter = customFilterFactory
						.createCustomFilter(customFilterConfig);
				if (filter != null) {
					result.add(filter);
				}

			} else if (config instanceof SQLFilterConfig) {
				SQLFilterConfig sqlFilterConfig = (SQLFilterConfig) config;
				SQLFilter filter = whereFactory
						.createFilter(sqlFilterConfig.getColumn(),
								sqlFilterConfig.getWhere());
				if (filter != null) {
					result.add(filter);
				}
			} else if (config instanceof IncludeFilterConfig) {
				IncludeFilterConfig includeFilterConfig = (IncludeFilterConfig) config;

				String id = ((FormActionConfig) includeFilterConfig.getAction())
						.getId();
				FormAction formAction = (FormAction) portlet.getElementById(id);

				SearchFormAction otherActionHandler = (SearchFormAction) formAction
						.getActionHandler();

				List<Filter> filters = otherActionHandler.createFilters(
						formAction.getForm(), table);

				if (filters != null) {
					result.add(new All(filters));
				}
			}
		}
		return result;
	}

	private Date convert(Date date, Class<?> columnType) {
		if (columnType == Date.class) {
			return date;
		} else if (columnType == java.sql.Date.class) {
			return new java.sql.Date(date.getTime());
		} else if (columnType == Timestamp.class) {
			return new Timestamp(date.getTime());
		} else {
			throw new IllegalArgumentException("Unsupported column type: "
					+ columnType);
		}
	}

	private void addTemporalFilterByConfig(List<Filter> result,
			FilterConfig config, Date beginDate, Date endDate, String columnName) {
		if (config instanceof EqualsFilterConfig) {
			Filter beginFilter = new Greater(columnName, beginDate, true);
			Filter endFilter = new Less(columnName, endDate, false);
			result.add(new All(Arrays.asList(beginFilter, endFilter)));
		} else if (config instanceof GreaterOrEqualFilterConfig) {
			result.add(new Greater(columnName, beginDate, true));
		} else if (config instanceof GreaterFilterConfig) {
			result.add(new Greater(columnName, endDate, true));
		} else if (config instanceof LessOrEqualFilterConfig) {
			result.add(new Less(columnName, endDate, false));
		} else if (config instanceof LessFilterConfig) {
			result.add(new Less(columnName, beginDate, false));
		} else {
			throw new IllegalArgumentException("Filter des Typs '"
					+ config.getClass().getSimpleName()
					+ "' kann nicht auf temporale Spalten angewendet werden");
		}
	}

	private boolean filterMatchesTable(Table table, FilterConfig config) {
		return config.getTable() == null
				|| config.getTable().equals(table.getId());
	}

	private boolean addGeneralFilterByConfig(List<Filter> result,
			FilterConfig config, Object fieldValue, String columnName) {
		if (config instanceof EqualsFilterConfig) {
			result.add(new Equal(columnName, fieldValue));
		} else if (config instanceof GreaterFilterConfig) {
			result.add(new Greater(columnName, fieldValue, false));
		} else if (config instanceof GreaterOrEqualFilterConfig) {
			result.add(new Greater(columnName, fieldValue, true));
		} else if (config instanceof LessFilterConfig) {
			result.add(new Less(columnName, fieldValue, false));
		} else if (config instanceof LessOrEqualFilterConfig) {
			result.add(new Less(columnName, fieldValue, true));
		} else if (config instanceof StartsWithFilterConfig) {
			result.add(new StartsWith(columnName, fieldValue,
					((StartsWithFilterConfig) config).isCaseSensitive()));
		} else if (config instanceof EndsWithFilterConfig) {
			result.add(new EndsWith(columnName, fieldValue,
					((EndsWithFilterConfig) config).isCaseSensitive()));
		} else if (config instanceof ContainsFilterConfig) {
			result.add(new Contains(columnName, fieldValue,
					((ContainsFilterConfig) config).isCaseSensitive()));
		} else if (config instanceof RegExpFilterConfig) {
			String valueString = fieldValue == null ? null : fieldValue
					.toString();
			result.add(new RegExpFilter(columnName, valueString,
					((RegExpFilterConfig) config).getModifiers()));
		} else if (config instanceof NothingFilterConfig) {
			result.add(new Nothing());
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Durchläuft des Modelbaum und liefert alle für die Suche nötigen
	 * Tabellenkomponenten.
	 * 
	 * @param form
	 *            - das aktuelle Formular als Ausgangspunkt für die Suche
	 * @return - eine Liste aller relevanten Tabellen für die Suche.
	 */
	List<Table> findSearchableTables(Form form) {
		List<Table> result = new ArrayList<Table>();
		if (actionConfig.getSearch() != null
				&& actionConfig.getSearch().getTables() != null) {
			findConfiguredTables(result);
		} else {
			findSearchableTables(form.getPanel(), form, result);
		}
		return result;
	}

	private void findConfiguredTables(List<Table> result) {
		SearchTablesConfig tables = actionConfig.getSearch().getTables();
		for (SearchTableConfig searchTableConfig : tables.getTable()) {
			TableConfig tableConfig = (TableConfig) searchTableConfig.getId();
			Table table = (Table) portlet.getElementById(tableConfig.getId());
			result.add(table);
		}
	}

	private void findSearchableTables(Panel panel, Form startingWithForm,
			List<Table> result) {
		Component nextElement = panel.findNextElement(Component.class,
				startingWithForm);
		while (nextElement != null && !(nextElement instanceof Form)) {
			if (nextElement instanceof Table) {
				result.add((Table) nextElement);
			} else if (nextElement instanceof Tabs) {
				for (Tab tab : ((Tabs) nextElement).getElements()) {
					findSearchableTables(tab, null, result);
				}
			}
			nextElement = panel.findNextElement(Component.class, nextElement);
		}
	}

	public void setCustomFilterFactory(CustomFilterFactory filterFactory) {
		this.customFilterFactory = filterFactory;
	}

}
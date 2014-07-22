package de.unioninvestment.eai.portal.portlet.crud.domain.form;

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
import de.unioninvestment.eai.portal.portlet.crud.config.IncludeFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.NotFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegExpFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StartsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

public class SearchFormActionValidator {
	
	private SearchFormAction action;
	private Form aForm;
	
	public SearchFormActionValidator(SearchFormAction action, Form aForm) {
		super();
		this.action = action;
		this.aForm = aForm;
	}
	
	public void validate(){
		List<Table> searchableTables = action.findSearchableTables(aForm);
		for(Table aTable:searchableTables){
			checkFiltersForTable(aForm, aTable);
		}
	}
	
	
	private void checkFiltersForTable(Form form, Table table){
		List<FilterConfig> filterConfigs = action.createFilterConfigs(form, table);
		checkFiltersForTable(form, table, filterConfigs);
	}

	private void checkFiltersForTable(Form form, Table table,
			List<FilterConfig> filterConfigs) {
		DataContainer container = table.getContainer();
		for(FilterConfig config: filterConfigs){
			if(!action.filterMatchesTable(table, config)){
				continue;
			}
			//*DBG*/ System.out.println("Got past matching");
			if (config instanceof ComparisonFilterConfig) {
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) config;

				//*DBG*/ System.out.println("Got comparison config "+config);
				
				FormField formField = form.getFields().get(
						comparisonFilterConfig.getField());

				//*DBG*/ System.out.println("Got form field "+formField+ " config "+config);
				
				if (formField instanceof MultiOptionListFormField) {
					if (!(config instanceof EqualsFilterConfig)) {
						throw new IllegalArgumentException(
								"Filter des Typs '"
										+ config.getClass().getSimpleName()
										+ "' kann nicht auf Multiselect-Felder angewendet werden");
					}
				}
				String columnName = comparisonFilterConfig.getColumn();
				Class<?> columnType = container.getType(columnName);
				
				//*DBG*/ System.out.println("Got column type " + columnType);
				
				if (columnType == null) {
					continue;
				} else if (String.class.isAssignableFrom(columnType)) {
					// All ComparisonFilter are applicable to Strings
				} else if (Number.class.isAssignableFrom(columnType)) {
					if(isStringBasedFilterConfig(config)){
						throw new IllegalArgumentException("Filter des Typs '"
								+ config.getClass().getSimpleName()
								+ "' kann nicht auf Number-Spalten angewendet werden");
					}
					
				} else if (Date.class.isAssignableFrom(columnType)
						&& formField instanceof DateFormField) {
					
					if(isStringBasedFilterConfig(config)){
						throw new IllegalArgumentException("Filter des Typs '"
								+ config.getClass().getSimpleName()
								+ "' kann nicht auf Date-Spalten angewendet werden");
					}
				} else {
					throw new IllegalArgumentException("Cannot filter column '"
							+ columnName + ": Unkown column type '"
							+ columnType + "'");
				}
				
			} else if (config instanceof AnyFilterConfig) {
				checkFiltersForTable(form, table, ((AnyFilterConfig) config).getFilters());
			} else if (config instanceof AllFilterConfig) {
				checkFiltersForTable(form, table, ((AllFilterConfig) config).getFilters());
			} else if (config instanceof NotFilterConfig) {
				checkFiltersForTable(form, table, ((NotFilterConfig) config).getFilters());
			} else if (config instanceof CustomFilterConfig) {
				//Do Nothing
			} else if (config instanceof SQLFilterConfig) {
				//Do nothing
			} else if (config instanceof IncludeFilterConfig) {
				IncludeFilterConfig includeFilterConfig = (IncludeFilterConfig) config;
				FormAction formAction = action.getActionForIncludeFilter(includeFilterConfig);
				SearchFormAction otherActionHandler = (SearchFormAction) formAction.getActionHandler();
				
				List<FilterConfig> includeFilterConfigsList = otherActionHandler.createFilterConfigs(form, table);
				checkFiltersForTable(form, table, includeFilterConfigsList);
			}

		}
	}

	boolean isStringBasedFilterConfig(FilterConfig config) {
		return config instanceof StartsWithFilterConfig ||
				config instanceof EndsWithFilterConfig || 
				config instanceof ContainsFilterConfig ||
				config instanceof RegExpFilterConfig;
	}


}

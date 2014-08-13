package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;

import java.util.Date;
import java.util.List;

// FIXME: JavaDoc an Klasse und Public Methoden
// FIXME: ggf. ebenfalls ins validation-Package verschieben
// FIXME: Unklare Aufgabenverteilung zwischen dieser Klasse und Prüfungen in CrudValidator (Klassen- und Methodennamen überdenken)
// FIXME: Durchgängige Verwendung von ModelValidationException (besser: Runtime statt Model?) statt IllegalArgumentException
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

    // FIXME: Aufteilen zu besseren Lesbarkeit (Clean Code)
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
								+ "' kann nicht auf Number-Spalten (" + columnName + ") angewendet werden");
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
							+ columnName + ": Unknown column type '"
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
				
				List<FilterConfig> includeFilterConfigsList = otherActionHandler.createFilterConfigs(formAction.getForm(), table);
				checkFiltersForTable(formAction.getForm(), table, includeFilterConfigsList);
			} else {
                // FIXME: Sicherstellen dass auch zukünftig alle Filter berücksichtigt sind
                // LOGGER.warn(...)
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

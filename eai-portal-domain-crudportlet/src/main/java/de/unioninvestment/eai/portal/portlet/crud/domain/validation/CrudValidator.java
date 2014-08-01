package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormActionValidator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// FIXME: JavaDoc für Klasse und Public Methoden
// FIXME: Umbenennen der Klasse: SearchFormActionsValidator und dafür ggf. kürzen der Methodennamen (um "Search")
public class CrudValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrudValidator.class);

	private ModelBuilder modelBuilder; 
	private Portlet portletDomain;
	private PortletConfig portletConfig;
	
	public CrudValidator(ModelBuilder modelBuilder, Portlet portletDomain,
			PortletConfig portletConfig) {
		super();
		this.modelBuilder = modelBuilder;
		this.portletDomain = portletDomain;
		this.portletConfig = portletConfig;
	}

	public void validate(){
		//possibly todo
		//validateDateColumnsHaveDateType();
		checkSearchFilterColumnsDefinedInQueries();
		checkSearchFiltersFieldsContainedInForms();
		validateSearchFormFilters();
	}
	
	void checkSearchFiltersFieldsContainedInForms(){
		List<Form> forms = modelBuilder.getForms();
		
		for(Form aForm:forms){
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if(searchActionWrapper!=null){
                // FIXME: methode der besseren Lesbarkeit halber aufteilen
				final List<String> searchFieldNames = new ArrayList<String>();
				final Set<String> fieldNames = new LinkedHashSet<String>();

				FormActionConfig actionConfig = (FormActionConfig) modelBuilder.getModelToConfigMapping().get(searchActionWrapper);

				// ermittle Ziel-Tabellen über SearchFormAction
				// traversiere rekursiv über filterconfigs
				//   wenn explizit Tabelle angegeben:, prüfe auf Existenz in dessen Container
				//   wenn keine Tabelle angegeben: prüfe auf Existenz in mindestens einem der Container der Tabellen
				if(actionConfig.getSearch() != null){
					SearchConfig searchConfig = actionConfig.getSearch();
                    // FIXME: ApplyFilters ist optional. Wenn nicht gesetzt, Prüfung der Action abbrechen
					List<FilterConfig> filters = searchConfig.getApplyFilters().getFilters();
					gatherFormFieldNamesInFilter(filters, searchFieldNames);
				}
				for(FormField field: aForm.getFields()){
					fieldNames.add(field.getName());
				}
				if(!fieldNames.containsAll(searchFieldNames)){
					List<String> wrongFields = new ArrayList<String>(searchFieldNames);
					wrongFields.removeAll(fieldNames);
					throw new IllegalArgumentException("Die Feldern '"
                            + StringUtils.join(wrongFields, ", ") + "' sind nicht den durchsuchten Formen verfügbar");
				}


			}
		}
	}

	void checkSearchFilterColumnsDefinedInQueries(){

		List<Form> forms = modelBuilder.getForms();
				
		for(Form aForm:forms){
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if(searchActionWrapper!=null){
                // FIXME: methode der besseren Lesbarkeit halber aufteilen, ggf. Codeduplikation der Iteration vermeiden
				final List<String> searchColumnNames = new ArrayList<String>();
				final Set<String> columnNames = new LinkedHashSet<String>();

				FormActionConfig actionConfig = (FormActionConfig) modelBuilder.getModelToConfigMapping().get(searchActionWrapper);
				
				// ermittle Ziel-Tabellen über SearchFormAction
				// traversiere rekursiv über filterconfigs
				//   wenn explizit Tabelle angegeben:, prüfe auf Existenz in dessen Container
				//   wenn keine Tabelle angegeben: prüfe auf Existenz in mindestens einem der Container der Tabellen 
				if(actionConfig.getSearch() != null){
					SearchConfig searchConfig = actionConfig.getSearch();
                    // FIXME: <apply-filters> ist optional. Wenn nicht gesetzt, Prüfung der Action abbrechen
					List<FilterConfig> filters = searchConfig.getApplyFilters().getFilters();
					gatherSearchColumnNames(filters, searchColumnNames);
				}
				
				
				SearchFormAction searchAction = (SearchFormAction)searchActionWrapper.getActionHandler();
				List<Table> searchableTables = searchAction.findSearchableTables(aForm);
				for(Table aTable:searchableTables){
					columnNames.addAll(aTable.getContainer().getColumns());
				}
				
				if(searchColumnNames.size()>0){
					if(columnNames.size()>0){
						for(String name:searchColumnNames){
							if(!columnNames.contains(name)){
								throw new IllegalArgumentException("Die Spalte '"
			                            + name + "' ist nicht den durchsuchten Tabellen verfügbar");
							}
						}
					}else{
						throw new IllegalArgumentException("Config ist nicht richtig konfiguriert. Da gibt es Such Filtern, aber keine Tabellen für Suche");
					}
				}

			}
		}

	}

	static void gatherFormFieldNamesInFilter(List<FilterConfig> filters,
			final List<String> formFieldNames) {
		for(FilterConfig filterConf:filters){
			if(filterConf instanceof ComparisonFilterConfig){
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) filterConf;
				String column = comparisonFilterConfig.getField();
				if(column!=null){
					formFieldNames.add(column);
				}
			}else if(filterConf instanceof SQLFilterConfig){
				//Do Nothing - no analysis of where statement
			} else if (filterConf instanceof FilterListConfig) {
				gatherFormFieldNamesInFilter(((FilterListConfig) filterConf).getFilters(), formFieldNames);	
			} else if (filterConf instanceof CustomFilterConfig) {
				//Do Nothing - Groovy Script
			} else if (filterConf instanceof IncludeFilterConfig) {
				//Skip - will be checked separately
			} else {
                LOGGER.warn("CrudValidator does not cover {}", filterConf.getClass().getName());
            }
		}
	}

	

	static void gatherSearchColumnNames(List<FilterConfig> filters,
			final List<String> searchColumnNames) {
		for(FilterConfig filterConf:filters){
			if(filterConf instanceof ComparisonFilterConfig){
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) filterConf;
				String column = comparisonFilterConfig.getColumn();
				if(column!=null){
					searchColumnNames.add(column);
				}
			}else if(filterConf instanceof SQLFilterConfig){
				SQLFilterConfig sqlFilterConfig = (SQLFilterConfig)filterConf;
				String column = sqlFilterConfig.getColumn();
				if(column!=null){
					searchColumnNames.add(column);
				}
			} else if (filterConf instanceof FilterListConfig) {
				gatherSearchColumnNames(((FilterListConfig) filterConf).getFilters(), searchColumnNames);	
			} else if (filterConf instanceof CustomFilterConfig) {
				//Do Nothing - Groovy Script
			} else if (filterConf instanceof IncludeFilterConfig) {
				//Skip - will be checked separately
            } else {
                LOGGER.warn("CrudValidator does not cover {}", filterConf.getClass().getName());
			}
		}
	}
	
	void validateSearchFormFilters() {
		List<Form> forms = modelBuilder.getForms();
		
		for(Form aForm:forms){
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if(searchActionWrapper!=null){
				SearchFormAction searchAction = (SearchFormAction)searchActionWrapper.getActionHandler();
				new SearchFormActionValidator(searchAction, aForm).validate();
			}
		}
	}

}

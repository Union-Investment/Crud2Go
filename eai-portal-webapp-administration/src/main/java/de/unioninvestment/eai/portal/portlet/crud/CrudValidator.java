package de.unioninvestment.eai.portal.portlet.crud;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationProcessor;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormActionValidator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

public class CrudValidator {
	
	private ModelBuilder modelBuilder; 
	private Portlet portletDomain;
	private PortletConfig portletConfig;
	
	public CrudValidator(ModelBuilder modelBuilder, Portlet portletDomain,
			Config portletConfig) {
		super();
		this.modelBuilder = modelBuilder;
		this.portletDomain = portletDomain;
		this.portletConfig = portletConfig.getPortletConfig();
	}

	public void validate(){
		//possibly todo
		//validateDateColumnsHaveDateType();
		checkSearchFilterColumnsDefinedInQueries();
		validateSearchFormFilters();
	}

	void checkSearchFilterColumnsDefinedInQueries(){
		final List<String> searchColumnNames = new ArrayList<String>();
		final Set<String> columnNames = new LinkedHashSet<String>();
		
		ConfigurationVisitor visitor = new ConfigurationVisitor(){
					public void visit(Object element){
						if(element instanceof FormActionConfig){
							FormActionConfig formActionConfig = (FormActionConfig)element;
							if(formActionConfig.getSearch() != null){
								SearchConfig searchConfig = formActionConfig.getSearch();
								List<FilterConfig> filters = searchConfig.getApplyFilters().getFilters();
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
									}
								}
							}
							
						}
						
						if(element instanceof TableConfig){
							TableConfig tableConfig = (TableConfig)element;
							if(tableConfig.getDatabaseQuery()!=null){
								Table table = (Table)portletDomain.getElementById(tableConfig.getId());
								if(table!=null){
									columnNames.addAll(table.getContainer().getColumns());
								}
							}
						}
					}
					
					public void visitAfter(Object element) {
						//intentionally left empty
					}
				};

		ConfigurationProcessor processor = new ConfigurationProcessor(visitor);
		processor.traverse(portletConfig);
		if(searchColumnNames.size()>0 && columnNames.size()>0){
			for(String name:searchColumnNames){
				if(!columnNames.contains(name)){
					throw new IllegalArgumentException("Spalte "+name+" ist nicht in Tabelle verfügbar");
				}
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

package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig
import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationProcessor
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField
import de.unioninvestment.eai.portal.portlet.crud.domain.model.QueryOptionList
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionTableColumn
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptDatabaseQueryContainer
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptForm
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormAction
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptOptionListFormField
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptTable

import com.vaadin.data.Property;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table

class PortletConfigChecks {
	public static void checkSearchColumns(String path, def portletConfig, def expectedMissingSearchColumns){
		def searchColumnNames = []
		Set columnNames = [] as Set
		
		def visitor = new ConfigurationVisitor(){
					void visit(element){
						if(element instanceof FormActionConfig){
							FormActionConfig formActionConfig = (FormActionConfig)element
							if(formActionConfig.search){
								def searchConfig = formActionConfig.search
								println "Found SEARCH CONFIG"
								searchConfig.applyFilters.filters.each{ FilterConfig filterConf->
									println filterConf.class
									if(filterConf instanceof ComparisonFilterConfig || filterConf instanceof SQLFilterConfig){
										if(filterConf.column){
											searchColumnNames<<filterConf.column
										}
									}
								}
							}
						}
						if(element instanceof ColumnConfig){
							ColumnConfig columnConfig = (ColumnConfig)element
							columnNames << columnConfig.name
						}
					};
					
					void visitAfter(element){
						//println "visitAfter: ${element}"
					};
				}

		def processor = new ConfigurationProcessor(visitor)
		processor.traverse(portletConfig)
		
		println "TEST SEARCH COLUMNS ${path} columns ${columnNames} searchColumns ${searchColumnNames} "
		if(columnNames && searchColumnNames){
			searchColumnNames.each{ name ->
				if(!columnNames.contains(name)){
					def expectedMissingCollumns = expectedMissingSearchColumns[path]
					if(!expectedMissingCollumns || (expectedMissingCollumns && !expectedMissingCollumns.contains(name))){
						Assert.assertTrue("Search Column "+name+" should be among table columns for path "+ path+ " columns "+columnNames,columnNames.contains(name))
					}
				}
			}
		}
	}

}

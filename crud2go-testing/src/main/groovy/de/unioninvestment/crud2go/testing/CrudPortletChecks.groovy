package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig

import org.junit.Assert

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

class CrudPortletChecks {

	static boolean CHECK_OPTIONS_SIZE = false

	public static void checkSearchColumns(String path, CrudTestConfig instance, def expectedMissingSearchColumns){
		def searchColumnNames = []
		Set columnNames = [] as Set
		def portletConfig = instance.config
		
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

	static def getOptions(def optionList){
		if(optionList instanceof QueryOptionList){
			return optionList.loadOptions()
		}
		return optionList.options
	}
	
	private static checkOptionList(String columnName, def optionList, String place = "form") {
		def options = getOptions(optionList)
		if(CHECK_OPTIONS_SIZE){
			Assert.assertTrue("Options should be available for field "+columnName+" on "+place, options.size()>0)
		}
		Assert.assertFalse("There should be no empty keys for search filters for field "+columnName+" on "+place,options.containsKey(''))
	}

	public static void checkOptionsInScriptPortlet(CrudTestConfig instance){
		def scriptPortlet = instance.portlet
		scriptPortlet.elements.each{ k, v ->
			if(v instanceof ScriptTable){

				if(v.container instanceof ScriptDatabaseQueryContainer){
					v.container.getCurrentQuery()
				}
				v.table.columns.columns.each{name, col ->
					if(col instanceof SelectionTableColumn){
						def optionList = col.optionList
						checkOptionList(name, optionList, 'Table')
					}
				}
			}
			if(v instanceof ScriptForm ){
				//println "Found form ${k} of type ${v.class}"
				v.fields.each{ fk, fv ->
					if (fv instanceof ScriptOptionListFormField){
						def optionListFormField = fv.formField
						//						println "Found optionlist form field ${fk} of type ${fv.class}"
						//						println optionListFormField.class

						if(optionListFormField instanceof OptionListFormField){
							//println "CHECKING QUERY"
							def optionList = optionListFormField.optionList
							checkOptionList(fk, optionList, 'Form')

						}else{
							println "NO QEURY CHECK"
						}

					}
				}
			}
		}
	}
	
	
	public static void checkSearchActions(def path, CrudTestConfig instance){
		def searchFields = []
		def searchAction = null
		def scriptPortlet = instance.portlet
	
		scriptPortlet.elements.each{ k, v ->
			if(v instanceof ScriptForm ){
				
				println "Found form ${k} of type ${v.class}"
				v.fields.each{ fk, fv ->
					
					println "Field ${fk} type ${fv}"
					fv.formField.eventRouter?.handler?.clear()
					if(!fv.formField.getValue()){
						
						if (fv instanceof ScriptOptionListFormField){
							def optionListFormField = fv.formField

							if(optionListFormField instanceof OptionListFormField){
								def optionList = optionListFormField.optionList
								Map options = getOptions(optionList)
								println "OPTIONS : ${options.getClass()} ${options}"
								if(options){
									setFieldValue(fk, fv.formField, options.keySet().iterator().next())
								}else{
									println "OPTIONS not found"
								}
							}else{
								println "NO QEURY CHECK"
							}

						}else{
							def valueChangedListeners = fv.formField.property.getListeners(Property.ValueChangeEvent.class)
							if(valueChangedListeners) {
								def copy = valueChangedListeners.toArray([])
								copy.each{
									fv.formField.property.removeValueChangeListener(it)
								}
							}
							def dateConfig = fv.formField.config.getDate()
							if(dateConfig){
								setFieldValue(fk, fv.formField, new java.text.SimpleDateFormat(dateConfig.format).format(new Date()))
							}else{
							//TODO: Discuss with Carsten concerning numeric vs String values
								//setFieldValue(fk, fv.formField, "aaa");
								setFieldValue(fk, fv.formField, "123456");
							}
						}
					}
				}

				
				v.actions.each{ ScriptFormAction action->
					if(action.title=='Suchen'){
						searchAction = action
					}
				}
			}
		}
		
		if(searchAction){
			def searchFormAction = searchAction.action.actionHandler
			if(searchFormAction){
				def form = searchAction.form.form
				println "Form ${form.class}"
				boolean hasFilter = form.hasFilter();
				if (!searchFormAction.requiresFilter || hasFilter) {
					println "${searchFormAction.timeout}"
					searchFormAction.timeout = 0
					List<Table> tables = searchFormAction.findSearchableTables(form);
					for (Table table : tables) {
						searchFormAction.applyFiltersToTable(form, table);
					}
				}
			}else{
				println "No SearchFormAction for ${path} searchAction ${searchAction}"
			}
		}

	}
	
	static def setFieldValue(fieldName, field, value){
		println "Field ${fieldName} set value ${value}"
		field.setValue(value)
	}

}

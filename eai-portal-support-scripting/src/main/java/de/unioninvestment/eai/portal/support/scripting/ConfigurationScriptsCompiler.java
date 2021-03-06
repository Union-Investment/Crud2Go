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
package de.unioninvestment.eai.portal.support.scripting;

import com.google.common.base.Strings;
import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig.Dynamic;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.util.Util;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Diese Klasse dient der Kompilierung aller Groovy-Scripte, die in der
 * XML-Konfiguration hinterlegt sind. Entsprechende Stellen im JAXB-Modell der
 * Konfiguration verwenden einen Konverter in die Klasse {@link GroovyScript},
 * so dass die Kompilate im Modell gespeichert und gecached werden können.
 * 
 * @author carsten.mjartan
 */
@Component
public class ConfigurationScriptsCompiler {
	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigurationScriptsCompiler.class);

	private final ScriptCompiler compiler;

	/**
	 * @param compiler
	 *            an den für die eigentliche Kompilierung delegiert wird.
	 */
	@Autowired
	public ConfigurationScriptsCompiler(ScriptCompiler compiler) {
		this.compiler = compiler;
	}

	/**
	 * Traversiert den JAXB-Objektbaum und kompiliert alle Scripte.
	 * 
	 * @param config
	 *            das Haupt-Objekt des JAXB-Konfigurationsmodells
	 */
	public void compileAllScripts(PortletConfig config) {
		if (!compileScripts(config.getScript(), "/portlet/script")) {
			compileNoopScriptClass(config);
		}
		compileAllClosureScripts(config);
	}

	private void compileAllClosureScripts(PortletConfig config) {
		compileClosure(config.getOnLoad(), "/onLoad");
		compileClosure(config.getOnReload(), "/onReload");
		compileClosure(config.getOnRefresh(), "/onRefresh");

		if (config.getTabs() != null) {
			compileAllClosureScripts(config.getTabs(), "");
		} else {
			compileAllClosureScripts(config.getPage(), "");
		}
		List<DialogConfig> dialogConfigList = config.getDialog();
		if (dialogConfigList != null) {
			for (DialogConfig dialogConfig : dialogConfigList) {
				compileAllClosureScripts(dialogConfig, "");
			}
		}
	}

	private void compileAllClosureScripts(TabsConfig tabs, String location) {
		compileClosure(tabs.getOnChange(), location + "/onChange");
		int i = 0;
		for (PanelConfig panel : tabs.getTab()) {
			compileAllClosureScripts(panel, location + "/tab[" + i++ + "]");
		}
	}

	private void compileAllClosureScripts(PanelConfig panel, String location) {
		if (panel instanceof TabConfig) {
			TabConfig tab = (TabConfig) panel;
			compileClosure(tab.getOnShow(), location + "/onShow");
			compileClosure(tab.getOnHide(), location + "/onHide");
		} else if (panel instanceof RegionConfig) {
			RegionConfig region = (RegionConfig) panel;
			compileClosure(region.getOnExpand(), location + "/onExpand");
			compileClosure(region.getOnCollapse(), location + "/onCollapse");
		}

		int i = 0;
		for (ComponentConfig component : panel.getElements()) {
			if (component instanceof CompoundSearchConfig) {
				CompoundSearchDetailsConfig details = ((CompoundSearchConfig) component).getDetails();
				if (details != null) {
					compileAllClosureScripts(
							details,
							location + "/elements[" + i++ + "]/details");
				}
			} else if (component instanceof TableConfig) {
				compileAllClosureScripts((TableConfig) component, location
						+ "/elements[" + i++ + "]");
			} else if (component instanceof FormConfig) {
				compileAllClosureScripts((FormConfig) component, location
						+ "/elements[" + i++ + "]");
			} else if (component instanceof TabsConfig) {
				compileAllClosureScripts((TabsConfig) component, location
						+ "/elements[" + i++ + "]");
			} else if (component instanceof ScriptComponentConfig) {
				compileComponentGeneratorScript(
						(ScriptComponentConfig) component, location
								+ "/elements[" + i++ + "]");
			} else if (component instanceof RegionConfig) {
				compileAllClosureScripts((RegionConfig) component, location
						+ "/elements[" + i++ + "]");
			}
		}
	}

	private void compileComponentGeneratorScript(
			ScriptComponentConfig component, String location) {
		compileClosure(component.getGenerator(), "builder", location
				+ "/generator");
	}

	private void compileAllClosureScripts(TableConfig table, String location) {
		compileTableOnModeChangeScript(table, location);
		compileTableOnSelectionChangeScript(table, location);
		compileTableOnDoubleClickScript(table, location);
		compileTableOnInitializeScript(table, location);
		compileTableActionScripts(table, location);
		compileContainerScript(table, location);
		compileRowStyleScript(table, location);
		compileTableOnRowChangeScript(table, location);
		compileClosure(table.getRowValidator(), "it,row", location
				+ "/row-validator");

		compileColumnScripts(table, location);
		compileRowEditableClosure(table, location);
	}

	private void compileColumnScripts(TableConfig table, String location) {
		if (table.getColumns() != null) {
			for (ColumnConfig column : table.getColumns().getColumn()) {
				compileClosure(column.getStyle(), "row, columnName", location
						+ "/columns/column/style");

				if (column.getSelect() != null
						&& column.getSelect().getDynamic() != null) {
					compileAllClosureScripts(column.getSelect().getDynamic(),
							location + "/columns/" + column.getName()
									+ "/select/dynamic");
				}
				compileGString(column.getDefault(), "now", location
						+ "/columns/column/default");

				GroovyScript editableClosure = column.getEditable();
				if (editableClosure != null
						&& !Util.isPlainBoolean(editableClosure)) {
					compileClosure(editableClosure, "table, columnName, row",
							location + "/columns/column/editable");
				}

				compileClosure(column.getValidator(), "it,value", location
						+ "/columns/column/validator");
				compileClosure(column.getGenerator(), "row,builder", location
						+ "/columns/column/generator");
				compileClosure(column.getGeneratedValue(), "row", location
						+ "/columns/column/generated-value");
			}
		}
	}

	private void compileRowStyleScript(TableConfig table, String location) {
		if (table.getRowStyle() != null) {
			compileClosure(table.getRowStyle(), "row", location + "/rowStyle");
		}
	}

	private void compileContainerScript(TableConfig table, String location) {
		if (table.getDatabaseQuery() != null) {
			compileAllClosureScripts(table.getDatabaseQuery(), location
					+ "/databaseQuery");
		} else if (table.getDatabaseTable() != null) {
			compileAllClosureScripts(table.getDatabaseTable(), location
					+ "/databaseTable");
		} else if (table.getScriptContainer() != null) {
			compileAllClosureScripts(table.getScriptContainer(), location
					+ "/scriptContainer");
		} else if (table.getRestContainer() != null) {
			compileAllClosureScripts(table.getRestContainer(), location
					+ "/scriptContainer");
		}
	}

	private void compileTableActionScripts(TableConfig table, String location) {
		int i = 0;
		for (TableActionConfig action : table.getAction()) {
			compileClosure(action.getOnExecution(), location + "/action[" + i++
					+ "]/onExecution");
			if (action.getExport() != null) {
				compileClosure(action.getExport().getFilename(), "it", location
						+ "/action[" + i + "]/export/filename");
			}
			if (action.getDownload() != null) {
				// "export" for backwards compatibility (deprecated)
				compileClosure(action.getDownload().getGenerator(),
						"it,factory,export", location + "/action[" + i
								+ "]/download/generator");
			}
		}
	}

	private void compileTableOnSelectionChangeScript(TableConfig table,
			String location) {
		compileClosure(table.getOnSelectionChange(), "it,selection", location
				+ "/onSelectionChange");
	}

	private void compileTableOnInitializeScript(TableConfig table,
			String location) {
		compileClosure(table.getOnInitialize(), "it", location
				+ "/onInitialize");
	}

	private void compileTableOnDoubleClickScript(TableConfig table,
			String location) {
		compileClosure(table.getOnDoubleClick(), "it,row", location
				+ "/onDoubleClick");
	}

	private void compileTableOnModeChangeScript(TableConfig table,
			String location) {
		compileClosure(table.getOnModeChange(), "it,mode", location
				+ "/onModeChange");
	}

	private void compileTableOnRowChangeScript(TableConfig table,
			String location) {
		compileClosure(table.getOnRowChange(), "it,row,changedValues", location
				+ "/onRowChange");
	}

	private void compileRowEditableClosure(TableConfig table, String location) {
		compileClosure(table.getRowEditable(), "it,row", location
				+ "/row-editable");
		compileClosure(table.getRowDeletable(), "it,row", location
				+ "/row-deletable");
	}

	private void compileAllClosureScripts(Dynamic dynamicSelect, String location) {
		compileClosure(dynamicSelect.getOptions(), "row,columnName", location
				+ "/options");
	}

	private void compileAllClosureScripts(ContainerConfig container,
			String location) {
		compileClosure(container.getOnCommit(), location + "/onCommit");
		compileClosure(container.getOnCreate(), "it,row", location
				+ "/onCreate");
		compileClosure(container.getOnInsert(), "it,row", location
				+ "/onInsert");
		compileClosure(container.getOnDelete(), "it,row", location
				+ "/onDelete");
		compileClosure(container.getOnUpdate(), "it,row", location
				+ "/onUpdate");
		if (container instanceof DatabaseQueryConfig) {
			DatabaseQueryConfig config = (DatabaseQueryConfig) container;
			compileDatabaseQueryScripts(config, location);
		} else if (container instanceof ScriptContainerConfig) {
			ScriptContainerConfig config = (ScriptContainerConfig) container;
			compileScriptContainerScripts(config, location);
		} else if (container instanceof ReSTContainerConfig) {
			ReSTContainerConfig config = (ReSTContainerConfig) container;
			compileReSTContainerScripts(config, location);
		}
	}

	private void compileReSTContainerScripts(ReSTContainerConfig config,
			String location) {
		compileClosure(config.getQuery().getCollection(), location
				+ "/query/collection");
		compileReSTAttributeScripts(config.getQuery().getAttribute(), location
				+ "/query/attributes");
		compileGString(config.getBaseUrl(), "", location + "/baseUrl");
		compileGString(config.getQuery().getUrl(), "", location + "/query/url");
		if (config.getInsert() != null) {
			compileGString(config.getInsert().getUrl(), "row", location
					+ "/insert/url");
			compileClosure(config.getInsert().getValue(), "row", location
					+ "/insert/value");
		}
		if (config.getUpdate() != null) {
			compileGString(config.getUpdate().getUrl(), "row", location
					+ "/update/url");
			compileClosure(config.getUpdate().getValue(), "row", location
					+ "/update/value");
		}
		if (config.getDelete() != null) {
			compileGString(config.getDelete().getUrl(), "row", location
					+ "/delete/url");
		}
	}

	private void compileReSTAttributeScripts(
			List<ReSTAttributeConfig> attributes, String location) {
		for (ReSTAttributeConfig attribute : attributes) {
			if (attribute.getPath() == null
					|| attribute.getPath().getSource() == null) {
				attribute.setPath(new GroovyScript(attribute.getName()));
			}
			compileClosure(attribute.getPath(),
					location + "[" + attribute.getName() + "]");
		}
	}

	private void compileScriptContainerScripts(
			ScriptContainerConfig scriptContainerConfig, String location) {
		compileClosure(scriptContainerConfig.getDelegate(), "", location
				+ "/delegate");
	}

	private void compileDatabaseQueryScripts(DatabaseQueryConfig container,
			String location) {
		if (container.getInsert() != null) {
			// compileStatementScripts(container.getInsert(), location);
			compileStatementScriptsWithParameter(container.getInsert(),
					location, "container,row,connection",
                    "container.generateInsertStatement(row)");
		}
		if (container.getUpdate() != null) {
			// compileStatementScripts(container.getUpdate(), location);
			compileStatementScriptsWithParameter(container.getUpdate(),
					location, "container,row,connection",
                    "container.generateUpdateStatement(row)");
		}
		if (container.getDelete() != null) {
			compileStatementScriptsWithParameter(container.getDelete(),
					location, "container,row,connection",
                    "container.generateDeleteStatement(row)");
		}
	}

	private void compileStatementScriptsWithParameter(
			StatementConfig statement, String location, String parameters, String generatorScript) {
		switch (statement.getType()) {
		case SQL:
            if (Strings.isNullOrEmpty(statement.getStatement().getSource())) {
                compileClosure(statement.getStatement(), parameters, location, generatorScript, false);
            } else {
			    compileGString(statement.getStatement(), parameters, location);
            }
			break;
		case SCRIPT:
			compileClosure(statement.getStatement(), parameters, location);
			break;
		default:
			throw new UnsupportedOperationException(
					"Unbekannter Statement-Typ: " + statement.getType());
		}
	}

	private void compileAllClosureScripts(FormConfig form, String location) {
		int i = 0;
		for (FormActionConfig action : form.getAction()) {
			String actionLocation = location + "/action[" + (i++) + "]";
			compileClosure(action.getOnExecution(), actionLocation
					+ "/onExecution");
			if (action.getSearch() != null
					&& action.getSearch().getApplyFilters() != null) {
				compileAllFilterClosureScripts(action.getSearch()
						.getApplyFilters().getFilters(), actionLocation
						+ "/search/apply-filters");
			}
		}

		i = 0;
		for (FormFieldConfig formFieldConfig : form.getField()) {
			String fieldLocation = location + "/field[" + i++ + "]";
			compileClosure(formFieldConfig.getOnValueChange(), fieldLocation
					+ "/onValueChange");

			if (formFieldConfig.getSelect() != null
					&& formFieldConfig.getSelect().getDynamic() != null) {
				compileClosure(formFieldConfig.getSelect().getDynamic()
						.getOptions(), "it", fieldLocation + "/select/dynamic");
			}
		}
	}

	private void compileAllFilterClosureScripts(List<FilterConfig> filters,
			String actionLocation) {
		int j = 0;
		for (FilterConfig filter : filters) {
			String filterLocation = actionLocation + "[" + (j++) + "]";
			compileAllClosureScripts(filter, filterLocation);
		}
	}

	private void compileAllClosureScripts(FilterConfig filter, String location) {
		if (filter instanceof CustomFilterConfig) {
			compileClosure(((CustomFilterConfig) filter).getFilter(), "row",
					location + "/filter");

		} else if (filter instanceof FilterListConfig) {
			FilterListConfig filterListConfig = (FilterListConfig) filter;
			compileAllFilterClosureScripts(filterListConfig.getFilters(),
					"/filters");
		}
	}

	private void compileNoopScriptClass(PortletConfig config) {
		ScriptConfig noopScript = new ScriptConfig();
		config.getScript().add(noopScript);
		noopScript.setValue(new GroovyScript(""));
		try {
			Class<Script> script = compiler.compileScript("");
			noopScript.getValue().setClazz(script);

		} catch (ScriptingException e) {
			LOG.error("Error compiling main script", e);
			throw new BusinessException("portlet.crud.error.compilingScript",
					"/portlet/script");
		}
	}

	private boolean compileScripts(List<ScriptConfig> scripts, String location) {
		boolean hasMainScript = false;
		for (ScriptConfig config : scripts) {
			if (config.getProperty() == null) {
				hasMainScript = true;
			}

			String scriptName;
			if (config.getSrc() != null) {
				scriptName = new File(config.getSrc()).getName();
			} else {
				String name = config.getProperty();
				if (name == null) {
					name = "main";
				}
				scriptName = StringUtils.capitalize(name)
						+ "PortletScript.groovy";
			}
			compileScript(scriptName, config.getValue(), location);
		}
		return hasMainScript;
	}

	private boolean compileClosure(GroovyScript groovyScript, String location) {
		return compileClosure(groovyScript, "it", location, null, false);
	}

	private boolean compileClosure(GroovyScript groovyScript,
			String parameterNames, String location) {
		return compileClosure(groovyScript, parameterNames, location, null, false);
	}

	private boolean compileGString(GroovyScript groovyScript,
			String parameterNames, String location) {
		return compileClosure(groovyScript, parameterNames, location, null, true);
	}

	private boolean compileClosure(GroovyScript groovyScript,
			String parameterNames, String location, String defaultSource, boolean wrapSourceAsGString) {
		if (groovyScript != null && (!Strings.isNullOrEmpty(groovyScript.getSource()) || defaultSource != null)) {
			String source = groovyScript.getSource();
            if (Strings.isNullOrEmpty(source)) {
                source = defaultSource;
            }

            String code;
			if (!wrapSourceAsGString) {
				// { row -> code }
				code = "{ " + parameterNames + " -> "
						+ source + " }";
			} else {
				// { row -> """code""" }
				code = "{ " + parameterNames + " -> "
						+ multilineGStringExpression(source)
						+ " }";
			}
			try {
				Class<Script> script = compiler.compileScript(code);
				groovyScript.setClazz(script);
				return true;

			} catch (ScriptingException e) {
				LOG.error("Error compiling script at '" + location + "'", e);
				throw new BusinessException(
						"portlet.crud.error.compilingScript", location);
			}
		}
		return false;
	}

	private static String multilineGStringExpression(String sqlString) {
		return sqlString == null ? null : "\"\"\""
				+ sqlString.replace("\"\"\"", "\\\"\\\"\\\"") + "\"\"\"";
	}

	private boolean compileScript(String name, GroovyScript groovyScript,
			String location) {
		if (groovyScript != null && groovyScript.getSource() != null) {
			String source = groovyScript.getSource();
			try {
				String sourceWithLogAtEnd = source + //
						"\nlog.debug '" + name + " execution finished...'";
				Class<Script> script = compiler.compileScript(
						sourceWithLogAtEnd, name);
				groovyScript.setClazz(script);
				return true;

			} catch (ScriptingException e) {
				LOG.error("Error compiling script at '" + location + "'", e);
				throw new BusinessException(
						"portlet.crud.error.compilingScript", location);
			}
		}
		return false;
	}

}

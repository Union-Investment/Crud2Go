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
package de.unioninvestment.eai.portal.portlet.crud.config.visitor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.QueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptConfig;

/**
 * Eine Visitor Implementierung, die aus einer Konfiguration die Namen aller
 * verwendeten Datenquellen extrahiert und in einem Set bereitstellt.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DatasourceNameCollectingVisitor implements ConfigurationVisitor {

	private static final Pattern DATASOURCE_NAME_FINDER_PATTERN = Pattern
			.compile("\\bsql\\s*\\(\\s*[\\\"'](\\w+)[\\\"']\\s*\\)");

	private final Set<String> datasourceNames = new HashSet<String>();

	@Override
	public void visit(Object element) {
		if (element instanceof QueryConfig) {
			this.visitQueryConfig((QueryConfig) element);
		} else if (element instanceof DatabaseTableConfig) {
			this.visitDatabaseTableConfig((DatabaseTableConfig) element);
		} else if (element instanceof DatabaseQueryConfig) {
			this.visitDatabaseQueryConfig((DatabaseQueryConfig) element);
		} else if (element instanceof ScriptConfig) {
			this.visitScriptConfig((ScriptConfig) element);
		}
	}

	@Override
	public void visitAfter(Object element) {
		// Nothing to do!
	}

	/**
	 * @return die eingesammelten Namen der in der Konfiguration verwendeten
	 *         Datenquellen.
	 */
	public Set<String> getDatasourceNames() {
		return this.datasourceNames;
	}

	private void visitQueryConfig(QueryConfig config) {
		this.addDataSourceName(config.getDatasource());
	}

	private void visitDatabaseQueryConfig(DatabaseQueryConfig config) {
		this.addDataSourceName(config.getDatasource());
	}

	private void visitDatabaseTableConfig(DatabaseTableConfig config) {
		this.addDataSourceName(config.getDatasource());
	}

	private void addDataSourceName(String name) {
		if (name != null && !name.isEmpty()) {
			this.datasourceNames.add(name);
		}
	}

	private void visitScriptConfig(ScriptConfig config) {
		GroovyScript script = config.getValue();
		if (script != null && script.getSource() != null) {
			Matcher datasourceNameFinder = DATASOURCE_NAME_FINDER_PATTERN
					.matcher(script.getSource());
			while (datasourceNameFinder.find()) {
				this.addDataSourceName(datasourceNameFinder.group(1));
			}
		}
	}
}

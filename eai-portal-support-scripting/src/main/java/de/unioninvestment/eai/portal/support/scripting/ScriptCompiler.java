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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet;

/**
 * Support-Klasse, die die Compilierung von Strings aus der Script-Konfiguration
 * übernimmt.
 * 
 * @author carsten.mjartan
 */
public class ScriptCompiler {

	private GroovyClassLoader loader;

	/**
	 * Initialisierung des Compilers.
	 */
	public ScriptCompiler() {
		loader = createSecuredGroovyClassloader();
	}

	/**
	 * Kompiliert Strings in Scripte.
	 * 
	 * @param scriptSource
	 *            der Script-Quellcode
	 * @return die kompilierte Klasse
	 */
	@SuppressWarnings("unchecked")
	public Class<Script> compileScript(String scriptSource) {

		try {
			return loader.parseClass(scriptSource, "PortletScript.groovy");

		} catch (Exception e) {
			throw new ScriptingException(e,
					"portlet.crud.error.compilingScript");
		}

	}

	private static GroovyClassLoader createSecuredGroovyClassloader() {
		final ImportCustomizer imports = new ImportCustomizer();
		imports.addStarImports(ScriptPortlet.class.getPackage().getName());
		imports.addStarImports(Component.class.getPackage().getName());
		final SecureASTCustomizer customizer = new SecureASTCustomizer();
		customizer.setImportsBlacklist(unmodifiableList(asList(
				"java.lang.System", "groovy.lang.GroovyShell",
				"groovy.lang.GroovyClassLoader")));
		customizer.setIndirectImportCheckEnabled(true);

		CompilerConfiguration configuration = new CompilerConfiguration();
		configuration.addCompilationCustomizers(imports, customizer);

		ClassLoader parent = ScriptCompiler.class.getClassLoader();

		return new GroovyClassLoader(parent, configuration);
	}
}

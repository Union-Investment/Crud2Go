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
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;

import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;

/**
 * Erstellt und initialisiert anhand einer gegebenen Script-Konfiguration ein
 * Groovy-Script.
 * 
 * @author max.hartmann
 * 
 */
public class ScriptBuilder {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(ScriptBuilder.class);
	private Script mainScript;
	private Binding mainScriptBinding = new Binding();
	private Binding propertyScriptBinding = new Binding();

	/**
	 * Registriert das Haupt-Script.
	 * 
	 * @param script
	 *            Script aus der Konfiguration
	 */
	public void registerMainScript(ScriptConfig script) {
		mainScript = createNewInstance(script.getValue());
	}

	/**
	 * Fügt dem Groovy-Script Binding-Variablen hinzu, diese stehen dem Script
	 * dann zu Laufzeit zur Verfügung.
	 * 
	 * @param name
	 *            Name der Variable
	 * @param value
	 *            Wert der Variable
	 * 
	 * @throws ScriptingException
	 *             falls der Script-Builder nicht initialisiert wurde
	 */
	public void addBindingVariable(String name, Object value) {
		mainScriptBinding.setVariable(name, value);
		propertyScriptBinding.setVariable(name, value);
	}

    public void updateBindingsOfMainScript() {
        mainScript.setBinding(mainScriptBinding);
    }

	/**
	 * Führt das Groovy-Script aus.
	 * 
	 */
	public void runMainScript() {
		mainScript.run();
	}

	/**
	 * Baut eine {@link Closure} zu einer gegebenen PortletAction. Das Script
	 * wird der Closure als Delegate zugewiesen.
	 * 
	 * @param closureScript
	 *            das compilierte Script zur Closure
	 * @return die Action-Closure oder <code>null</code>
	 * 
	 * @see {@link Closure}
	 * @see {@link Closure#setDelegate(Object)}
	 */
	@SuppressWarnings("unchecked")
	public Closure<Object> buildClosure(GroovyScript closureScript) {
		Closure<Object> closure = null;
		if (closureScript != null && !Strings.isNullOrEmpty(closureScript.getSource())) {
			if (closureScript.getClazz() != null) {
				closure = (Closure<Object>) createNewInstance(closureScript)
						.run();
				closure.setDelegate(mainScript);
				closure.setResolveStrategy(Closure.DELEGATE_ONLY);

			} else if (closureScript.getSource() != null) {
				throw new TechnicalCrudPortletException(
						"No closure script compilation was done for script: "
								+ closureScript.getSource());
			}
		}
		return closure;
	}

	private Script createNewInstance(GroovyScript script) {
		try {
			return script.getClazz().newInstance();

		} catch (InstantiationException e) {
			LOG.error("Error instantiating main script", e);
			throw new ScriptingException(e,
					"portlet.crud.error.instantiatingMainScript");
		} catch (IllegalAccessException e) {
			LOG.error("Error instantiating main script", e);
			throw new ScriptingException(e,
					"portlet.crud.error.instantiatingMainScript");
		}
	}

	public Script getMainScript() {
		return mainScript;
	}

	public void registerAndRunPropertyScript(String property,
			GroovyScript script) {
		Script scriptInstance = createNewInstance(script);
		scriptInstance.setBinding(propertyScriptBinding);
		scriptInstance.run();
		
		mainScriptBinding.setVariable(property, scriptInstance);
	}

}

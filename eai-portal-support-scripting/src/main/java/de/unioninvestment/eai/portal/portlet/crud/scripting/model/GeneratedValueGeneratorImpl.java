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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GeneratedValueGenerator;

/**
 * Implementierung des {@link GeneratedValueGenerator}, der an eine
 * Script-Closure delegiert.
 * 
 * @author carsten.mjartan
 */
public class GeneratedValueGeneratorImpl implements GeneratedValueGenerator {

	private final Closure<Object> closure;

	/**
	 * @param generatedValueClosure
	 *            Closure, die für die Generierung verwendet werden soll
	 */
	public GeneratedValueGeneratorImpl(Closure<Object> generatedValueClosure) {
		this.closure = generatedValueClosure;
	}

	@Override
	public Object getValue(ContainerRow row) {
		ScriptRow scriptRow = new ScriptRow(row);
		return closure.call(scriptRow);
	}

}

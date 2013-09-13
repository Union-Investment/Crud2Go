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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.json.JsonBuilder;
import groovy.lang.Closure;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;

public class JsonCreator implements PayloadCreator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonCreator.class);

	private ReSTContainer container;
	private ScriptBuilder scriptBuilder;

	public JsonCreator(ReSTContainer container, ScriptBuilder scriptBuilder) {
		this.container = container;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	public String getMimeType() {
		return "application/json";
	}

	@Override
	public byte[] create(GenericItem item, GroovyScript conversionScript,
			String charset) {
		Closure<?> closure = scriptBuilder.buildClosure(conversionScript);
		ScriptRow scriptRow = scriptRow(item);
		Object data = closure.call(scriptRow);

		JsonBuilder builder = new JsonBuilder(data);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generating JSON:\n{}", builder.toPrettyString());
		}
		String json = builder.toString();

		return getBytes(json, charset);
	}

	private byte[] getBytes(String json, String charset) {
		try {
			return json.getBytes(charset);

		} catch (UnsupportedEncodingException e) {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.unknownEncoding", charset);
		}
	}

	private ScriptRow scriptRow(GenericItem item) {
		GenericContainerRowId containerRowId = new GenericContainerRowId(
				item.getId(), container.getPrimaryKeyColumns());
		GenericContainerRow containerRow = new GenericContainerRow(
				containerRowId, item, container, false, true);
		return new ScriptRow(containerRow);
	}

}

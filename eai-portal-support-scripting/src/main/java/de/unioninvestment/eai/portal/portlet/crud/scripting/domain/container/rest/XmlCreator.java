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

import groovy.lang.Closure;
import groovy.xml.MarkupBuilder;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.io.OutputStreamWriter;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;

public class XmlCreator implements PayloadCreator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XmlCreator.class);

	private ReSTContainerConfig config;
	private ReSTContainer container;
	private ScriptBuilder scriptBuilder;

	public XmlCreator(ReSTContainer container, ReSTContainerConfig config,
			ScriptBuilder scriptBuilder) {
		this.container = container;
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	public String getMimeType() {
		return "application/xml";
	}

	@Override
	public byte[] create(GenericItem item, GroovyScript conversionScript,
			String charset) {

		try {
			ByteArrayOutputStream byteStream = createBytestream(item,
					conversionScript, charset);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generating XML:\n{}",
						new String(byteStream.toByteArray(), charset));
			}
			return byteStream.toByteArray();

		} catch (UnsupportedCharsetException e) {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.unknownEncoding",
					charset);
		} catch (UnsupportedEncodingException e) {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.unknownEncoding",
					charset);
		}
	}

	private ByteArrayOutputStream createBytestream(GenericItem item,
			GroovyScript conversionScript, String charset) {

		Closure<?> closure = scriptBuilder.buildClosure(conversionScript);

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(byteStream,
				charset);
		MarkupBuilder xml = new MarkupBuilder(writer);
		closure.setDelegate(xml);

		ScriptRow scriptRow = scriptRow(item);
		closure.call(scriptRow);
		return byteStream;
	}

	private ScriptRow scriptRow(GenericItem item) {
		GenericContainerRowId containerRowId = new GenericContainerRowId(
				item.getId(), container.getPrimaryKeyColumns());
		GenericContainerRow containerRow = new GenericContainerRow(
				containerRowId, item, container, false, true);
		return new ScriptRow(containerRow);
	}

}

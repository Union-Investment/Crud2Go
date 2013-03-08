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
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

/**
 * Parser for XML.
 * 
 * @author carsten.mjartan
 */
public class XmlParser extends AbstractParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XmlParser.class);

	private ReSTContainerConfig config;
	private ScriptBuilder scriptBuilder;

	/**
	 * @param config
	 *            the ReST Configuration
	 * @param scriptBuilder
	 *            needed for Closure instantation
	 */
	public XmlParser(ReSTContainerConfig config, ScriptBuilder scriptBuilder) {
		super(config, scriptBuilder);
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	protected Object parseData(Reader reader) throws IOException {
		try {
			return new XmlSlurper().parse(reader);

		} catch (SAXException e) {
			throw new TechnicalCrudPortletException("Invalid XML", e);

		} catch (ParserConfigurationException e) {
			throw new TechnicalCrudPortletException(
					"Serious parser configuration error", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterable<?> getCollection(Object parsedData) {

		Object collection = parsedData;

		GroovyScript collectionScript = config.getQuery()
				.getCollection();
		if (collectionScript != null && collectionScript.getSource() != null) {
			Closure<?> closure = scriptBuilder.buildClosure(collectionScript);
			collection = callClosureAgainstDelegate(closure,
					parsedData);
		}
		if (collection == null) {
			LOGGER.info("ReST Collection is null, assuming empty list");
			return new ArrayList<Object[]>(0);

		} else if (collection instanceof GPathResult) {
			final GPathResult result = (GPathResult) collection;
			return new Iterable<Object>() {
				@Override
				public Iterator<Object> iterator() {
					return result.iterator();
				}
			};

		} else if (collection instanceof Iterable) {
			return (Iterable<?>) collection;

		} else {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.wrongCollectionType",
					collection.getClass().getName());
		}
	}

	@Override
	protected Object unmarshalValue(Object valueReturnedByClosure) {
		if (valueReturnedByClosure instanceof GPathResult) {
			return ((GPathResult) valueReturnedByClosure).text();
		} else {
			return valueReturnedByClosure;
		}
	}
}

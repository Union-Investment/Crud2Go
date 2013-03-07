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
package de.unioninvestment.eai.portal.portlet.crud.config.converter;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;

/**
 * Utility-Klasse die die Konvertierung zwischen XML-Konfiguration und dem
 * dazugehörigen JAXB-Modell durchführt.
 * 
 * @author carsten.mjartan
 */
public class PortletConfigurationUnmarshaller {
	private JAXBContext context;

	private Schema schema;

	/**
	 * Initialisierung des JAXB-Kontexts.
	 * 
	 * @throws JAXBException
	 *             bei Fehlern in der JAXB-Konfiguration
	 * @throws SAXException
	 *             bei Fehlern bei der Analyse des Konfigurations-Schemas
	 */
	public PortletConfigurationUnmarshaller() throws JAXBException,
			SAXException {
		if (context == null) {
			initialize();
		}
	}

	private synchronized void initialize() throws JAXBException, SAXException {
		ClassLoader classLoader = getClass().getClassLoader();

		this.context = JAXBContext.newInstance(
				"de.unioninvestment.eai.portal.portlet.crud.config",
				classLoader);

		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		InputStream is = classLoader
				.getResourceAsStream("de/unioninvestment/eai/portal/portlet/crud/crud-portlet.xsd");
		schema = factory.newSchema(new StreamSource(is));
	}

	/**
	 * Parsen, validieren und konvertieren der im stream übergebenen
	 * XML-Konfiguration.
	 * 
	 * @param stream
	 *            XML-InputStream
	 * @return das konvertierte Java-Objektmodell
	 * @throws JAXBException
	 *             bei Fehlern
	 */
	@SuppressWarnings("unchecked")
	public PortletConfig unmarshal(InputStream stream) throws JAXBException {
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		JAXBElement<PortletConfig> element = (JAXBElement<PortletConfig>) unmarshaller
				.unmarshal(stream);
		return element.getValue();
	}

}

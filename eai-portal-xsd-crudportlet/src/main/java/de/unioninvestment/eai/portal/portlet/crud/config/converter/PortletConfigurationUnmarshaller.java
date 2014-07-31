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

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Utility-Klasse die die Konvertierung zwischen XML-Konfiguration und dem
 * dazugehörigen JAXB-Modell durchführt.
 * 
 * @author carsten.mjartan
 */
public class PortletConfigurationUnmarshaller {
	private static final JAXBContext context = createContext();
	private static Schema schema = createSchema();

	/**
	 * Initialisierung des JAXB-Kontexts.
	 * 
	 * @throws JAXBException
	 *             bei Fehlern in der JAXB-Konfiguration
	 */
	private static JAXBContext createContext() {
		try {
			ClassLoader classLoader = PortletConfigurationUnmarshaller.class
					.getClassLoader();

			return JAXBContext.newInstance(
					"de.unioninvestment.eai.portal.portlet.crud.config",
					classLoader);

		} catch (JAXBException e) {
			throw new RuntimeException("Error initializing JAXB", e);
		}
	}

	/**
	 * Initialisierung des crud-portlet Schemas.
	 * 
	 * @throws SAXException
	 *             bei Fehlern bei der Analyse des Konfigurations-Schemas
	 */
	private static Schema createSchema() {
		try {
			ClassLoader classLoader = PortletConfigurationUnmarshaller.class
					.getClassLoader();

			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			InputStream is = classLoader
					.getResourceAsStream("de/unioninvestment/eai/portal/portlet/crud/crud-portlet.xsd");
			return factory.newSchema(new StreamSource(is));

		} catch (SAXException e) {
			throw new RuntimeException("Error initializing JAXB", e);
		}
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
		JAXBElement<PortletConfig> element = (JAXBElement<PortletConfig>) createUnmarshaller()
				.unmarshal(stream);
		return element.getValue();
	}

    /**
     * Parsen, validieren und konvertieren der als String übergebenen
     * XML-Konfiguration.
     *
     * @param xml
     *            XML-String
     * @return das konvertierte Java-Objektmodell
     * @throws JAXBException
     *             bei Fehlern
     */
    @SuppressWarnings("unchecked")
    public PortletConfig unmarshal(String xml) throws JAXBException {
        JAXBElement<PortletConfig> element = (JAXBElement<PortletConfig>) createUnmarshaller()
                .unmarshal(new StringReader(xml));
        return element.getValue();
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

}

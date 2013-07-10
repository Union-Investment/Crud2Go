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
package de.unioninvestment.eai.portal.portlet.crud.validation;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.vaadin.data.Validator;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
import de.unioninvestment.eai.portal.portlet.crud.config.validation.ConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.config.validation.SecurityValidationVisitor;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationProcessor;

/**
 * Validiert ein PortletPresenter Config XML gegen das Schema.
 * 
 * @author markus.bonsch
 * 
 */
public class ConfigurationUploadValidator implements Validator {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigurationUploadValidator.class);

	private final SchemaFactory factory = SchemaFactory
			.newInstance("http://www.w3.org/2001/XMLSchema");
	private javax.xml.validation.Validator xsdValidator;

	/**
	 * Generiert einen ConfigurationValidator.
	 * 
	 */
	public ConfigurationUploadValidator() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"de/unioninvestment/eai/portal/portlet/crud/crud-portlet.xsd");
		Schema schema;
		try {
			schema = factory.newSchema(new StreamSource(is));

			xsdValidator = schema.newValidator();

		} catch (SAXException e) {
			LOG.error("XSD is invalid or not found: " + e, e);
		}

	}

	@Override
	public void validate(Object value) {
		if ((value instanceof byte[]) && !isValid(value)) {
			throw new InvalidValueException(
					getMessage("portlet.crud.page.upload.invalid"));
		}
	}

	public boolean isValid(Object value) {
		try {
			xsdValidator.validate(new StreamSource(new ByteArrayInputStream(
					(byte[]) value)));

			PortletConfig portletConfig = unmarshal(value);

			validateSecurity(portletConfig);

		} catch (ConfigurationException e) {
			LOG.warn("Konfiguration ist nicht valide: " + e.getMessage());
			return false;

		} catch (Exception e) {
			LOG.warn("XSD is invalid or not found: " + e, e);
			return false;
		}

		return true;
	}

	private PortletConfig unmarshal(Object value) throws JAXBException,
			SAXException {
		return new PortletConfigurationUnmarshaller()
				.unmarshal(new ByteArrayInputStream((byte[]) value));

	}

	private void validateSecurity(PortletConfig portletConfig) {
		ConfigurationProcessor roleValidation = new ConfigurationProcessor(
				new SecurityValidationVisitor());
		roleValidation.traverse(portletConfig);
	}

}

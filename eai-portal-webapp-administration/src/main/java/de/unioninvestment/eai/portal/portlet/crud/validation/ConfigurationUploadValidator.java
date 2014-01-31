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

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
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

	@Override
	public void validate(Object value) {
		if ((value instanceof byte[]) && !isValid(value)) {
			throw new InvalidValueException(
					getMessage("portlet.crud.page.upload.invalid"));
		}
	}

	public boolean isValid(Object value) {
		try {
			PortletConfig portletConfig = validateAndUnmarshal(value);

			validateSecurity(portletConfig);

		} catch (Exception e) {
			LOG.warn("Konfiguration ist nicht valide: " + e.getMessage(), e);
			return false;
		}

		return true;
	}

	private PortletConfig validateAndUnmarshal(Object value)
			throws JAXBException {
		ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) value);
		PortletConfig portletConfig = new PortletConfigurationUnmarshaller()
				.unmarshal(stream);
		return portletConfig;
	}

	private void validateSecurity(PortletConfig portletConfig) {
		ConfigurationProcessor roleValidation = new ConfigurationProcessor(
				new SecurityValidationVisitor());
		roleValidation.traverse(portletConfig);
	}

}

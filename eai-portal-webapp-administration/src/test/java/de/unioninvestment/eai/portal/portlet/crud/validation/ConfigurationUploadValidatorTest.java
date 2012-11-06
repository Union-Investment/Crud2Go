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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;

import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;

public class ConfigurationUploadValidatorTest extends SpringPortletContextTest {

	private static ConfigurationUploadValidator configurationValidator;

	@Test
	public void shouldValidXML() throws Exception {
		configurationValidator = new ConfigurationUploadValidator();
		byte[] bytes = getXmlFromClasspath("validConfig.xml");
		boolean valid = configurationValidator.isValid(bytes);
		configurationValidator.validate(bytes);
		assertTrue(valid);
	}

	@Test(expected = InvalidValueException.class)
	public void shouldInvalidXML() throws Exception {
		configurationValidator = new ConfigurationUploadValidator();
		byte[] bytes = getXmlFromClasspath("invalidConfig.xml.txt");
		configurationValidator.validate(bytes);
	}

	private byte[] getXmlFromClasspath(String location) throws IOException {
		InputStream is = ConfigurationUploadValidatorTest.class.getClassLoader()
				.getResourceAsStream(location);
		byte[] bytes = IOUtils.toByteArray(is);
		return bytes;
	}

}

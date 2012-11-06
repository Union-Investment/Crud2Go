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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class ModelSupportTest extends ModelSupport {

	@Test
	public void shouldCreateConfigFromXMLResource() throws JAXBException {
		PortletConfig config = createConfiguration("validConfig.xml");
		assertThat(config, notNullValue());
	}

	@Test
	public void shouldCreateModelFromConfig() throws JAXBException {
		PortletConfig config = createConfiguration("validConfig.xml");
		Portlet model = createModel(config);
		assertThat(model, notNullValue());
	}

}

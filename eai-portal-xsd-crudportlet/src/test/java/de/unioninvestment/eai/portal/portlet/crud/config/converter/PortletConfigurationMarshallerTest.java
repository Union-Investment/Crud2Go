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

import de.unioninvestment.eai.portal.portlet.crud.config.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PortletConfigurationMarshallerTest {

    PortletConfig portletConfig;

    @Before
    public void setup() {
        portletConfig = new PortletConfig();
        PageConfig pageConfig = new PageConfig();
        ScriptComponentConfig scriptComponentConfig = new ScriptComponentConfig();

        portletConfig.setPage(pageConfig);
        pageConfig.getElements().add(scriptComponentConfig);
        scriptComponentConfig.setGenerator(new GroovyScript("test"));
    }

	@Test
	public void shouldProvideParsedConfig() throws JAXBException, SAXException {
		InputStream stream = this.getClass().getResourceAsStream(
				"validConfig.xml");
		PortletConfig config = new PortletConfigurationMarshaller()
				.unmarshal(stream);

		assertThat(config, notNullValue());
	}

    @Test
    public void shouldProvideMarshalledXML() throws JAXBException, SAXException {
        String xml = new PortletConfigurationMarshaller().marshal(portletConfig);
        assertThat(xml, containsString("<portlet"));
    }

    @Test
    public void shouldWrapComplexCodeInCData() throws JAXBException, SAXException {
        TableConfig tableConfig = new TableConfig();
        DatabaseQueryConfig databaseQueryConfig = new DatabaseQueryConfig();

        portletConfig.getPage().getElements().add(tableConfig);
        tableConfig.setDatabaseQuery(databaseQueryConfig);
        databaseQueryConfig.setDatasource("eai");
        databaseQueryConfig.setQuery("select a from b '<'");

        String xml = new PortletConfigurationMarshaller().marshal(portletConfig);
        assertThat(xml, containsString("<![CDATA[select a from b '<']]>"));
    }


}

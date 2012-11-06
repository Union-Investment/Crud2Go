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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import groovy.util.GroovyMBean;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JMXWrapperTest {
	private JMXWrapper jmxWrapper;

	@SuppressWarnings("rawtypes")
	private Map connectionArgs;

	@Mock
	private MBeanServerConnection mBeanServerConnectionMock;

	@Mock
	private MBeanInfo beanInfoMock;

	@Mock
	private ObjectName objectNameMock;
	
	@Mock
	private Map<String, ? extends Map<String, ? extends Object>> ergebnisMock;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws MalformedObjectNameException,
			NullPointerException, InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException {
		MockitoAnnotations.initMocks(this);

		connectionArgs = new LinkedHashMap();
		connectionArgs.put("port", 9003);
		connectionArgs.put("host", "localhost");

		jmxWrapper = new JMXWrapper(connectionArgs);

	}

	@Test
	public void shouldReturnGroovyMBean() throws MalformedURLException,
			IOException, JMException {

		jmxWrapper.setConnection(mBeanServerConnectionMock);
		ObjectName objectName = new ObjectName(
				"Catalina:type=DataSource,class=javax.sql.DataSource,name=\"jdbc/eaiDs\"");
		when(mBeanServerConnectionMock.getMBeanInfo(objectName)).thenReturn(
				beanInfoMock);
		when(beanInfoMock.getOperations()).thenReturn(
				(new MBeanOperationInfo[] {}));

		/**
		 * 
		 */

		GroovyMBean groovyMBean = jmxWrapper
				.proxyFor("Catalina:type=DataSource,class=javax.sql.DataSource,name=\"jdbc/eaiDs\"");

		assertThat(groovyMBean, is(notNullValue()));
	}

	@Test
	public void shouldReturnMBeanPropertyMap() throws NullPointerException,
			IOException, JMException {

		jmxWrapper.setConnection(mBeanServerConnectionMock);
		ObjectName objectName = new ObjectName(
				"Catalina:type=DataSource,class=javax.sql.DataSource,name=\"jdbc/eaiDs\"");
		when(mBeanServerConnectionMock.getMBeanInfo(objectName)).thenReturn(
				beanInfoMock);
		when(beanInfoMock.getOperations()).thenReturn(
				(new MBeanOperationInfo[] {}));

		when(
				mBeanServerConnectionMock.queryNames(any(ObjectName.class),
						any(QueryExp.class))).thenReturn(
				singleton(objectNameMock));

		when(objectNameMock.getCanonicalName()).thenReturn("cannonicalName");

		when(mBeanServerConnectionMock.getAttribute(objectNameMock, "password"))
				.thenReturn("hideMeNextTime!");

		List<String> properties = new ArrayList<String>();
		properties.add("password");

		Map<String, ? extends Map<String, ? extends Object>> result = jmxWrapper
				.query("Catalina:type=DataSource,class=javax.sql.DataSource,name=\"*\",*",
						properties);

		assertThat(result.containsKey("cannonicalName"), is(true));
		assertThat(result.size(), is(1));

		Map<String, Object> attributes = (Map<String, Object>) result.get("cannonicalName");
		assertThat(attributes.containsKey("password"), is(true));
		assertThat(attributes.size(), is(1));
		assertThat(attributes.get("password"), is((Object) "hideMeNextTime!"));

		// for (Map.Entry<String, Map<String, Object>> entry :
		// result.entrySet()) {
		// System.out.println(entry.getKey());
		//
		// Map<String, Object> attributes = entry.getValue();
		// for (Map.Entry<String, Object> attributesList : attributes
		// .entrySet()) {
		//
		// System.out.println("   " + attributesList.getKey() + " : "
		// + attributesList.getValue());
		// }
		// System.out.println();
		// }
	}

	@Test
	public void shouldReturnEmptyResult() throws IOException, JMException {
		jmxWrapper = new JMXWrapper((String) null);

		Map<String, ? extends Map<String, ? extends Object>> result = jmxWrapper.query(null,
				new ArrayList<String>());

		assertThat(result.size(), is(0));
	}

	@Test
	public void shouldInvokeRemoteQuery() throws IOException, JMException {
		jmxWrapper = new JMXWrapper(mBeanServerConnectionMock);
		
		ObjectName objectName = new ObjectName("test", "service", "VALUE");
		jmxWrapper.setRemoteScriptingMBeanName(objectName);

		List<String> properties = asList("Name");
		List<String> getterScripts = asList("return 'TestName'");
		
		when(mBeanServerConnectionMock.invoke(objectName, "query", new Object[] {"test:service=DUMMY", properties, getterScripts}, new String[] { "java.lang.String","java.util.List", "java.util.List" }))
			.thenReturn(ergebnisMock);
		
		Map<String, ? extends Map<String, ? extends Object>> result = jmxWrapper.query("test:service=DUMMY",
				properties, getterScripts);
		
		assertTrue(result == ergebnisMock);
	}
}

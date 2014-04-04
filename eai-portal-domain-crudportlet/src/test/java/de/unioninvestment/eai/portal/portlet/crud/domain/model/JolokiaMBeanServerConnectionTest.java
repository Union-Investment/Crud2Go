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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JolokiaMBeanServerConnectionTest {

	private static String TEST_URL = "http://host:port/jolokia";

	private static String TEST_RESPONSE_ATTR = "{\"timestamp\":1396507601,\"status\":200,"
			+ "\"request\":{\"mbean\":\"org.apache.servicemix:ContainerName=ServiceMix,"
			+ "Name=algo-benchmark-targetadapter-sa,Type=ServiceAssembly\","
			+ "\"attribute\":\"currentState\",\"type\":\"read\"},"
			+ "\"value\":\"Shutdown\"}";

	private static String TEST_OBJECT_NAME_ATTR = "org.apache.servicemix:ContainerName="
			+ "ServiceMix,Name=algo-benchmark-targetadapter-sa,Type=ServiceAssembly";

	private static String TEST_RESPONSE_NAMES = "{\"timestamp\":1396514422,\"status\":200,"
			+ "\"request\":{\"mbean\":\"org.apache.servicemix:ContainerName=ServiceMix,"
			+ "Type=ServiceAssembly,*\",\"type\":\"read\"},\"value\":{\"org.apache.servicemix:"
			+ "ContainerName=ServiceMix,Name=algo-benchmark-targetadapter-sa,Type="
			+ "ServiceAssembly\":{\"description\":\"Osiris :: Investment :: ibk :: "
			+ "algo-benchmark-targetadapter-sa :: SA\",\"name\":\"algo-benchmark-targetadapter-sa\","
			+ "\"currentState\":\"Shutdown\",\"serviceUnits\":[{\"objectName\":"
			+ "\"org.apache.servicemix:ContainerName=ServiceMix,Name=algo-benchmark-targetadapter"
			+ "-jms-su,SubType=servicemix-jms,Type=ServiceUnit\"},{\"objectName\":"
			+ "\"org.apache.servicemix:ContainerName=ServiceMix,Name=algo-benchmark-targetadapter-rdbms-su,"
			+ "SubType=servicemix-rdbms,Type=ServiceUnit\"}]}}}";

	private static String TEST_OBJECT_NAME_NAMES = "org.apache.servicemix:ContainerName="
			+ "ServiceMix,Type=ServiceAssembly,*";

	private static String REMOTE_SCRIPTING_MBEAN_NAME = "crud:service=CrudRemoteScript,name=script";
	private static String TEST_RESPONSE_REMOTE_SCRIPTING = "{\"timestamp\":1396524103,\"status\":200,"
			+ "\"request\":{\"mbean\":\"crud:name=script,service=CrudRemoteScript\",\"type\":\"search\"},"
			+ "\"value\":[\"crud:name=script,service=CrudRemoteScript\"]}";

	private static String TEST_RESPONSE_IVOKE = "{\"timestamp\":1396537169,\"status\":200,\"request\":"
			+ "{\"mbean\":\"crud:name=script,service=CrudRemoteScript\",\"operation\":\"query(java.lang.String,"
			+ "java.util.List,java.util.List)\",\"arguments\":[\"org.apache.servicemix:ContainerName=ServiceMix,"
			+ "Type=ServiceAssembly,*\",\"[currentState]\",\"[]\"],\"type\":\"exec\"},\"value\":"
			+ "{\"org.apache.servicemix:ContainerName=ServiceMix,Name=algo-benchmark-targetadapter-sa,"
			+ "Type=ServiceAssembly\":{\"currentState\":\"Shutdown\"},\"org.apache.servicemix:ContainerName="
			+ "ServiceMix,Name=ackv-kursziehung-order-sourceadaptor-sa,Type=ServiceAssembly\":{\"currentState\":"
			+ "\"Shutdown\"},\"org.apache.servicemix:ContainerName=ServiceMix,Name=crims-financialratio-targetadaptor-sa,"
			+ "Type=ServiceAssembly\":{\"currentState\":\"Shutdown\"}}}";

	private static String TEST_RESULT_IVOKE = "{\"org.apache.servicemix:ContainerName=ServiceMix,"
			+ "Name=algo-benchmark-targetadapter-sa,Type=ServiceAssembly\":{\"currentState\":\"Shutdown\"},"
			+ "\"org.apache.servicemix:ContainerName=ServiceMix,Name=ackv-kursziehung-order-sourceadaptor-sa,"
			+ "Type=ServiceAssembly\":{\"currentState\":\"Shutdown\"},\"org.apache.servicemix:ContainerName="
			+ "ServiceMix,Name=crims-financialratio-targetadaptor-sa,Type=ServiceAssembly\":{\"currentState\":"
			+ "\"Shutdown\"}}";

	@Mock
	private JolokiaMBeanServerConnection jolokiaMBeanServerConnectionMock;

	@Mock
	private J4pClient j4pClientMock;

	@Mock
	private HttpClient httpClientMock;

	@Mock
	private HttpResponse httpResponseMock;

	@Mock
	private HttpEntity httpEntityMock;

	@Before
	public void setUp() throws MalformedObjectNameException,
			NullPointerException, InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetAttribute() throws IOException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, J4pException,
			MalformedObjectNameException, ParseException {

		ObjectName objectName = new ObjectName(TEST_OBJECT_NAME_ATTR);

		JolokiaMBeanServerConnection jolokiaMBeanServerConnection = new JolokiaMBeanServerConnection();
		jolokiaMBeanServerConnection.setJ4pClient(j4pClientMock);

		J4pReadRequest request = new J4pReadRequest(objectName, "currentState");
		J4pResponse<J4pReadRequest> response = new J4pResponse<J4pReadRequest>(
				request,
				(JSONObject) new JSONParser().parse(TEST_RESPONSE_ATTR)) {
		};

		when(j4pClientMock.execute(any(J4pReadRequest.class))).thenReturn(
				response);

		assertTrue("Shutdown".equals(jolokiaMBeanServerConnection.getAttribute(
				objectName, "currentState")));
	}

	@Test
	public void shouldQueryNames() throws IOException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, J4pException,
			MalformedObjectNameException, ParseException {

		ObjectName objectName = new ObjectName(TEST_OBJECT_NAME_NAMES);

		JolokiaMBeanServerConnection jolokiaMBeanServerConnection = new JolokiaMBeanServerConnection();
		jolokiaMBeanServerConnection.setJ4pClient(j4pClientMock);

		J4pReadRequest request = new J4pReadRequest(objectName);
		J4pResponse<J4pReadRequest> response = new J4pResponse<J4pReadRequest>(
				request,
				(JSONObject) new JSONParser().parse(TEST_RESPONSE_NAMES)) {
		};

		when(j4pClientMock.execute(any(J4pReadRequest.class))).thenReturn(
				response);

		Set<ObjectName> result = jolokiaMBeanServerConnection.queryNames(
				objectName, null);

		assertTrue(result.size() == 1);
		assertTrue(result
				.contains(new ObjectName(
						"org.apache.servicemix:ContainerName=ServiceMix,"
								+ "Name=algo-benchmark-targetadapter-sa,Type=ServiceAssembly")));
	}

	@Test
	public void shouldGetObjectNames() throws IOException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, J4pException,
			MalformedObjectNameException, ParseException {

		final ByteArrayInputStream bis = new ByteArrayInputStream(
				TEST_RESPONSE_REMOTE_SCRIPTING.getBytes());

		when(httpClientMock.execute(any(HttpUriRequest.class))).thenReturn(
				httpResponseMock);
		when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
		when(httpEntityMock.getContent()).thenReturn(bis);

		JolokiaMBeanServerConnection jolokiaMBeanServerConnection = new JolokiaMBeanServerConnection();
		jolokiaMBeanServerConnection.setJ4pClient(new J4pClient(TEST_URL,
				httpClientMock));
		ObjectName objectName = new ObjectName(REMOTE_SCRIPTING_MBEAN_NAME);

		assertTrue(jolokiaMBeanServerConnection.isRegistered(objectName));
	}

	@Test
	public void shouldInvoke() throws IOException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			J4pException, MalformedObjectNameException, ParseException {

		ArrayList<String> currentState = new ArrayList<String>();
		currentState.add("currentState");

		Object[] params = new Object[] {
				"org.apache.servicemix:ContainerName=ServiceMix,Type=ServiceAssembly,*",
				currentState, new ArrayList<String>() };
		String[] signature = new String[] { "java.lang.String",
				"java.util.List", "java.util.List" };

		ObjectName objectName = new ObjectName(REMOTE_SCRIPTING_MBEAN_NAME);

		JolokiaMBeanServerConnection jolokiaMBeanServerConnection = new JolokiaMBeanServerConnection();
		jolokiaMBeanServerConnection.setJ4pClient(j4pClientMock);

		J4pExecRequest request = new J4pExecRequest(
				REMOTE_SCRIPTING_MBEAN_NAME,
				"query(java.lang.String,java.util.List,java.util.List)", params);

		J4pResponse<J4pExecRequest> response = new J4pResponse<J4pExecRequest>(
				request,
				(JSONObject) new JSONParser().parse(TEST_RESPONSE_IVOKE)) {
		};

		when(j4pClientMock.execute(any(J4pExecRequest.class))).thenReturn(
				response);

		@SuppressWarnings("rawtypes")
		Map result = (Map) jolokiaMBeanServerConnection.invoke(objectName,
				"query", params, signature);

		assertTrue(result.size() == 3);
		assertTrue(TEST_RESULT_IVOKE.equals(result.toString()));
	}

}

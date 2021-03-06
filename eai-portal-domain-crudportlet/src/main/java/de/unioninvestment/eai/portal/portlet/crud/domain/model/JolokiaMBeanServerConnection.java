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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pListRequest;
import org.jolokia.client.request.J4pListResponse;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pSearchRequest;
import org.jolokia.client.request.J4pSearchResponse;
import org.jolokia.client.request.J4pWriteRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Jolokia-Verbindung. Stellt Operationen zu Kommunikation mit dem Jolokia-Agent
 * zur Verfügung.
 * 
 * @author polina.vinarski
 * 
 */
public class JolokiaMBeanServerConnection implements MBeanServerConnection {

	private J4pClient j4pClient;

	/**
	 * Setzt den Jolokia-Client fest.
	 * 
	 * @param j4pClient
	 *            Jolokia-Client
	 */
	public void setJ4pClient(J4pClient j4pClient) {
		this.j4pClient = j4pClient;
	}

	/**
	 * Gibt den Jolokia-Client zurück.
	 * 
	 * @return Jolokia-Client
	 */
	public J4pClient getJ4pClient() {
		return this.j4pClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAttribute(ObjectName name, String attribute)
			throws MBeanException, AttributeNotFoundException,
			InstanceNotFoundException, ReflectionException, IOException {
		J4pReadRequest j4pReadRequest = new J4pReadRequest(name, attribute);
		try {
			return j4pClient.execute(j4pReadRequest).getValue();
		} catch (J4pException e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(ObjectName name, Attribute attribute)
			throws InstanceNotFoundException, AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException,
			ReflectionException, IOException {
		J4pWriteRequest j4pWriteRequest = new J4pWriteRequest(name,
				attribute.getName(), attribute.getValue(), null);
		try {
			j4pClient.execute(j4pWriteRequest);
		} catch (J4pException e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(ObjectName name, String operationName,
			Object[] params, String[] signature)
			throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		StringBuffer pOperation = new StringBuffer();
		pOperation.append(operationName).append("(")
				.append(getArrayForArgument(signature)).append(")");

		Object[] args = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = serializeArgumentToJson(params[i]).toString();
		}

		J4pExecRequest j4pExecRequest = new J4pExecRequest(name,
				pOperation.toString(), args);

		try {
			return j4pClient.execute(j4pExecRequest).getValue();
		} catch (J4pException e) {
			throw new MBeanException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRegistered(ObjectName name) throws IOException {
		try {
			J4pSearchRequest j4pSearchRequest = new J4pSearchRequest(
					name.toString());
			J4pSearchResponse j4pSearchResponse = j4pClient
					.execute(j4pSearchRequest);
			return !j4pSearchResponse.getObjectNames().isEmpty();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ObjectName> queryNames(ObjectName name, QueryExp query)
			throws IOException {
		J4pReadRequest j4pReadRequest = new J4pReadRequest(name);
		try {
			Map<String, Map<String, Object>> response = Collections.emptyMap();
			Set<ObjectName> objectNames = new HashSet<ObjectName>();
			response = j4pClient.execute(j4pReadRequest).getValue();
			for (String responseKey : response.keySet()) {
				ObjectName objectName = new ObjectName(responseKey);
				objectNames.add(objectName);
			}
			return objectNames;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MBeanInfo getMBeanInfo(ObjectName name)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		try {
			J4pListRequest j4pListRequest = new J4pListRequest(name);
			J4pListResponse j4pListResponse = j4pClient.execute(j4pListRequest);
			@SuppressWarnings("unchecked")
			Map<String, Object> value = (Map<String, Object>) j4pListResponse
					.getValue();
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> attrMap = (Map<String, Map<String, Object>>) value
					.get("attr") == null ? new HashMap<String, Map<String, Object>>()
					: (Map<String, Map<String, Object>>) value.get("attr");
			// 1.MBeanAttributeInfo
			List<MBeanAttributeInfo> aInfos = new ArrayList<MBeanAttributeInfo>();
			Set<String> attrNameSet = attrMap.keySet();
			for (String attrName : attrNameSet) {
				MBeanAttributeInfo info = new MBeanAttributeInfo(attrName,
						(String) attrMap.get(attrName).get("type"),
						(String) attrMap.get(attrName).get("desc"),
						(Boolean) attrMap.get(attrName).get("rw"),
						(Boolean) attrMap.get(attrName).get("rw"), false);
				aInfos.add(info);
			}

			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> opMap = (Map<String, Map<String, Object>>) value
					.get("op") == null ? new HashMap<String, Map<String, Object>>()
					: (Map<String, Map<String, Object>>) value.get("op");
			// 2.MBeanOperationInfo
			List<MBeanOperationInfo> oInfos = new ArrayList<MBeanOperationInfo>();
			Set<String> opNameSet = opMap.keySet();
			for (String opName : opNameSet) {
				if (opMap.get(opName) instanceof Map) {
					MBeanOperationInfo info = mapMBeanOperationInfo(
							opMap.get(opName), opName);
					oInfos.add(info);
				} else if (opMap.get(opName) instanceof JSONArray) {
					for (int i = 0; i < ((JSONArray) opMap.get(opName)).size(); i++) {
						if (((JSONArray) opMap.get(opName)).get(i) instanceof Map) {
							@SuppressWarnings("unchecked")
							MBeanOperationInfo info = mapMBeanOperationInfo(
									(Map<String, Object>) ((JSONArray) opMap.get(opName))
											.get(i), opName);
							oInfos.add(info);
						}
					}
				}
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> notMap = (Map<String, Object>) value.get("not") == null ? new HashMap<String, Object>()
					: (Map<String, Object>) value.get("not");
			// 3.MBeanNotificationInfo
			List<MBeanNotificationInfo> nInfos = new ArrayList<MBeanNotificationInfo>();

			if (!notMap.isEmpty()) {
				@SuppressWarnings("unchecked")
				MBeanNotificationInfo nInfo = new MBeanNotificationInfo(
						(String[]) ((List<String>) notMap.get("types") == null ? new ArrayList<String>()
								: (List<String>) notMap.get("types")).toArray(new String[((List<String>) notMap
								.get("types")).size()]),
						(String) notMap.get("name"),
						(String) notMap.get("desc"));
				nInfos.add(nInfo);
			}

			MBeanInfo mBeanInfo = new MBeanInfo(name.toString(),
					(String) value.get("desc"),
					aInfos.toArray(new MBeanAttributeInfo[aInfos.size()]),
					null,
					oInfos.toArray(new MBeanOperationInfo[oInfos.size()]),
					nInfos.toArray(new MBeanNotificationInfo[nInfos.size()]));
			return mBeanInfo;
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	private MBeanOperationInfo mapMBeanOperationInfo(
			Map<String, Object> opItem, String opName) {
		@SuppressWarnings("unchecked")
		List<Map<String, String>> paramList = (List<Map<String, String>>) opItem
				.get("args");
		List<MBeanParameterInfo> params = new ArrayList<MBeanParameterInfo>();
		for (Map<String, String> paramMap : paramList) {
			MBeanParameterInfo param = new MBeanParameterInfo(
					(String) paramMap.get("name"),
					(String) paramMap.get("type"),
					(String) paramMap.get("desc"));
			params.add(param);
		}
		MBeanOperationInfo info = new MBeanOperationInfo(opName,
				(String) opItem.get("desc"),
				params.toArray(new MBeanParameterInfo[params.size()]),
				(String) opItem.get("ret"), 0);
		return info;
	}

	/**
	 * Übernommen aus Jolokia, da keine direkte Verwendung möglich
	 * 
	 * @param pArg
	 *            Parameter
	 * @return Parameter als JSON serialisiert
	 */
	protected Object serializeArgumentToJson(Object pArg) {
		if (pArg == null) {
			return null;
		} else if (pArg instanceof JSONAware) {
			return pArg;
		} else if (pArg.getClass().isArray()) {
			return serializeArray(pArg);
		} else if (pArg instanceof Map) {
			return serializeMap((Map) pArg);
		} else if (pArg instanceof Collection) {
			return serializeCollection((Collection) pArg);
		} else {
			return pArg instanceof Number || pArg instanceof Boolean ? pArg
					: pArg.toString();
		}
	}

	private Object serializeCollection(Collection pArg) {
		JSONArray array = new JSONArray();
		for (Object value : ((Collection) pArg)) {
			array.add(serializeArgumentToJson(value));
		}
		return array;
	}

	private Object serializeMap(Map pArg) {
		JSONObject map = new JSONObject();
		for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) pArg)
				.entrySet()) {
			map.put(entry.getKey(), serializeArgumentToJson(entry.getValue()));
		}
		return map;
	}

	private Object serializeArray(Object pArg) {
		JSONArray innerArray = new JSONArray();
		for (int i = 0; i < Array.getLength(pArg); i++) {
			innerArray.add(serializeArgumentToJson(Array.get(pArg, i)));
		}
		return innerArray;
	}

	private String getArrayForArgument(Object[] pArg) {
		StringBuilder inner = new StringBuilder();
		for (int i = 0; i < pArg.length; i++) {
			inner.append(nullEscape(pArg[i]));
			if (i < pArg.length - 1) {
				inner.append(",");
			}
		}
		return inner.toString();
	}

	private String nullEscape(Object pArg) {
		if (pArg == null) {
			return "[null]";
		} else if (pArg instanceof String && ((String) pArg).length() == 0) {
			return "\"\"";
		} else if (pArg instanceof JSONAware) {
			return ((JSONAware) pArg).toJSONString();
		} else {
			return pArg.toString();
		}
	}

	// Diese Methoden werden aktuell nicht benötigt

	@Override
	public void addNotificationListener(ObjectName name,
			NotificationListener listener, NotificationFilter filter,
			Object handback) throws InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void addNotificationListener(ObjectName name, ObjectName listener,
			NotificationFilter filter, Object handback)
			throws InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public ObjectInstance createMBean(String className, ObjectName name)
			throws ReflectionException, InstanceAlreadyExistsException,
			MBeanRegistrationException, MBeanException,
			NotCompliantMBeanException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public ObjectInstance createMBean(String className, ObjectName name,
			ObjectName loaderName) throws ReflectionException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			MBeanException, NotCompliantMBeanException,
			InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public ObjectInstance createMBean(String className, ObjectName name,
			Object[] params, String[] signature) throws ReflectionException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			MBeanException, NotCompliantMBeanException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public ObjectInstance createMBean(String className, ObjectName name,
			ObjectName loaderName, Object[] params, String[] signature)
			throws ReflectionException, InstanceAlreadyExistsException,
			MBeanRegistrationException, MBeanException,
			NotCompliantMBeanException, InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public AttributeList getAttributes(ObjectName name, String[] attributes)
			throws InstanceNotFoundException, ReflectionException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public String getDefaultDomain() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public String[] getDomains() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public Integer getMBeanCount() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public ObjectInstance getObjectInstance(ObjectName name)
			throws InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public boolean isInstanceOf(ObjectName name, String className)
			throws InstanceNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query)
			throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeNotificationListener(ObjectName name, ObjectName listener)
			throws InstanceNotFoundException, ListenerNotFoundException,
			IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeNotificationListener(ObjectName name,
			NotificationListener listener) throws InstanceNotFoundException,
			ListenerNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeNotificationListener(ObjectName name,
			ObjectName listener, NotificationFilter filter, Object handback)
			throws InstanceNotFoundException, ListenerNotFoundException,
			IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void removeNotificationListener(ObjectName name,
			NotificationListener listener, NotificationFilter filter,
			Object handback) throws InstanceNotFoundException,
			ListenerNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public AttributeList setAttributes(ObjectName name, AttributeList attributes)
			throws InstanceNotFoundException, ReflectionException, IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public void unregisterMBean(ObjectName name)
			throws InstanceNotFoundException, MBeanRegistrationException,
			IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

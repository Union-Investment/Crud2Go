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
package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.util.GroovyMBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.MalformedObjectNameException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig.Attribute;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.JMXWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.TestUser;
import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.container.UpdateContext;

public class JmxDelegateTest {
	@Mock
	private JmxContainerConfig jmxContainerConfigMock;

	@Mock
	private JMXWrapper jmxWrapperMock;

	@Mock
	private GenericItem itemMock;

	@Mock
	private GenericProperty propertyMock;

	@Mock
	private GroovyMBean gMbeanMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnNotNullMetaData()
			throws MalformedObjectNameException, NullPointerException {
		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));
		MetaData metaData = jmxDelegate.getMetaData();
		assertThat(metaData, notNullValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnMBeanObjectNameAsFirstColumn() {
		when(jmxContainerConfigMock.getAttribute()).thenReturn(
				createAttributeList());

		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));
		MetaData metaData = jmxDelegate.getMetaData();

		Column column = metaData.getColumns().get(0);
		assertThat(column.getName(), is("mbeanObjectName"));
		assertThat((Class<String>) column.getType(), equalTo(String.class));
		assertThat(column.isReadOnly(), is(true));
		assertThat(column.isRequired(), is(true));
		assertThat(column.isPartOfPrimaryKey(), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnMetaData() {
		when(jmxContainerConfigMock.getAttribute()).thenReturn(
				createAttributeList());

		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));
		MetaData metaData = jmxDelegate.getMetaData();

		Column column = metaData.getColumns().get(1);
		assertThat(column.getName(), is("name1"));
		assertThat((Class<String>) column.getType(), equalTo(String.class));
		assertThat(column.isReadOnly(), is(true));
		assertThat(column.isRequired(), is(true));
		assertThat(column.isPartOfPrimaryKey(), is(false));
	}

	@Test
	public void shouldReturnOneRow() throws IOException, JMException {

		when(jmxContainerConfigMock.getAttribute()).thenReturn(
				createAttributeList());

		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));
		jmxDelegate.setQuery("query");
		jmxDelegate.setJmxWrapper(jmxWrapperMock);

		final Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		result.put("objectName", new HashMap<String, Object>());
		when(
				jmxWrapperMock.query("query", asList("name1"),
						asList((String) null)))
				.thenAnswer(new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return result;
					}

				});

		List<Object[]> rows = jmxDelegate.getRows();

		assertThat(rows, notNullValue());
		assertThat(rows.size(), is(1));
	}

	@Test
	public void shouldReturnOneRowWithGetterScripts() throws IOException,
			JMException {

		List<Attribute> createAttributeList = createAttributeList();
		when(jmxContainerConfigMock.getAttribute()).thenReturn(
				createAttributeList);
		createAttributeList.get(0).setServerSideGetter("return 'Test123'");

		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));
		jmxDelegate.setQuery("query");
		jmxDelegate.setJmxWrapper(jmxWrapperMock);

		final Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		result.put("objectName", new HashMap<String, Object>());
		when(
				jmxWrapperMock.query("query", asList("name1"),
						asList("return 'Test123'")))
				.thenAnswer(new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return result;
					}

				});

		List<Object[]> rows = jmxDelegate.getRows();

		assertThat(rows, notNullValue());
		assertThat(rows.size(), is(1));
	}

	@Test
	public void shouldUpdateAttribute() throws JMException, IOException {
		JmxDelegate jmxDelegate = new JmxDelegate(jmxContainerConfigMock,
				jmxWrapperMock, new TestUser("testUser"));

		when(itemMock.getItemPropertyIds()).thenAnswer(
				new Answer<Collection<?>>() {

					@Override
					public Collection<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return asList("attribute1");
					}
				});
		when(itemMock.getId()).thenReturn(
				new GenericItemId(new Object[] { "4711" }));
		when(itemMock.getItemProperty("attribute1")).thenReturn(propertyMock);
		when(propertyMock.isModified()).thenReturn(true);
		when(propertyMock.getValue()).thenReturn("1");
		when(jmxWrapperMock.proxyFor("4711")).thenReturn(gMbeanMock);

		jmxDelegate.update(asList(itemMock), new UpdateContext());

		verify(gMbeanMock).setProperty("attribute1", "1");
	}

	private List<Attribute> createAttributeList() {
		List<Attribute> attributsliste = new ArrayList<JmxContainerConfig.Attribute>();
		Attribute attribute1 = new Attribute();
		attribute1.setName("name1");
		attribute1.setReadonly(true);
		attribute1.setRequired(true);
		attribute1.setType(String.class);
		attributsliste.add(attribute1);

		return attributsliste;
	}

	@Test
	public void testExtractMetadataFromConfig()
	{

	}

	@Test
	public void getRows() {
	
	}
}

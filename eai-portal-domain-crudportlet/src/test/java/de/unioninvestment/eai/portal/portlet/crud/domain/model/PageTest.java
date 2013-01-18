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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;

public class PageTest {

	@Mock
	PageConfig configMock;

	private Page page;

	@Mock
	private Form form;

	@Mock
	private Form form2;

	@Mock
	private Table table;

	@Mock
	private Portlet portletMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		page = new Page(configMock);
	}

	@Test
	public void shouldProvidePortletReference() {
		page.setPortlet(portletMock);
		assertThat(page.getPortlet(), is(portletMock));
	}

	@Test
	public void shouldReturnPageElements() {
		page.addComponent(form);
		page.addComponent(table);
		assertThat(page.getElements(), is(asList((Component) form, table)));
	}

	@Test
	public void shouldReturnFollowingTable() {
		page.addComponent(form);
		page.addComponent(table);
		assertThat(page.findNextElement(Table.class, form), equalTo(table));
	}

	@Test
	public void shouldReturnNullIfFormIsLastElement() {
		page.addComponent(form);
		assertThat(page.findNextElement(Table.class, form), nullValue());
	}

	@Test
	public void shouldReturnNullIfNoFollowingTableFound() {
		page.addComponent(form);
		page.addComponent(form2);
		assertThat(page.findNextElement(Table.class, form), nullValue());
	}

	@Test
	public void shouldNotUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(false);
		assertThat(page.isHorizontalLayout(), is(false));
	}

	@Test
	public void shoutUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(true);
		assertThat(page.isHorizontalLayout(), is(true));
	}
}

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.support.QueryOptionListRepository;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.context.ContextProvider;


public class PortletCachingTest {

	@Mock
	private QueryOptionListRepository repositoryMock;
	
	@Mock
	private ContextProvider contextProviderMock;

	private PortletCaching caching;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Context.setProvider(contextProviderMock);
		when(contextProviderMock.getBean(QueryOptionListRepository.class)).thenReturn(repositoryMock);
		caching = new PortletCaching();
	}
	
	@After
	public void cleanup() {
		Context.setProvider(null);
	}
	
	@Test
	public void shouldDelegateInvalidateQueryOptionList() {
		when(repositoryMock.remove("test", "select * from TESTTABLE")).thenReturn(true);
		assertThat(caching.invalidateQueryOptionList("test", "select * from TESTTABLE"), is(true));
	}
	
	@Test
	public void shouldDelegateInvalidateQueryOptionLists() {
		Pattern pattern = Pattern.compile("TESTTABLE");
		when(repositoryMock.removeAll("test", pattern)).thenReturn(4);
		assertThat(caching.invalidateQueryOptionLists("test", pattern), is(4));
	}
	
}

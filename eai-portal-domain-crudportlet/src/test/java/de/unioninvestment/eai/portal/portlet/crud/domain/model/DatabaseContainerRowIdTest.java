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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.sqlcontainer.RowId;

public class DatabaseContainerRowIdTest {

	@Mock
	private RowId rowIdMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldProvidePrimaryKeyValuesAsMap() {
		when(rowIdMock.getId()).thenReturn(new Object[] { 1, 2 });

		ContainerRowId containerRowId = new DatabaseContainerRowId(rowIdMock,
				asList("A", "B"));
		assertThat(containerRowId.asMap().size(), is(2));
		assertThat(containerRowId.asMap().get("A"), is((Object) 1));
		assertThat(containerRowId.asMap().get("B"), is((Object) 2));
	}

	@Test
	public void shouldAcceptEmptyPrimaryKey() {
		when(rowIdMock.getId()).thenReturn(null);

		ContainerRowId containerRowId = new DatabaseContainerRowId(rowIdMock,
				new LinkedList<String>());
		assertThat(containerRowId.asMap(), nullValue());
	}

}

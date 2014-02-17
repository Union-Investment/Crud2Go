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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.GeneratedValueGenerator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ValuesRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItemValuesRow;

public class GeneratedColumnsStreamItemWrapperTest {

	private GeneratedColumnsStreamItemWrapper wrapper;

	@Mock
	private TableColumns columnsMock;
	@Mock
	private StreamItem delegateMock;

	@Mock
	private TableColumn columnMockA, columnMockB;

	@Mock
	private GeneratedValueGenerator generatorMock;

	@Captor
	private ArgumentCaptor<ValuesRow> valuesRowCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		wrapper = new GeneratedColumnsStreamItemWrapper(delegateMock, asList(
				"A", "B"), columnsMock);
		when(columnsMock.get("A")).thenReturn(columnMockA);
		when(columnsMock.get("B")).thenReturn(columnMockB);
	}

	@Test
	public void shouldFetchContainerPropertiesFromDelegate() {
		when(delegateMock.getValue("A")).thenReturn("4711");
		assertThat(wrapper.getValue("A"),  is((Object)"4711"));
	}

	@Test
	public void shouldFetchAllPropertiesFromDelegateWithoutColumnMetaData() {
		wrapper = new GeneratedColumnsStreamItemWrapper(delegateMock, asList(
				"A", "B"), null);
		when(delegateMock.getValue("A")).thenReturn("4711");
		assertThat(wrapper.getValue("A"),  is((Object)"4711"));
	}
	
	@Test
	public void shouldFetchGeneratedValuesFromColumnValueGenerator() {
		when(columnMockA.isGenerated()).thenReturn(true);
		when(columnMockA.getGeneratedValueGenerator()).thenReturn(generatorMock);
		when(generatorMock.getValue(valuesRowCaptor.capture())).thenReturn("generated");
		
		assertThat(wrapper.getValue("A"), is((Object)"generated"));
		assertThat(valuesRowCaptor.getValue(), instanceOf(StreamItemValuesRow.class));
	}

	@Test
	public void shouldCacheValuesRowForGeneratedColumns() {
		when(columnMockA.isGenerated()).thenReturn(true);
		when(columnMockA.getGeneratedValueGenerator()).thenReturn(generatorMock);
		when(columnMockB.isGenerated()).thenReturn(true);
		when(columnMockB.getGeneratedValueGenerator()).thenReturn(generatorMock);
		when(generatorMock.getValue(valuesRowCaptor.capture())).thenReturn("generated", "generatedToo");
		
		assertThat(wrapper.getValue("A"), is((Object)"generated"));
		assertThat(wrapper.getValue("B"), is((Object)"generatedToo"));
		
		List<ValuesRow> valueRows = valuesRowCaptor.getAllValues();
		assertThat(valueRows.get(0), sameInstance(valueRows.get(1)));
	}

	@Test
	public void shouldReturnNullOnGeneratedColumnsWithoutValueGenerator() {
		when(columnMockA.isGenerated()).thenReturn(true);
		assertThat(wrapper.getValue("A"), is(nullValue()));
		verifyZeroInteractions(delegateMock);
	}
}

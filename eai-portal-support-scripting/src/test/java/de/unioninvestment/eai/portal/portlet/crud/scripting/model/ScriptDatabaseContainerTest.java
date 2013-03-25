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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Script;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler;

public class ScriptDatabaseContainerTest {

	private ScriptCompiler compiler = new ScriptCompiler();

	private ScriptContainer container;

	@Mock
	private ContainerRow containerRowMock;

	@Mock
	private ContainerRowId containerRowIdMock1;

	@Mock
	private ContainerRowId containerRowIdMock2;

	@Mock
	private DataContainer databaseContainerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		container = new ScriptDatabaseContainer(databaseContainerMock);
	}

	@Test
	public void shouldAddFiltersInsideClosure() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { \n equal 'MYCOL', 'Test' \n equal 'MY2COL', 'Bla' \n }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Equal("MYCOL", "Test"), new Equal("MY2COL",
						"Bla")));
	}

	@Test
	public void shouldSupportFilterReplacement() throws InstantiationException,
			IllegalAccessException {

		runScript("container.replaceFilters { equal 'MYCOL', 'Test' }");

		verify(databaseContainerMock).replaceFilters(
				asList((Filter) new Equal("MYCOL", "Test")), false);
	}

	@Test
	public void shouldSupportEqualFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { equal 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Equal("MYCOL", "Test")));
	}

	@Test
	public void shouldSupportGreaterFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { greater 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Greater("MYCOL", "Test", false)));
	}

	@Test
	public void shouldSupportGreaterOrEqualFilter()
			throws InstantiationException, IllegalAccessException {

		runScript("container.addFilters { greaterOrEqual 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Greater("MYCOL", "Test", true)));
	}

	@Test
	public void shouldSupportLessFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { less 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Less("MYCOL", "Test", false)));
	}

	@Test
	public void shouldSupportLessOrEqualFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { lessOrEqual 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Less("MYCOL", "Test", true)));
	}

	@Test
	public void shouldSupportLikeFilterByStartsWith()
			throws InstantiationException, IllegalAccessException {

		runScript("container.addFilters { startsWith 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new StartsWith("MYCOL", "Test", false)));
	}

	@Test
	public void shouldSupportLikeFilterByContains()
			throws InstantiationException, IllegalAccessException {

		runScript("container.addFilters { contains 'MYCOL', 'Test' }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Contains("MYCOL", "Test", false)));
	}

	@Test
	public void shouldSupportAnyFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { any { \n equal \"MYCOL1\", 30 \nequal \"MYCOL2\", 40 \ncontains \"MYCOL3\", 'Test'} }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Any(asList(
						(Filter) new Equal("MYCOL1", 30), new Equal("MYCOL2",
								40), new Contains("MYCOL3", "Test", false)))));
	}

	@Test
	public void shouldSupportAndFilter() throws InstantiationException,
			IllegalAccessException {

		runScript("container.addFilters { all { \n equal \"MYCOL1\", 30 \nequal \"MYCOL2\", 40 \ncontains \"MYCOL3\", 'Test'} }");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new All(asList(
						(Filter) new Equal("MYCOL1", 30), new Equal("MYCOL2",
								40), new Contains("MYCOL3", "Test", false)))));
	}

	@Test
	public void shouldAddDurableFilter() throws InstantiationException,
			IllegalAccessException {
		runScript("container.addFilters { equal \"MYCOL1\", 30, durable:true } ");

		verify(databaseContainerMock).addFilters(
				asList((Filter) new Equal("MYCOL1", 30, true)));
	}

	@Test
	public void shouldNotRemoveDurableFilter() throws InstantiationException,
			IllegalAccessException {
		runScript("container.addFilters { equal durable:true, \"MYCOL1\", 30; \n equal \"MYCOL2\", 20; \n equal \"MYCOL3\", 10; } ");
		runScript("container.removeAllFilters() ");

		verify(databaseContainerMock).removeAllFilters();
		// assertions und weitere verifys?
	}

	@Test
	public void shouldRemoveDurableFilter() throws InstantiationException,
			IllegalAccessException {
		runScript("container.addFilters { equal \"MYCOL1\", 30, durable:true; equal \"MYCOL2\", 20; equal \"MYCOL3\", 10 } ");
		runScript("container.removeAllFilters(true) ");

		verify(databaseContainerMock).removeAllFilters(true);

		// assertions und weitere verifys?
	}

	private void runScript(String script) throws InstantiationException,
			IllegalAccessException {
		Class<Script> compileScript = compiler.compileScript(script);
		Script instance = compileScript.newInstance();
		instance.getBinding().setVariable("container", container);

		instance.run();
	}

	@Test
	public void shouldRemoveAllRows() {
		container.removeAllRows();

		verify(databaseContainerMock).removeAllRows();
	}

	@Test
	public void shouldAddRow() {
		when(databaseContainerMock.addRow()).thenReturn(containerRowMock);

		ScriptRow row = container.addRow();

		assertThat(row, is(ScriptRow.class));
		verify(databaseContainerMock).addRow();
	}

	@Test
	public void shouldCallRefreshOnTable() {
		container.refresh();

		verify(databaseContainerMock).refresh();
	}

	@Test
	public void shouldreturnContainerRowIds() {
		List<ContainerRowId> containerRowIds = new ArrayList<ContainerRowId>();
		containerRowIds.add(containerRowIdMock1);
		containerRowIds.add(containerRowIdMock2);

		when(databaseContainerMock.getRowIds()).thenReturn(containerRowIds);

		assertThat(container.getRowIds().size(), is(2));
	}
}

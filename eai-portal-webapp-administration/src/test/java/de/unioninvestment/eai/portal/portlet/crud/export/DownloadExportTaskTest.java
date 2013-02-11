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
package de.unioninvestment.eai.portal.portlet.crud.export;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.tableexport.TableExport;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

public class DownloadExportTaskTest {

	private com.vaadin.ui.Table vaadinTable = new com.vaadin.ui.Table();

	@Mock
	private Table tableModelMock;

	private DownloadExportTask task;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		task = new DownloadExportTask(null, vaadinTable, tableModelMock, true,
				"abc", "text/plain");
	}

	@Test
	public void shouldCreateTableExport() {
		TableExport export = task.createExport();

		assertThat(export.getMimeType(), is("text/plain"));
		assertThat(export.getTable(), sameInstance(vaadinTable));
	}

	/**
	 * Benötigt für IE / Firefox(?)
	 */
	@Test
	public void shouldExportToNewWindow() {
		TableExport export = task.createExport();

		assertThat(export.getExportWindow(), is("_blank"));
	}
}

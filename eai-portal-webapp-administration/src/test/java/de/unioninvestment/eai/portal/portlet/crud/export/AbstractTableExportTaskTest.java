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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.ContextMock;

public class AbstractTableExportTaskTest extends SpringPortletContextTest {

	@Mock
	private MessageSource messageSourceMock;

	@Mock
	private com.vaadin.ui.Table vaadinTableMock;

	@Mock
	private de.unioninvestment.eai.portal.portlet.crud.domain.model.Table tableModelMock;

	@Mock
	public TableExport tableExportMock;

	@Mock
	public ExcelExport excelExportMock;

	private TestExportTask task;

	@Mock
	private ExportFrontend frontendMock;

	@Mock
	private UI uiMock;

	@Mock
	private Lock lockMock;

	@Mock
	private VaadinSession vaadinSessionMock;
	
	@Rule
	public ContextMock contextMock = new ContextMock();

	@Mock
	private TableColumns tableColumnsMock;

	@Mock
	private TableColumn column1Mock;
	@Mock
	private TableColumn column2Mock;

	@Mock
	private TableColumn column3Mock;

	private final class TestExportTask extends AbstractTableExportTask {

		public TestExportTask(UI application, com.vaadin.ui.Table vaadinTable,
				de.unioninvestment.eai.portal.portlet.crud.domain.model.Table tableModel, boolean automaticDownload) {
			super(application, vaadinTable, tableModel, automaticDownload);
		}

		@Override
		protected TableExport createExport() {
			return tableExportMock;
		}

		@Override
		protected String createFilename() {
			return "myFilename";
		}
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		task = new TestExportTask(uiMock, vaadinTableMock,
				tableModelMock, true);
		task.setMessageSource(messageSourceMock);

		when(uiMock.getSession()).thenReturn(vaadinSessionMock);
		when(vaadinSessionMock.getLockInstance()).thenReturn(lockMock);
		
		executeUiAccessRunnables();
		
		mockExportCallbackCall();
	}

	private void executeUiAccessRunnables() {
		when(uiMock.access(isA(Runnable.class))).thenAnswer(new Answer<Future<Void>>() {
			@Override
			public Future<Void> answer(InvocationOnMock invocation)
					throws Throwable {
				Runnable arg = (Runnable) invocation.getArguments()[0];
				arg.run();
				return null;
			}
		});
	}

	private void mockExportCallbackCall() {
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ExportWithExportSettings callback = (ExportWithExportSettings) invocation
						.getArguments()[0];
				callback.export();
				return null;
			}
		}).when(tableModelMock).withExportSettings(any(ExportWithExportSettings.class));
	}

	@Test
	public void shouldAskSubclassForFilename() {
		task.run();
		assertThat(task.getFilename(), is("myFilename"));
	}

	@Test
	public void shouldUseSubclassTableExportInstanceForReport() {
		task.run();
		verify(tableExportMock).convertTable();
	}

	@Test
	public void shouldMarkTaskAsFinishedAfterComputation() {
		task.run();
		assertThat(task.isFinished(), is(true));
	}

	@Test
	public void shouldProvideDateFormatHelperMethod() {
		assertTrue(task.createFilenameTime().matches(
				"\\d\\d_\\d\\d_\\d\\d\\d\\d"));
	}

	@Test
	public void shouldMarkTaskAsFinishedEvenAfterInterruption() {
		interruptUsingCheckMethod();
		task.run();
		assertThat(task.isFinished(), is(true));
	}

	private void interruptUsingCheckMethod() {
		task.cancel();
		callCheckMethodOnConvertTable();
	}

	@Test
	public void shouldRevertToPreviousVaadinApplicationAfterFinishing() {
		task.run();
		assertThat(UI.getCurrent(), is(nullValue()));
	}

	private void callCheckMethodOnConvertTable() {
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				task.checkForInterruption();
				return null;
			}
		}).when(tableExportMock).convertTable();
	}

	@Test
	public void shouldInformFrontendAboutInternalErrors() {
		task.setFrontend(frontendMock);
		doThrow(new RuntimeException()).when(tableExportMock).convertTable();
		task.run();

		verify(frontendMock).handleException(
				isA(TechnicalCrudPortletException.class));
	}

	@Test
	public void shouldInformFrontendAboutBusinessErrors() {
		task.setFrontend(frontendMock);
		doThrow(new BusinessException("portlet.crud.error.export.csvLimit"))
				.when(tableExportMock).convertTable();
		task.run();

		verify(frontendMock).handleException(isA(BusinessException.class));
	}

	@Test
	public void shouldInformFrontendOnFinish() {
		task.setFrontend(frontendMock);
		task.run();
		verify(frontendMock).updateProgress(1.0f);
		verify(frontendMock).finished();
	}

	@Test
	public void shouldSendTheReportToClient() {
		callCheckMethodOnConvertTable();
		task.run();
		task.sendToClient("_blank");
		verify(tableExportMock).sendConverted();
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotAllowSendMethodBeforeFinish() {
		task.sendToClient("_blank");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotAllowSendMethodAfterInterruption() {
		interruptUsingCheckMethod();
		task.run();

		task.sendToClient("_blank");
	}

	@Test
	public void shouldApplyDefaultDateFormat() {
		task.applyExcelFormatForColumns(excelExportMock);
		verify(excelExportMock).setDateDataFormat("[$-0407]dd.MM.yyyy;@");
	}
	
	@Test
	public void shouldApplyDefaultNumberFormat() {
		task.applyExcelFormatForColumns(excelExportMock);
		verify(excelExportMock).setDoubleDataFormat("#.##0");
	}
	
	@Test
	public void shouldApplyExplicitColumnExcelFormat() {
		prepareThreeColumns();
		when(column1Mock.getExcelFormat()).thenReturn("myExcelFormat");
		
		task.applyExcelFormatForColumns(excelExportMock);

		verify(excelExportMock).setExcelFormatOfProperty("column1", "myExcelFormat");
	}

	@Test
	public void shouldApplyDateFormatCalculatedFromDisplayFormat() {
		prepareThreeColumns();
		when(column1Mock.getDisplayFormat()).thenReturn("dd.MM.yyyy HH:mm");
		
		task.applyExcelFormatForColumns(excelExportMock);
		
		verify(excelExportMock).setExcelFormatOfProperty("column1", "[$-0407]dd.MM.yyyy HH:mm;@");
	}
	
	@Test
	public void shouldNotApplyAnyColumnFormatsByDefault() {
		prepareThreeColumns();
		
		task.applyExcelFormatForColumns(excelExportMock);
		
		verify(excelExportMock, never()).setExcelFormatOfProperty(isA(String.class), isA(String.class));
	}

	@Test
	public void shouldIgnoreGeneratedColumnsWithoutExcelFormat() {
		prepareThreeColumns();
		doReturn(null).when(vaadinTableMock).getType("column1");
		
		task.applyExcelFormatForColumns(excelExportMock);
		
		verify(excelExportMock, never()).setExcelFormatOfProperty(isA(String.class), isA(String.class));
	}
	
	private void prepareThreeColumns() {
		when(tableModelMock.getColumns()).thenReturn(tableColumnsMock);
		when(tableColumnsMock.iterator()).thenReturn(asList(column1Mock, column2Mock, column3Mock).iterator());
		when(column1Mock.getName()).thenReturn("column1");
		when(column2Mock.getName()).thenReturn("column2");
		when(column3Mock.getName()).thenReturn("column3");
		doReturn(Date.class).when(vaadinTableMock).getType("column1");
		doReturn(Number.class).when(vaadinTableMock).getType("column2");
		doReturn(String.class).when(vaadinTableMock).getType("column3");
	}
	
}
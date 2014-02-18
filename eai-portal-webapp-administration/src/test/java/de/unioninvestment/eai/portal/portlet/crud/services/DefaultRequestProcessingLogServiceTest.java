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

package de.unioninvestment.eai.portal.portlet.crud.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import de.unioninvestment.eai.portal.junit.rules.Log4jStub;
import de.unioninvestment.eai.portal.portlet.crud.CrudUI;
import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.support.vaadin.RequestProcessingInfo;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class DefaultRequestProcessingLogServiceTest {

	private DefaultRequestProcessingLogService service;

	@Mock
	private TaskScheduler schedulerMock;
	@Mock
	private Settings settingsMock;
	@Mock
	private RequestProcessingLogDao daoMock;
	@Mock
	private Clock clockMock;
	@Mock
	private RequestProcessingInfo infoMock;
	@Mock
	private CrudUI uiMock;

	@Rule
	public LiferayContext context = new LiferayContext();

	@Rule
	public Log4jStub logging = new Log4jStub(DefaultRequestProcessingLogService.class);
	
	@Mock
	private Config configMock;

	Date requestStart = new GregorianCalendar(2014, 1, 18, 15, 18, 0).getTime();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		service = new DefaultRequestProcessingLogService();
		service.scheduler = schedulerMock;
		service.settings = settingsMock;
		service.dao = daoMock;
		service.clock = clockMock;

		when(settingsMock.isRequestLogEnabled()).thenReturn(true);
		when(settingsMock.getRequestLogCleanupCronExpression()).thenReturn(
				"* * * * * ?");
		when(settingsMock.getRequestLogCleanupMaxAgeDays()).thenReturn(30);
		when(settingsMock.getRequestLogMinimalDurationMillis())
				.thenReturn(5000);

		when(uiMock.getPage()).thenReturn(context.getPageMock());
	}

	@Test
	public void shouldScheduleCleanupTask() {

		service.start();

		verify(schedulerMock).schedule(Mockito.isA(Runnable.class),
				Mockito.eq(new CronTrigger("* * * * * ?")));
	}

	@Test
	public void shouldNotScheduleCleanupTaskIfRequestLogIsDisabled() {
		when(settingsMock.isRequestLogEnabled()).thenReturn(false);
		service.start();
		verifyZeroInteractions(schedulerMock);
	}

	@Test
	public void shouldNotScheduleCleanupTaskIfCronIsNull() {
		when(settingsMock.getRequestLogCleanupCronExpression())
				.thenReturn(null);
		service.start();
		verifyZeroInteractions(schedulerMock);
	}

	@Test
	public void shouldNotScheduleCleanupTaskIfMaxAgeIsNull() {
		when(settingsMock.getRequestLogCleanupMaxAgeDays()).thenReturn(null);
		service.start();
		verifyZeroInteractions(schedulerMock);
	}

	@Test
	public void shouldCleanupRequestLog() {
		service.start();
		when(clockMock.now()).thenReturn(
				new GregorianCalendar(2014, 1, 18, 15, 18, 0).getTime());

		service.cleanupRequestLogTable();

		verify(daoMock).cleanupRequestLogTable(
				new GregorianCalendar(2014, 0, 19, 15, 18, 0).getTime());
	}

	@Test
	public void shouldAddNewRowForLongRunningRequestWithLoggingEnabled() {
		givenALongRunningRequest();

		service.addRequestLogEntry(infoMock, uiMock);

		verify(daoMock).storeRequestLogEntry("http://test.de", "svnUrl",
				"sqlStatements", 12, requestStart, 5122, 2121);
	}

	@Test
	public void shouldHandleMissingConfig() {
		givenALongRunningRequest();
		when(uiMock.getPortletConfig()).thenReturn(null);

		service.addRequestLogEntry(infoMock, uiMock);
		verify(daoMock).storeRequestLogEntry("http://test.de", null,
				"sqlStatements", 12, requestStart, 5122, 2121);
	}

	@Test
	public void shouldIgnoreRequestsIfLoggingIsDisabled() {
		givenALongRunningRequest();
		when(settingsMock.isRequestLogEnabled()).thenReturn(false);

		service.addRequestLogEntry(infoMock, uiMock);
		verifyZeroInteractions(daoMock);
	}

	@Test
	public void shouldIgnoreShortRunningRequests() {
		givenALongRunningRequest();
		when(infoMock.getTimeSinceRequestStart()).thenReturn(4900L);

		service.addRequestLogEntry(infoMock, uiMock);
		verifyZeroInteractions(daoMock);
	}

	@Test
	public void shouldFailGracefully() {
		givenALongRunningRequest();
		when(infoMock.getMeasuredTime("db")).thenThrow(new RuntimeException("Fail"));

		service.addRequestLogEntry(infoMock, uiMock);
		logging.assertError("Error writing request log entry");
	}

	private void givenALongRunningRequest() {
		when(context.getPageMock().getLocation()).thenReturn(
				URI.create("http://test.de"));

		when(uiMock.getPortletConfig()).thenReturn(configMock);
		when(configMock.getFileName()).thenReturn("svnUrl");

		when(infoMock.getSqlStatements()).thenReturn("sqlStatements");
		when(infoMock.getCountOfSqlStatements()).thenReturn(12);

		when(infoMock.getStartTime()).thenReturn(requestStart.getTime());
		when(infoMock.getTimeSinceRequestStart()).thenReturn(5122L);
		when(infoMock.getMeasuredTime("db")).thenReturn(2121L);
	}
}

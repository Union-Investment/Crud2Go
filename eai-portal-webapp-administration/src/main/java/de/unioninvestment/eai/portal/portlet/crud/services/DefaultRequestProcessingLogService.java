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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import de.unioninvestment.eai.portal.portlet.crud.CrudUI;
import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.support.vaadin.RequestProcessingInfo;

/**
 * Implementation that adds and cleans up request processing data to
 * C2G_REQUEST_LOG.
 * 
 * @author cmj
 */
@Service
public class DefaultRequestProcessingLogService implements
		RequestProcessingLogService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRequestProcessingLogService.class);

	@Autowired
	Settings settings;

	@Autowired
	RequestProcessingLogDao dao;

	@Autowired
	TaskScheduler scheduler;

	@Autowired
	Clock clock;

	@PostConstruct
	public void start() {
		if (settings.isRequestLogEnabled()) {
			if (settings.getRequestLogCleanupCronExpression() != null
					&& settings.getRequestLogCleanupMaxAgeDays() != null) {

				scheduleRequestLogCleanupTask();
			} else {
				LOGGER.warn("Request Logging is activated but cleanup is not configured!");
			}
		}
	}

	private void scheduleRequestLogCleanupTask() {
		String cronExpression = settings.getRequestLogCleanupCronExpression();
		LOGGER.info("Scheduling request log cleanup task with cron '{}'",
				cronExpression);

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				cleanupRequestLogTable();
			}
		}, new CronTrigger(cronExpression));
	}

	/**
	 * Removes all entries older than the preconfigured age from
	 * C2G_REQUEST_LOG.
	 * <p/>
	 * Method execution is scheduled by a cron expression from
	 * eai-portal-administration.properties.
	 */
	protected void cleanupRequestLogTable() {
		int deletedRows = dao.cleanupRequestLogTable(newestDateToDelete());
		LOGGER.info("Deleted {} rows during request log cleanup", deletedRows);
	}

	private Date newestDateToDelete() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(clock.now());
		calendar.add(Calendar.DAY_OF_YEAR,
				-settings.getRequestLogCleanupMaxAgeDays());
		Date newestDateToDelete = calendar.getTime();
		return newestDateToDelete;
	}

	@Override
	public void addRequestLogEntry(RequestProcessingInfo info, CrudUI ui) {
		if (settings.isRequestLogEnabled()) {
			if (info.getTimeSinceRequestStart() >= settings
					.getRequestLogMinimalDurationMillis()) {
				try {

					String url = ui.getPage().getLocation().toString();
					String svnUrl = ui.getPortletConfig() != null ? ui
							.getPortletConfig().getFileName() : null;
					Date dateCreated = new Date(info.getStartTime());

					dao.storeRequestLogEntry(url, //
							svnUrl, //
							info.getSqlStatements(), //
							info.getCountOfSqlStatements(), //
							dateCreated, //
							info.getTimeSinceRequestStart(), //
							info.getMeasuredTime("db"));

				} catch (Exception e) {
					LOGGER.error("Error writing request log entry", e);
				}
			}
		}
	}
}

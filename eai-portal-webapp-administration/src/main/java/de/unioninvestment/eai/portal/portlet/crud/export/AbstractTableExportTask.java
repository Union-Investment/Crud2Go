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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

import com.vaadin.Application;
import com.vaadin.addon.tableexport.TableExport;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportCallback;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;

/**
 * Abstrakte Oberklasse für Exports, die auf dem TableExport Addon basieren.
 * 
 * @author carsten.mjartan
 */
@Configurable
public abstract class AbstractTableExportTask implements ExportTask {

	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private TableExport export;
	private String filename;

	private volatile boolean cancelled = false;
	private volatile boolean finished = false;

	private final Application application;
	protected final com.vaadin.ui.Table vaadinTable;
	protected final Table tableModel;
	protected volatile ExportFrontend frontend;

	@Autowired
	private MessageSource messageSource;

	private final boolean automaticDownload;

	/**
	 * @param application
	 *            die Vaadin Application für den ThreadLocal
	 * @param vaadinTable
	 *            die zu exportierende Vaadin-Tabelle
	 * @param tableModel
	 *            das Tabellen-Modell
	 * @param automaticDownload
	 */
	public AbstractTableExportTask(Application application,
			com.vaadin.ui.Table vaadinTable, Table tableModel,
			boolean automaticDownload) {
		this.application = application;
		this.vaadinTable = vaadinTable;
		this.tableModel = tableModel;
		this.automaticDownload = automaticDownload;
	}

	@Override
	public void run() {
		Application previousApplication = PortletApplication
				.getCurrentApplication();
		try {
			PortletApplication.setCurrentApplication(application);

			filename = createFilename();
			LOGGER.info("Started export thread for report '{}'", filename);
			export = createExport();

			tableModel.withExportSettings(new ExportCallback() {
				public void export() {
					LOGGER.info("Building Report");
					try {
						export.convertTable();
						finished = true;
						informFrontendAboutFinish();
						LOGGER.info("Finished Building Excel sheet");

					} catch (ExportInterruptionException e) {
						LOGGER.info("Report generation was cancelled/interrupted");
						cancelled = true;
					}
				}
			});
			LOGGER.info("Finished export thread for report '{}'", filename);

		} catch (Exception e) {
			LOGGER.error("Error during report generation", e);
			informFrontendAboutException(e);

		} finally {
			finished = true;
			PortletApplication.setCurrentApplication(previousApplication);
		}
	}

	protected abstract TableExport createExport();

	protected abstract String createFilename();

	protected String createFilenameTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
		return sdf.format(new Date());
	}

	@Override
	public void sendToClient(String target) {
		if (finished && !cancelled) {
			export.setExportWindow(target);
			export.sendConverted();
		} else {
			throw new IllegalStateException(
					"Cannot send export to client when not finished");
		}
	}

	@Override
	public void cancel() {
		cancelled = true;
		if (filename != null) {
			// only if started ;-)
			waitForFinishing();
		}
	}

	@Override
	public void setFrontend(ExportFrontend frontend) {
		this.frontend = frontend;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	protected String getFilename() {
		return filename;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}

	protected void checkForInterruption() {
		if (cancelled || Thread.currentThread().isInterrupted()) {
			cancelled = true;
			throw new ExportInterruptionException();
		}
	}

	private void informFrontendAboutFinish() {
		if (frontend != null) {
			if (automaticDownload) {
				frontend.finished();
			} else {
				// this has to indirectly trigger the finish - not nice but
				// needed for IE7/8
				export.sendConverted();
			}
			frontend.updateProgress(100f);
		}
	}

	private void informFrontendAboutException(Exception e) {
		if (frontend != null) {
			if (e instanceof BusinessException) {
				frontend.handleException(e);
			} else {
				frontend.handleException(new TechnicalCrudPortletException(
						messageSource.getMessage(
								"portlet.crud.error.export.internal", null,
								"Es ist ein technischer Fehler aufgetreten",
								null)));
			}
		}
	}

	private void waitForFinishing() {
		while (true) {
			if (finished) {
				return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	/**
	 * For Testing
	 * 
	 * @param filename
	 *            der Dateiname
	 */
	void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * For Testing.
	 * 
	 * @param messageSource
	 *            die Übersetzungstabelle
	 */
	void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected boolean isAutomaticDownload() {
		return automaticDownload;
	}

}
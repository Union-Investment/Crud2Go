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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

/**
 * Abstrakte Oberklasse für Exports, die auf dem TableExport Addon basieren.
 * 
 * @author carsten.mjartan
 */

public abstract class AbstractTableExportTask extends AbstractExportTask
		implements ExportTask {

	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private String filename;

	private volatile boolean cancelled = false;
	private volatile boolean finished = false;

	protected TableExport export;
	protected final com.vaadin.ui.Table vaadinTable;
	protected final Table tableModel;

	/**
	 * @param application
	 *            die Vaadin Application für den ThreadLocal
	 * @param vaadinTable
	 *            die zu exportierende Vaadin-Tabelle
	 * @param tableModel
	 *            das Tabellen-Modell
	 * @param automaticDownload
	 */
	public AbstractTableExportTask(UI ui, com.vaadin.ui.Table vaadinTable,
			Table tableModel, boolean automaticDownload) {
		super(ui, automaticDownload);
		this.vaadinTable = vaadinTable;
		this.tableModel = tableModel;
	}

	@Override
	public void run() {
		try {

			filename = createFilename();
			LOGGER.info("Started export thread for report '{}'", filename);
			export = createExport();

			doWithExportSettingsAndProperLocking(tableModel, new ExportWithExportSettings() {
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
			finished = true;
			LOGGER.error("Error during report generation", e);
			informFrontendAboutException(e);

		} finally {
			finished = true;
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

	protected void informFrontendAboutFinish() {
		if (frontend != null) {
			ui.access(new Runnable() {
				@Override
				public void run() {
					if (automaticDownload) {
						frontend.finished();
					} else {
						// this has to indirectly trigger the finish - not nice
						// but needed for IE7/8
						export.sendConverted();
					}
					frontend.updateProgress(1.0f);
				}
			});
		}
	}

	@Override
	protected StreamResource createResourceForContent() {
		throw new UnsupportedOperationException(
				"Not needed as informFrontendAboutFinish() is overridden");
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

	protected boolean isAutomaticDownload() {
		return automaticDownload;
	}

	protected void deleteFileOnExit(File tempFile) {
		if (tempFile != null && tempFile.exists()) {
			tempFile.deleteOnExit();
		}
	}

	protected void closeStream(FileOutputStream fileOut) {
		if (fileOut != null) {
			try {
				fileOut.close();
			} catch (final IOException e) {
				// ignore it
			}
		}
	}

}
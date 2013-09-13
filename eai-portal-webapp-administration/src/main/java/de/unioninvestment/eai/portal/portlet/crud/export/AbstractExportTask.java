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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

/**
 * Common superclas for both Download Actions and Export Actions using the
 * TableExport Add-On.
 * 
 * @author cmj
 */
@Configurable
public abstract class AbstractExportTask implements ExportTask {

	protected final UI ui;
	protected final boolean automaticDownload;

	protected volatile ExportFrontend frontend;
	private float lastProgress = 0f;

	@Autowired
	protected MessageSource messageSource;

	/**
	 * Initialize this class.
	 * 
	 * @param ui
	 * @param automaticDownload
	 */
	public AbstractExportTask(UI ui, boolean automaticDownload) {
		this.ui = ui;
		this.automaticDownload = automaticDownload;
	}

	/**
	 * Delegates to {@link Table#withExportSettings(ExportWithExportSettings)},
	 * ensuring proper UI locking.
	 * 
	 * @param tableModel
	 *            the model to act on
	 * @param realExportWithExportSettings
	 *            the real export functionality that doesn't itself require a
	 *            lock
	 */
	protected void doWithExportSettingsAndProperLocking(Table tableModel,
			final ExportWithExportSettings realExportWithExportSettings) {
		ui.getSession().getLockInstance().lock();
		try {
			tableModel.withExportSettings(new ExportWithExportSettings() {
				@Override
				public void export() {
					ui.getSession().getLockInstance().unlock();
					try {
						realExportWithExportSettings.export();
					} finally {
						ui.getSession().getLockInstance().lock();
					}
				}
			});
		} finally {
			ui.getSession().getLockInstance().unlock();
		}
	}

	@Override
	public void setFrontend(ExportFrontend frontend) {
		this.frontend = frontend;
	}

	/**
	 * Updates the progress bar after at least 1% of progress, ensuring proper
	 * locking.
	 * 
	 * @param progress
	 *            the new progress (0 <= progress <= 1)
	 */
	public void informFrontendAboutProgress(final float progress) {
		if (frontend != null) {
			if ((progress - lastProgress) >= 0.01f) {
				ui.access(new Runnable() {
					@Override
					public void run() {
						frontend.updateProgress(progress);
					}
				});
				lastProgress = progress;
			}
		}
	}

	/**
	 * @return a stream resource, required for manual download.
	 */
	abstract protected StreamResource createResourceForContent();

	/**
	 * Informs the frontend about a finished job, either allowing manual
	 * download or opening the download automatically. Creates a lock on the UI
	 * session.
	 */
	protected void informFrontendAboutFinish() {
		if (frontend != null) {
			ui.access(new Runnable() {
				@Override
				public void run() {
					if (automaticDownload) {
						frontend.finished();
					} else {
						StreamResource resource = createResourceForContent();
						frontend.finished(resource);
					}
					frontend.updateProgress(1.0f);
				}
			});
		}
	}

	/**
	 * Informs the frontend about an error. Creates a lock on the UI session.
	 * 
	 * @param e
	 *            the error
	 */
	protected void informFrontendAboutException(final Exception e) {
		if (frontend != null) {
			ui.access(new Runnable() {
				@Override
				public void run() {
					if (e instanceof BusinessException) {
						frontend.handleException(e);
					} else {
						frontend.handleException(new TechnicalCrudPortletException(
								messageSource
										.getMessage(
												"portlet.crud.error.export.internal",
												null,
												"Es ist ein technischer Fehler aufgetreten",
												null)));
					}
				}
			});
		}
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

}
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

import java.io.InputStream;

import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableExportType;

/**
 * Tabellen-Aktion (Skript-Action).
 * 
 * @author max.hartmann
 * 
 */
public class TableAction extends AbstractAction<TableActionConfig> {
	/**
	 * Callback to handle the actual download generation of a download-action.
	 * 
	 * 
	 * @author Jan Malcomess (codecentric AG)
	 * @since 1.46
	 * @see TableAction#isDownloadAction().
	 */
	public interface DownloadActionCallback {
		/**
		 * Called, when download generation starts.
		 * 
		 * @param filename
		 *            The name of the file generated for download.
		 * @param mimeType
		 *            The MIME-Type of the file generated for download.
		 */
		public void start(String filename, String mimeType);

		/**
		 * Optionally called to allow for provision of a progress bar.
		 * 
		 * @param progress
		 *            The current progress (e.g. count / size)
		 */
		public void updateProgess(float progress);

		/**
		 * Called, when download generation is finished and the file can
		 * actually be downloaded.
		 * 
		 * @param stream
		 *            <code>InputStream</code> providing the content of the
		 *            file.
		 */
		public void finish(InputStream stream);
	}

	private static final long serialVersionUID = 1L;

	private Table table;

	/**
	 * Callback to handle the actual download generation of a download-action.
	 * 
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	private DownloadActionCallback downloadActionCallback;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param portlet
	 *            Portletmodel
	 * @param config
	 *            Konfiguration
	 * @param table
	 *            Tabelle
	 * @param trigger
	 *            Triggers
	 */
	public TableAction(Portlet portlet, TableActionConfig config, Table table,
			Triggers trigger) {
		super(portlet, config, trigger);
		this.table = table;
	}

	public Table getTable() {
		return table;
	}

	/**
	 * @return <code>true</code> if this action is an export action.
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public boolean isExportAction() {
		return getConfig().getExport() != null;
	}

	/**
	 * if <code>{@link #isExportAction()} == true</code> then this will return
	 * the concrete type.
	 * 
	 * @return the concrete <code>{@link TableExportType}</code>.
	 * @throws NullPointerException
	 *             if <code>{@link #isExportAction()} == false</code>.
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public TableExportType getExportType() {
		return getConfig().getExport().getTarget();
	}

	/**
	 * @return <code>true</code> if this action is a download action.
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public boolean isDownloadAction() {
		return getConfig().getDownload() != null;
	}

	@Override
	public void execute() {
		fireExecutionEvent();

		super.execute();
	}

	/**
	 * @return Callback to handle the actual download generation of a
	 *         download-action.
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public DownloadActionCallback getDownloadActionCallback() {
		return downloadActionCallback;
	}

	/**
	 * @param downloadActionCallback
	 *            Callback to handle the actual download generation of a
	 *            download-action.
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public void setDownloadActionCallback(
			DownloadActionCallback downloadActionCallback) {
		this.downloadActionCallback = downloadActionCallback;
	}
}

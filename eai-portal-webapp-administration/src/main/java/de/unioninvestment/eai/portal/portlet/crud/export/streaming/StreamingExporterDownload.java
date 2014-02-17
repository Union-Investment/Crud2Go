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

package de.unioninvestment.eai.portal.portlet.crud.export.streaming;

import java.io.InputStream;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

/**
 * Implementation of {@link Download} that uses a Container-{@link DataStream}
 * for input and some {@link Exporter} for the export.
 * 
 * @author cmj
 */
public class StreamingExporterDownload implements Download {

	private final String filename;
	private final String mimeType;
	private final DataContainer container;
	private int idx;
	private int count;
	private Table table;
	private Exporter exporter;
	
	int statusUpdateInterval = 100;

	/**
	 * @param filename
	 *            the filename returned to the browser
	 * @param mimeType
	 *            the content mime type returned to the browsetr
	 * @param table
	 *            the table to export
	 * @param exporter
	 *            the generator of the export file
	 */
	public StreamingExporterDownload(String filename, String mimeType,
			Table table, Exporter exporter) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.table = table;
		this.exporter = exporter;
		this.container = table.getContainer();
		this.idx = 0;
	}

	@Override
	public InputStream build(Status status) {
		DataStream stream = table.getStream();
		try {
			this.count = stream.open(true);

			StreamingExportInfo exportInfo = new StreamingExportInfo(container,
					table);
			exporter.begin(exportInfo);

			StreamItemInfoWrapper itemInfo = new StreamItemInfoWrapper(table,
					exportInfo.getColumnNames());
			while (stream.hasNext()) {
				StreamItem item = stream.next();
				itemInfo.setItem(item);
				exporter.addRow(itemInfo);
				updateStatus(status);
			}
			return exporter.getInputStream();

		} finally {
			stream.close();
		}
	}

	private void updateStatus(Status status) {
		idx++;
		if (idx % statusUpdateInterval == 0) {
			status.updateProgress(((float) idx) / count);
		}
	}

	public String getFilename() {
		return filename;
	}

	public String getMimeType() {
		return mimeType;
	}

}

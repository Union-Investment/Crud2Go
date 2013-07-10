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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

/**
 * Export in eine Excel-Datei. Der Export ist für die Ausführung in einem
 * separaten Thread ausgelegt.
 * 
 * @author carsten.mjartan
 */
public class ExcelExportTask extends AbstractTableExportTask implements
		ExportTask {

	private static final String EXCEL_XSLX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	/**
	 * Erweiterung des Excel-Exports um Frontend-Updates.
	 * 
	 * @author carsten.mjartan
	 */
	public class ExcelExportWithProgress extends ExcelExport {
		private static final long serialVersionUID = 1L;

		private ExcelExportWithProgress(com.vaadin.ui.Table table,
				Workbook wkbk, String shtName, String rptTitle,
				String xptFileName, boolean hasTotalsRow) {
			super(table, wkbk, shtName, rptTitle, xptFileName, hasTotalsRow);
		}

		@Override
		protected int addDataRows(final Sheet sheetToAddTo, final int row) {
			final Collection<?> itemIds = getTable().getContainerDataSource()
					.getItemIds();
			int localRow = row;
			int count = 0;
			double size = itemIds.size();
			for (final Object itemId : itemIds) {
				checkForInterruption();
				if (frontend != null) {
					float progress = (float) (count / size);
					frontend.updateProgress(progress);
				}
				addDataRow(sheetToAddTo, itemId, localRow);
				count++;
				localRow++;
			}
			return localRow;
		}

		/**
		 * Methode von TableExport kopiert, damit ein Override von
		 * {@link #sendConvertedFileToUser(Application, File, String)} möglich
		 * wird.
		 */
		@Override
		public boolean sendConverted() {
			File tempFile = null;
			FileOutputStream fileOut = null;
			try {
				tempFile = File.createTempFile("tmp", ".xls");
				fileOut = new FileOutputStream(tempFile);
				workbook.write(fileOut);
				if (null == mimeType) {
					setMimeType(EXCEL_MIME_TYPE);
				}
				// removed "super."
				final boolean success = sendConvertedFileToUser(
						UI.getCurrent(), tempFile, exportFileName);
				return success;
			} catch (final IOException e) {
				LOGGER.warn("Converting to XLS failed with IOException ", e);
				return false;
			} finally {
				closeStream(fileOut);
				deleteFileOnExit(tempFile);
			}
		}

		@Override
		protected boolean sendConvertedFileToUser(UI ui, File fileToExport,
				String exportFileName) {
			if (isAutomaticDownload()) {
				return super.sendConvertedFileToUser(UI.getCurrent(),
						fileToExport, exportFileName);
			} else {
				TemporaryFileDownloadResource resource;
				try {
					resource = new TemporaryFileDownloadResource(
							UI.getCurrent(), exportFileName, mimeType,
							fileToExport);
					frontend.finished(resource);
					return true;

				} catch (final FileNotFoundException e) {
					LOGGER.error("Temporary File for Export does not exist ", e);
					return false;
				}
			}
		}
	}

	/**
	 * @param application
	 *            die aktuelle Vaadin Applikation
	 * @param vaadinTable
	 *            die zu exportierende Vaadin-Tabelle
	 * @param tableModel
	 *            das Tabellen-Modell
	 * @param automaticDownload
	 *            wenn <code>true</code>, dann wird die Fertigmeldung an das
	 *            Frontend geliefert und ein automatische Download erfolgt,
	 *            sonst wird bei Fertigmeldung eine StreamResource mitgegeben,
	 *            auf die als Link geklickt werden kann.
	 */
	public ExcelExportTask(UI ui, com.vaadin.ui.Table vaadinTable,
			Table tableModel, boolean automaticDownload) {
		super(ui, vaadinTable, tableModel, automaticDownload);
	}

	@Override
	protected String createFilename() {
		return "export_" + createFilenameTime() + ".xlsx";
	}

	@Override
	protected TableExport createExport() {
		SXSSFWorkbook wkbk = new SXSSFWorkbook(50);
		wkbk.setCompressTempFiles(true);
		ExcelExportWithProgress excelExport = new ExcelExportWithProgress(
				vaadinTable, wkbk, "ExportCallback", null, getFilename(), false);
		excelExport.setExportWindow("_blank");
		excelExport.setMimeType(EXCEL_XSLX_MIMETYPE);
		excelExport.setRowHeaders(true);
		excelExport.excludeCollapsedColumns();
		excelExport.setDoubleDataFormat("#.##0");
		excelExport.setDateDataFormat("DD.MM.YYYY");
		return excelExport;
	}

}

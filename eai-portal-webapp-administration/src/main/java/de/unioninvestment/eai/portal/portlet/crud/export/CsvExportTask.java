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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;

import com.vaadin.addon.tableexport.CsvExport;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.addon.tableexport.XLS2CSVmra;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

/**
 * Export in eine CSV-Datei. Der Export ist für die Ausführung in einem
 * separaten Thread ausgelegt.
 * 
 * @author carsten.mjartan
 */
public class CsvExportTask extends AbstractTableExportTask implements
		ExportTask {
	/**
	 * Erweiterung des CSV-Exports um Frontend-Updates.
	 * 
	 * @author carsten.mjartan
	 */
	public class CsvExportWithProgress extends CsvExport {
		private static final long serialVersionUID = 1L;

		private CsvExportWithProgress(com.vaadin.ui.Table table,
				String sheetName) {
			super(table, sheetName);
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
				
				float progress = (float) (count / size);
				informFrontendAboutProgress(progress);
				
				addDataRow(sheetToAddTo, itemId, localRow);
				count++;
				localRow++;
			}
			return localRow;
		}

		public boolean sendConverted() {
			File tempXlsFile, tempCsvFile;
			try {
				tempXlsFile = File.createTempFile("tmp", ".xls");
				final FileOutputStream fileOut = new FileOutputStream(
						tempXlsFile);
				workbook.write(fileOut);
				final FileInputStream fis = new FileInputStream(tempXlsFile);
				final POIFSFileSystem fs = new POIFSFileSystem(fis);
				tempCsvFile = File.createTempFile("tmp", ".csv");
				final PrintStream p = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(tempCsvFile, true)));

				final XLS2CSVmra xls2csv = new XLS2CSVmra(fs, p, -1);
				xls2csv.process();
				p.close();
				if (null == mimeType) {
					setMimeType(CSV_MIME_TYPE);
				}
				// "super." entfernt, damit die Methode in der Subklasse
				// überschrieben werden kann.
				return sendConvertedFileToUser(UI.getCurrent(), tempCsvFile,
						exportFileName);
			} catch (final IOException e) {
				LOGGER.warn("Converting to CSV failed with IOException ", e);
				return false;
			}
		}

		@Override
		protected boolean sendConvertedFileToUser(UI ui, File fileToExport,
				String exportFileName) {
			if (isAutomaticDownload()) {
				return super.sendConvertedFileToUser(ui, fileToExport,
						exportFileName);
			} else {
				TemporaryFileDownloadResource resource;
				try {
					resource = new TemporaryFileDownloadResource(ui,
							exportFileName, mimeType, fileToExport);
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
	 * @param vaadinTable
	 *            die zu exportierende Vaadin-Tabelle
	 * @param tableModel
	 *            das Tabellen-Modell
	 * @param application
	 * @param automaticDownload
	 */
	public CsvExportTask(UI ui, com.vaadin.ui.Table vaadinTable,
			Table tableModel, boolean automaticDownload) {
		super(ui, vaadinTable, tableModel, automaticDownload);
	}

	@Override
	protected String createFilename() {
		return "export_" + createFilenameTime() + ".csv";
	}

	@Override
	protected TableExport createExport() {
		if (vaadinTable.size() > 65530) {
			throw BusinessException.withFreeformMessage(getMessageSource()
					.getMessage("portlet.crud.error.export.csvLimit", null,
							null));
		}
		CsvExportWithProgress export = new CsvExportWithProgress(vaadinTable,
				"Export");
		export.setMimeType(ExcelExport.CSV_MIME_TYPE);
		export.setExportFileName(getFilename());
		export.setExportWindow("_blank");
		export.setDisplayTotals(false);
		export.setRowHeaders(true);
		export.excludeCollapsedColumns();
		export.setDoubleDataFormat("#.##");
		export.setDateDataFormat("DD.MM.YYYY");
		
		applyExcelFormatForColumns(export);

		return export;
	}
}

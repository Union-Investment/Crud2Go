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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of the {@link ExcelExporter} that transforms the generated Excel
 * sheet to a CSV file.
 * 
 * @author cmj
 */
public class CsvExporter extends ExcelExporter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CsvExporter.class);

	public static final String CSV_MIMETYPE = "text/csv";

	@Override
	public InputStream getInputStream() {
		final InputStream fis = super.getInputStream();
		return createCSVFromExcel(fis);
	}

	private InputStream createCSVFromExcel(final InputStream fis) {
		File tempCsvFile = null;
		PrintStream csvFileOut = null;
		try {
			tempCsvFile = File.createTempFile("tmp", ".csv");
			csvFileOut = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(tempCsvFile, true)));

			OPCPackage p = OPCPackage.open(fis);
			XLSX2CSV xlsx2csv = new XLSX2CSV(p, csvFileOut, -1);
			xlsx2csv.process();

			return new DeletingFileInputStream(tempCsvFile);

		} catch (Exception e) {
			LOGGER.error("Error creating CSV", e);
			return null;

		} finally {
			if (csvFileOut != null) {
				try {
					csvFileOut.close();
				} catch (final RuntimeException e) {
				}
			}
			tempCsvFile.deleteOnExit();
		}
	}
}

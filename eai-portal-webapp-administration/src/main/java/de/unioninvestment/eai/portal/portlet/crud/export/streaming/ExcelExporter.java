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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.support.vaadin.context.Context;

/**
 * Exporter for Excel data. Most of the Code is taken from the Vaadin
 * TableExport Addon (v1.4.0).
 * 
 * @author cmj
 */
public class ExcelExporter implements Exporter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExcelExporter.class);

	public static final String EXCEL_XSLX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	/**
	 * The name of the sheet in the workbook the table contents will be written
	 * to.
	 */
	protected String sheetName;

	/**
	 * The workbook that contains the sheet containing the report with the table
	 * contents.
	 */
	protected Workbook workbook;

	/** The Sheet object that will contain the table contents report. */
	protected Sheet sheet;

	/** The POI cell creation helper. */
	protected CreationHelper createHelper;
	protected DataFormat dataFormat;

	/**
	 * Various styles that are used in report generation. These can be set by
	 * the user if the default style is not desired to be used.
	 */
	protected CellStyle dateCellStyle, doubleCellStyle, columnHeaderCellStyle;
	protected Short dateDataFormat, doubleDataFormat;
	protected Map<Short, CellStyle> dataFormatCellStylesMap = new HashMap<Short, CellStyle>();

	protected Row headerRow;
	protected Map<Object, String> propertyExcelFormatMap = new HashMap<Object, String>();

	private int currentRow;

	private String[] columnNames;
	private String[] columnTitles;
	private Class<?>[] columnTypes;
	private String[] displayFormats;
	private String[] excelFormats;

	@Override
	public void begin(ExportInfo exportInfo) {
		this.columnNames = exportInfo.getColumnNames();
		this.columnTitles = exportInfo.getColumnTitles();
		this.columnTypes = exportInfo.getColumnTypes();
		this.displayFormats = exportInfo.getDisplayFormats();
		this.excelFormats = exportInfo.getExcelFormats();

		prepareDefaults();
		applyExcelFormatForColumns();

		initialSheetSetup();
		this.currentRow = 0;

		addHeaderRow();
		currentRow++;
	}

	@Override
	public void addRow(ItemInfo itemInfo) {
		final Row sheetRow = sheet.createRow(currentRow);
		for (int col = 0; col < columnNames.length; col++) {
			String columnName = columnNames[col];
			Class<?> columnType = columnTypes[col];
			Object value;
			if (itemInfo.isOption(columnName)) {
				value = itemInfo.getTitle(columnName);
				columnType = String.class;
			} else {
				value = itemInfo.getValue(columnName);
			}
			Cell sheetCell = sheetRow.createCell(col);

			final CellStyle cs = getCellStyle(col);
			sheetCell.setCellStyle(cs);
			CellUtil.setAlignment(sheetCell, workbook, CellStyle.ALIGN_LEFT);

			if (null != value) {
				if (!isNumeric(columnType)) {
					if (java.util.Date.class.isAssignableFrom(columnType)) {
                        //noinspection ConstantConditions
                        sheetCell.setCellValue((Date) value);
					} else {
						sheetCell.setCellValue(createHelper
								.createRichTextString(value.toString()));
					}
				} else {
					try {
						final Double d = Double.parseDouble(value.toString());
						sheetCell.setCellValue(d);
					} catch (final NumberFormatException nfe) {
						LOGGER.warn(
								"NumberFormatException parsing a numeric value: ",
								nfe);
						sheetCell.setCellValue(createHelper
								.createRichTextString(value.toString()));
					}
				}
			}
		}
		currentRow++;
	}

	@Override
	public InputStream getInputStream() {
        evaluateFormulas();
        autoSizeAllColumns();

		File tempFile = null;
		FileOutputStream fileOut = null;
		try {
			tempFile = File.createTempFile("tmp", ".xls");
			fileOut = new FileOutputStream(tempFile);
			workbook.write(fileOut);
			fileOut.close();
			return new DeletingFileInputStream(tempFile);

		} catch (IOException e) {
			LOGGER.error("Error creating Excel", e);
			return null;

		} finally {
			try {
                if (fileOut != null) {
                    fileOut.close();
                }
			} catch (final IOException e) {
                // ignore
			}
            if (tempFile != null) {
                tempFile.deleteOnExit();
            }
		}
	}

    private void initialSheetSetup() {
		final PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
	}

    private void evaluateFormulas() {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
    }

    /**
     * Final formatting of the sheet upon completion of writing the data. For example, we can only
     * size the column widths once the data is in the report and the sheet knows how wide the data
     * is.
     */
    protected void autoSizeAllColumns() {
        for (int col = 0; col < columnNames.length; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    private void addHeaderRow() {
		headerRow = sheet.createRow(currentRow);
		Cell headerCell;
		headerRow.setHeightInPoints(40);
		for (int col = 0; col < columnNames.length; col++) {
			headerCell = headerRow.createCell(col);
			headerCell.setCellValue(createHelper
					.createRichTextString(columnTitles[col]));
			headerCell.setCellStyle(columnHeaderCellStyle);
			CellUtil.setAlignment(headerCell, workbook, CellStyle.ALIGN_LEFT);
		}
	}

	/**
	 * This method is called by addDataRow() to determine what CellStyle to use.
	 * By default we just return dataStyle which is either set to the default
	 * data style, or can be overriden by the user using setDataStyle().
	 * However, if the user wants to have different data items have different
	 * styles, then this method should be overriden. The parameters passed in
	 * are all potentially relevant items that may be used to determine what
	 * formatting to return, that are not accessible globally.
	 *
	 * @param col
	 *            the col
	 * 
	 * @return the data style
	 */
	private CellStyle getCellStyle(final int col) {
		final String columnName = columnNames[col];
		// get the basic style for the type of cell (i.e. data, header, total)
		// set the dataformat, regardless of the other style settings
		if (this.propertyExcelFormatMap.containsKey(columnName)) {
			final short df = dataFormat.getFormat(propertyExcelFormatMap
					.get(columnName));
			if (dataFormatCellStylesMap.containsKey(df)) {
				return dataFormatCellStylesMap.get(df);
			}
			final CellStyle retStyle = workbook.createCellStyle();
			retStyle.cloneStyleFrom(dataFormatCellStylesMap
					.get(doubleDataFormat));
			retStyle.setDataFormat(df);
			dataFormatCellStylesMap.put(df, retStyle);
			return retStyle;
		}
		final Class<?> columnType = columnTypes[col];
		if (isNumeric(columnType)) {
			return dataFormatCellStylesMap.get(doubleDataFormat);
		} else if (java.util.Date.class.isAssignableFrom(columnType)) {
			return dataFormatCellStylesMap.get(dateDataFormat);
		}
		return dataFormatCellStylesMap.get(doubleDataFormat);
	}

	private void prepareDefaults() {
		this.workbook = createWorkbook();
		this.sheetName = "Table Export";

		this.sheet = this.workbook.createSheet(this.sheetName);
		this.createHelper = this.workbook.getCreationHelper();
		this.dataFormat = this.workbook.createDataFormat();
		this.dateDataFormat = defaultDateDataFormat();
		this.doubleDataFormat = defaultDoubleDataFormat();

		this.doubleCellStyle = defaultDataCellStyle(this.workbook);
		this.doubleCellStyle.setDataFormat(doubleDataFormat);
		this.dataFormatCellStylesMap.put(doubleDataFormat, doubleCellStyle);

		this.dateCellStyle = defaultDataCellStyle(this.workbook);
		this.dateCellStyle.setDataFormat(this.dateDataFormat);
		this.dataFormatCellStylesMap.put(this.dateDataFormat,
				this.dateCellStyle);

		this.columnHeaderCellStyle = defaultHeaderCellStyle(this.workbook);
	}

	private Workbook createWorkbook() {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		workbook.setCompressTempFiles(true);
		return workbook;
	}

	private void applyExcelFormatForColumns() {
		setDoubleDataFormat("General");
		setDateDataFormat(DateFormatConverter.convert(Context.getLocale(),
				"dd.MM.yyyy"));

		for (int col = 0; col < excelFormats.length; col++) {
			String columnName = columnNames[col];
			String excelFormat = excelFormats[col];
			if (excelFormat == null) {
				Class<?> columnType = columnTypes[col];
				if (columnType != null) {
					if (Date.class.isAssignableFrom(columnType)) {
						String dateDisplayFormat = displayFormats[col];
						if (dateDisplayFormat != null) {
							excelFormat = DateFormatConverter.convert(
									Context.getLocale(), dateDisplayFormat);
						}
					}
				}
			}
			if (excelFormat != null) {
				setExcelFormatOfProperty(columnName, excelFormat);
			}
		}
	}

	private void setExcelFormatOfProperty(final Object propertyId,
			final String excelFormat) {
		if (this.propertyExcelFormatMap.containsKey(propertyId)) {
			this.propertyExcelFormatMap.remove(propertyId);
		}
		this.propertyExcelFormatMap.put(propertyId.toString(), excelFormat);
	}

	/**
	 * Returns the default header style. Obtained from:
	 * http://svn.apache.org/repos/asf/poi
	 * /trunk/src/examples/src/org/apache/poi/ss/examples/TimesheetDemo.java
	 * 
	 * @param wb
	 *            the wb
	 * 
	 * @return the cell style
	 */
	private CellStyle defaultHeaderCellStyle(final Workbook wb) {
		CellStyle style;
		final Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 11);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		style.setWrapText(true);
		return style;
	}

	/**
	 * Returns the default data cell style. Obtained from:
	 * http://svn.apache.org/repos/asf/poi
	 * /trunk/src/examples/src/org/apache/poi/ss/examples/TimesheetDemo.java
	 * 
	 * @param wb
	 *            the wb
	 * 
	 * @return the cell style
	 */
	private CellStyle defaultDataCellStyle(final Workbook wb) {
        final Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 11);
		CellStyle style;
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setFont(dataFont);
		style.setDataFormat(doubleDataFormat);
		return style;
	}

	private short defaultDoubleDataFormat() {
		return createHelper.createDataFormat().getFormat("0.00");
	}

	private short defaultDateDataFormat() {
		return createHelper.createDataFormat().getFormat("mm/dd/yyyy");
	}

	private void setDoubleDataFormat(final String excelDoubleFormat) {
		CellStyle prevDoubleDataStyle = null;
		if (dataFormatCellStylesMap.containsKey(doubleDataFormat)) {
			prevDoubleDataStyle = dataFormatCellStylesMap.get(doubleDataFormat);
			dataFormatCellStylesMap.remove(doubleDataFormat);
		}
		doubleDataFormat = createHelper.createDataFormat().getFormat(
				excelDoubleFormat);
		if (null != prevDoubleDataStyle) {
			doubleCellStyle = prevDoubleDataStyle;
			doubleCellStyle.setDataFormat(doubleDataFormat);
			dataFormatCellStylesMap.put(doubleDataFormat, doubleCellStyle);
		}
	}

	private void setDateDataFormat(final String excelDateFormat) {
		CellStyle prevDateDataStyle = null;
		if (dataFormatCellStylesMap.containsKey(dateDataFormat)) {
			prevDateDataStyle = dataFormatCellStylesMap.get(dateDataFormat);
			dataFormatCellStylesMap.remove(dateDataFormat);
		}
		dateDataFormat = createHelper.createDataFormat().getFormat(
				excelDateFormat);
		if (null != prevDateDataStyle) {
			dateCellStyle = prevDateDataStyle;
			dateCellStyle.setDataFormat(dateDataFormat);
			dataFormatCellStylesMap.put(dateDataFormat, dateCellStyle);
		}
	}

	/**
	 * Utility method to determine whether value being put in the Cell is
	 * numeric.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return true, if is numeric
	 */
	private boolean isNumeric(final Class<?> type) {
		if ((Integer.class.equals(type) || (int.class.equals(type)))) {
			return true;
		} else if ((Long.class.equals(type) || (long.class.equals(type)))) {
			return true;
		} else if ((Double.class.equals(type)) || (double.class.equals(type))) {
			return true;
		} else if ((Short.class.equals(type)) || (short.class.equals(type))) {
			return true;
		} else if ((Float.class.equals(type)) || (float.class.equals(type))) {
			return true;
		} else if ((BigDecimal.class.equals(type))
				|| (BigDecimal.class.equals(type))) {
			return true;
		}
		return false;
	}
}

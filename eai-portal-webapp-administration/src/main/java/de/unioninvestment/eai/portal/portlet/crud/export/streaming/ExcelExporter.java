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

import com.google.common.base.Strings;
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
     * Font that is used for Header and Data.
     */
    private final String fontName;


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
	private CellStyle columnHeaderCellStyle;
    private CellStyle[] cellStyles;
    private Short dateDataFormat, doubleDataFormat;
	protected Row headerRow;

	private int currentRow;

    private boolean[] multilineFlags;
	private String[] columnNames;
	private String[] columnTitles;
	private Class<?>[] columnTypes;
	private String[] displayFormats;
	private String[] excelFormats;
    private int excelRowAccessWindowSize;

    public ExcelExporter(int excelRowAccessWindowSize, String fontName) {
        this.excelRowAccessWindowSize = excelRowAccessWindowSize;
        this.fontName = fontName;
    }

    @Override
	public void begin(ExportInfo exportInfo) {
        this.multilineFlags = exportInfo.getMultilineFlags();
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
        return cellStyles[col];
	}

	private void prepareDefaults() {
		this.workbook = createWorkbook();
		this.sheetName = "Table Export";

		this.sheet = this.workbook.createSheet(this.sheetName);
		this.createHelper = this.workbook.getCreationHelper();
		this.dataFormat = this.workbook.createDataFormat();
		this.dateDataFormat = defaultDateDataFormat();
		this.doubleDataFormat = defaultDoubleDataFormat();

		this.columnHeaderCellStyle = defaultHeaderCellStyle(this.workbook);
	}

	private Workbook createWorkbook() {
		SXSSFWorkbook workbook = new SXSSFWorkbook(excelRowAccessWindowSize);
		workbook.setCompressTempFiles(true);
		return workbook;
	}

    private short getDataFormat(int col) {
        String excelFormat = excelFormats[col];
        if (excelFormat != null) {
            return dataFormat.getFormat(excelFormat);
        }
        Class<?> columnType = columnTypes[col];
        if (columnType != null) {
            if (Date.class.isAssignableFrom(columnType)) {
                String dateDisplayFormat = displayFormats[col];
                if (dateDisplayFormat != null) {
                    excelFormat = DateFormatConverter.convert(
                            Context.getLocale(), dateDisplayFormat);
                    return dataFormat.getFormat(excelFormat);
                }
                return dateDataFormat;
            }
        }
        return doubleDataFormat;
    }

	private void applyExcelFormatForColumns() {

        cellStyles = new CellStyle[columnNames.length];
        for (int col = 0; col < columnNames.length; col++) {
            short dataFormat = getDataFormat(col);
            cellStyles[col] = createCustomCellStyle(dataFormat, multilineFlags[col]);
        }
	}

    private CellStyle createCustomCellStyle(short dataFormat, boolean multiline) {
        CellStyle cellStyle = defaultDataCellStyle(workbook);
        cellStyle.setDataFormat(dataFormat);
        if (multiline) {
            cellStyle.setWrapText(true);
        }
        return cellStyle;
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
        if (!Strings.isNullOrEmpty(fontName)) {
            headerFont.setFontName(fontName);
        }
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
		CellStyle style;
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setWrapText(false);
		style.setDataFormat(doubleDataFormat);
        if (!Strings.isNullOrEmpty(fontName)) {
            final Font dataFont = wb.createFont();
            dataFont.setFontName(fontName);
            style.setFont(dataFont);
        }
		return style;
	}

	private short defaultDoubleDataFormat() {
		return createHelper.createDataFormat().getFormat("General");
	}

	private short defaultDateDataFormat() {
		return createHelper.createDataFormat().getFormat(
                DateFormatConverter.convert(Context.getLocale(), "dd.MM.yyyy"));
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

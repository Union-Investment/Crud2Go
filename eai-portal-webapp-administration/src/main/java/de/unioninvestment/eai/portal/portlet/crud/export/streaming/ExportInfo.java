/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * regarding copyright ownership.  The ASF licenses this file
 * distributed with this work for additional information
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.unioninvestment.eai.portal.portlet.crud.export.streaming;

/**
 * Information container for table metadata used by {@link Exporter}s.
 * 
 * @author cmj
 * 
 */
public interface ExportInfo {
	/**
	 * @return the column names
	 */
	String[] getColumnNames();

	/**
	 * @return the column titles if set, else the column names
	 */
	String[] getColumnTitles();

	/**
	 * @return the column types
	 */
	Class<Object>[] getColumnTypes();

	/**
	 * @return the display formats or <code>null</code>, if not set for a column
	 */
	String[] getDisplayFormats();

	/**
	 * @return the excel format strings or <code>null</code>, if not set for a
	 *         column
	 */
	String[] getExcelFormats();
}

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

/**
 * Interface for export target formats.
 * 
 * @author cmj
 * 
 */
public interface Exporter {

	/**
	 * Called first.
	 * 
	 * @param info
	 *            about the table to export
	 */
	void begin(ExportInfo info);

	/**
	 * Called once per row.
	 * 
	 * @param itemInfo
	 *            about the values of the next row
	 */
	void addRow(ItemInfo itemInfo);

	/**
	 * @return an {@link InputStream} containing the serialized data
	 */
	InputStream getInputStream();

}

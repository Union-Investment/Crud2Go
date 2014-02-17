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

/**
 * Inteface for export item information.
 * 
 * @author cmj
 */
public interface ItemInfo {

	/**
	 * @param columnName
	 * @return the value for the given column
	 */
	Object getValue(String columnName);

	/**
	 * @param columnName
	 * @return <code>true</code>, if this item is a combobox and
	 *         {@link #getTitle(String)} may return the value to export.
	 */
	boolean isComboBox(String columnName);

	/**
	 * @param columnName
	 * @return the dropdown display title for the given column
	 */
	String getTitle(String columnName);

}

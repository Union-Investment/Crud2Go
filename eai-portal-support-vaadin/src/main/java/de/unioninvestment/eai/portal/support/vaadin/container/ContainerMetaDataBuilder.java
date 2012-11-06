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
package de.unioninvestment.eai.portal.support.vaadin.container;

import groovy.util.BuilderSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builder für Objekte vom Typ {@link MetaData}.
 */
public class ContainerMetaDataBuilder extends BuilderSupport {

	private MetaData metaData;

	private List<Column> columns = new ArrayList<Column>();

	public MetaData createMetaData() {
		metaData = new MetaData(columns, true, true, true, true, true);

		return metaData;
	}

	@Override
	protected void setParent(Object parent, Object child) {
		// wird nicht benötigt

	}

	@Override
	protected Object createNode(Object name) {
		// wird nicht benötigt
		return null;
	}

	@Override
	protected Object createNode(Object name, Object value) {
		// wird nicht benötigt
		return null;
	}

	@Override
	protected Object createNode(Object name,
			@SuppressWarnings("rawtypes") Map attributes) {
		Class<?> type = (Class<?>) attributes.get("type");
		boolean readOnly = attributes.containsKey("readOnly") ? (Boolean) attributes
				.get("readOnly") : false;
		boolean required = attributes.containsKey("required") ? (Boolean) attributes
				.get("required") : false;
		boolean partOfPrimaryKey = attributes.containsKey("partOfPrimaryKey") ? (Boolean) attributes
				.get("partOfPrimaryKey") : false;
		String order = attributes.containsKey("order") ? (String) attributes
				.get("order") : null;

		Column column = new Column((String) name, type, readOnly, required,
				partOfPrimaryKey, order);
		columns.add(column);

		return null;
	}

	@Override
	protected Object createNode(Object name, @SuppressWarnings("rawtypes") Map attributes, Object value) {
		// wird nicht benötigt
		return null;
	}

}

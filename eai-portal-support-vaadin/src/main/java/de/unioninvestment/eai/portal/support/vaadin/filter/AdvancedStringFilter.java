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
package de.unioninvestment.eai.portal.support.vaadin.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.SimpleStringFilter;

/**
 * Filter für Container-Inhalte vom Typ String.
 */
public class AdvancedStringFilter implements Filter {

	private static final long serialVersionUID = 1L;

	private final boolean onlyMatchPostfix;
	private final SimpleStringFilter delegateFilter;
	private final Object propertyId;
	private final boolean ignoreCase;
	private final String filterString;

	public AdvancedStringFilter(Object propertyId, String filterString,
			boolean ignoreCase, boolean onlyMatchPrefix,
			boolean onlyMatchPostfix) {
		super();
		this.propertyId = propertyId;
		this.ignoreCase = ignoreCase;
		this.filterString = ignoreCase ? filterString.toLowerCase()
				: filterString;
		this.onlyMatchPostfix = onlyMatchPostfix;
		delegateFilter = new SimpleStringFilter(propertyId, filterString,
				ignoreCase, onlyMatchPrefix);
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		if (!onlyMatchPostfix) {
			return delegateFilter.passesFilter(itemId, item);
		}

		final Property p = item.getItemProperty(propertyId);
		if (p == null || p.getValue() == null) {
			return false;
		}
		final String value = ignoreCase ? p.getValue().toString().toLowerCase()
				: p.getValue().toString();
		if (!value.endsWith(filterString)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return this.propertyId.equals(propertyId);
	}

	public boolean isOnlyMatchPrefix() {
		return delegateFilter.isOnlyMatchPrefix();
	}

	public boolean isOnlyMatchPostfix() {
		return this.onlyMatchPostfix;
	}

	public Object getPropertyId() {
		return this.propertyId;
	}

	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	public String getFilterString() {
		return this.filterString;
	}

}

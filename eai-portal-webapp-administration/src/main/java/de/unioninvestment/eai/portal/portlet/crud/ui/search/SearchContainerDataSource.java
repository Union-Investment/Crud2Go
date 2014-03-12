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

package de.unioninvestment.eai.portal.portlet.crud.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.OptionHandler;

class SearchContainerDataSource extends
		AbstractBeanContainer<String, SearchContainerDataSource.Entry>
		implements Container, Container.Indexed, Container.Filterable {

	private static final long serialVersionUID = 1L;

	private Collection<String> options = new ArrayList<String>();

	private OptionHandler handler = new OptionHandler() {
		@Override
		public List<String> getOptions(String filterString) {
			return new ArrayList<String>();
		}
	};

	public static class Entry {
		String value;

		public Entry(String value) {
			super();
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	protected SearchContainerDataSource() {
		super(Entry.class);
		setBeanIdResolver(new BeanIdResolver<String, SearchContainerDataSource.Entry>() {
			@Override
			public String getIdForBean(Entry bean) {
				return bean.value;
			}
		});
	}

	@Override
	public void addContainerFilter(Filter filter)
			throws UnsupportedFilterException {
		updateOptions(((SimpleStringFilter) filter).getFilterString());
	}

	public void updateOptions(String filterString) {
		Collection<String> newOptions = handler.getOptions(filterString);
		if (!options.equals(newOptions)) {
			options = newOptions;
			removeAllItems();
			addAll(mapToEntries(options));
		}
	}

	private List<Entry> mapToEntries(Collection<String> options) {
		List<Entry> result = new ArrayList<Entry>(options.size());
		for (String option : options) {
			result.add(new Entry(option));
		}
		return result;
	}

	public void addOption(String newItemCaption) {
		options.add(newItemCaption);
		addBean(new Entry(newItemCaption));
	}

	public void setOptionHandler(OptionHandler handler) {
		this.handler = handler;
		updateOptions("");
	}
}

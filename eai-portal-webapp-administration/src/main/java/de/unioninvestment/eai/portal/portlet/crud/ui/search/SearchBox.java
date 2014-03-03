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

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;

import java.util.Collection;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import com.google.common.base.Strings;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class SearchBox extends ComboBox {

	private static final long serialVersionUID = 1L;

	public interface OptionHandler {
		Collection<String> getOptions(String filterString);
	}

	public interface QuerySearchHandler {
		void search(Query query);
	}

	private SearchContainerDataSource container;
	private String previousFilterString;
	private String caseSensitiveFilterstring;

	private final String[] masterFields;
	private QuerySearchHandler searchHandler;

	private Object lastExpression;

	private String lastNewItem;

	public SearchBox(String[] masterFields, QuerySearchHandler searchHandler) {
		this.masterFields = masterFields;
		this.searchHandler = searchHandler;

		container = new SearchContainerDataSource();

		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setItemCaptionPropertyId("value");
		setContainerDataSource(container);
		setImmediate(true);
		setNewItemsAllowed(true);
		setNullSelectionAllowed(true);

		addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				handleValueChange(event);
			}

		});
		setNewItemHandler(new NewItemHandler() {

			@Override
			public void addNewItem(String newItemCaption) {
				handleAddNewItem(newItemCaption);
			}

		});
	}

	public void setOptionHandler(OptionHandler handler) {
		container.setOptionHandler(handler);
	}

	@Override
	public void setValue(Object newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		if (newValue != null && !containsId(newValue)) {
			handleAddNewItem((String) newValue);
		}
		super.setValue(newValue);
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		handleChangedFilterString(variables);
		super.changeVariables(source, variables);
	}

	private void handleChangedFilterString(Map<String, Object> variables) {
		String newFilterString = (String) variables.get("filter");
		if (newFilterString != null) {
			previousFilterString = caseSensitiveFilterstring;
			caseSensitiveFilterstring = newFilterString;

			if (!Strings.isNullOrEmpty(previousFilterString)) {
				if (newFilterString.length() > previousFilterString.length()) {
					String newChars = newFilterString
							.substring(previousFilterString.length());
					int spaceIdx = newChars.indexOf(' ');
					if (previousFilterString.charAt(previousFilterString
							.length() - 1) != ' ' && spaceIdx >= 0) {
						String expression = caseSensitiveFilterstring
								.substring(0, previousFilterString.length()
										+ spaceIdx);

						search(expression, true);
					}
				}
			}
		}
	}

	@Override
	protected Filter buildFilter(String filterString,
			FilteringMode filteringMode) {
		if (null != filterString && !"".equals(filterString)) {
			return new SimpleStringFilter(getItemCaptionPropertyId(),
					caseSensitiveFilterstring, false, true);
		}
		return null;
	}

	private void continueEditing() {
		focus();
	}

	private void handleAddNewItem(String newItemCaption) {
		container.addOption(newItemCaption);
		lastNewItem = newItemCaption;
		setValue(newItemCaption);
	}

	private void handleValueChange(
			com.vaadin.data.Property.ValueChangeEvent event) {
		continueEditing();

		String expression = (String) event.getProperty().getValue();
		if (expression != null) {
			boolean isItemFromOptions = !expression.equals(lastNewItem);
			search(expression, isItemFromOptions);
		} else if (lastExpression != null) {
			searchHandler.search(null);
			lastExpression = null;
		}
	}

	protected void search(String expression, boolean ignoreInvalidQuery) {
		if (!expression.equals(lastExpression)) {
			Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
			QueryParser luceneParser = new MultiFieldQueryParser(
					Version.LUCENE_46, masterFields, analyzer);
			try {
				Query query = luceneParser.parse(expression);
				searchHandler.search(query);
				lastExpression = expression;

			} catch (ParseException e) {
				if (!ignoreInvalidQuery) {
					Notification.show(getMessage("portlet.crud.error.compoundsearch.invalidQuery", expression));
				}
			}
		}
	}
}

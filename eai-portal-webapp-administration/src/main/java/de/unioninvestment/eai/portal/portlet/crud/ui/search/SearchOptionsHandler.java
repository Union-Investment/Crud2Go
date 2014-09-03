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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.OptionHandler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class SearchOptionsHandler implements OptionHandler {

	private static final int DEFAULT_MAXIMUM_FIELD_OPTIONS = 100;

	private static final class FilterString {
		private static final String FIELDNAME_REGEX = "[a-zA-Z][a-zA-Z0-9_]*";
		private static final Pattern IN_RANGE_BEFORE_TO = Pattern
				.compile("[\\[\\{][^ \\)]+ $");
		private static final Pattern IN_FIELD_PHRASE = Pattern
				.compile("(\\A|[ \\(])(" + FIELDNAME_REGEX + "):\"([^\"]*)$");
		private static final Pattern IN_FIELD_TERM = Pattern
				.compile("(\\A|[ \\(])(" + FIELDNAME_REGEX + "):([^ ]*)$");
		private static final Pattern IN_DEFAULT_PHRASE = Pattern
				.compile("(\\A|[ \\(])\"([^\"]*)$");

		private String all;

		private String head = null;
		private String tail = null;

		private String partialFieldName = null;
		private String fieldname = null;
		private String partialFieldValue = null;
		private String partialDefaultValue = null;

		public FilterString(String all) {
			this.all = all == null ? "" : all;
			analyze();
		}

		private void analyze() {
			Matcher phraseMatcher = IN_FIELD_PHRASE.matcher(all);
			if (phraseMatcher.find()) {
				analyzePhraseOrTerm(phraseMatcher);
				return;
			}
			Matcher termMatcher = IN_FIELD_TERM.matcher(all);
			if (termMatcher.matches()) {
				analyzePhraseOrTerm(termMatcher);
				return;
			}
			Matcher defaultPhraseMatcher = IN_DEFAULT_PHRASE.matcher(all);
			if (defaultPhraseMatcher.find()) {
				analyzeDefaultPhraseOrTerm(defaultPhraseMatcher);
				return;
			}
			if (all.endsWith("(") || all.endsWith(" ") || all.equals("")) {
				head = all;
				tail = "";
				partialFieldName = "";
				partialDefaultValue = "";
			} else {
				int lastSpaceIndex = all.lastIndexOf(' ');
				head = lastSpaceIndex == -1 ? "" : all.substring(0,
						lastSpaceIndex + 1);
				tail = all.substring(lastSpaceIndex + 1);
				partialFieldName = tail;
				partialDefaultValue = tail;
			}
		}

		private void analyzeDefaultPhraseOrTerm(Matcher matcher) {
			String match = matcher.group(0);
			tail = match.substring(matcher.group(1).length());
			head = all.substring(0, all.length() - tail.length());
			partialDefaultValue = matcher.group(2);
		}

		private void analyzePhraseOrTerm(Matcher matcher) {
			String match = matcher.group(0);
			tail = match.substring(matcher.group(1).length());
			head = all.substring(0, all.length() - tail.length());
			fieldname = matcher.group(2);
			partialFieldValue = matcher.group(3);
		}

		public boolean isInRangeBeforeTO() {
			return IN_RANGE_BEFORE_TO.matcher(all).find();
		}

		public boolean isInRangeAfterTO() {
			return all.endsWith(" TO ");
		}

		public boolean isInFieldName() {
			return partialFieldName != null && fieldname == null;
		}

		public String getPartialLowercaseFieldName() {
			return partialFieldName == null ? null : partialFieldName
					.toLowerCase();
		}

		public String getLowercaseFieldname() {
			return fieldname == null ? null : fieldname.toLowerCase();
		}

		public String getHead() {
			return head;
		}

		public String getPartialFieldValue() {
			return partialFieldValue;
		}

		public boolean isInFieldValue() {
			return partialFieldValue != null;
		}

		public boolean isInDefaultValue() {
			return partialDefaultValue != null;
		}

		public String getPartialDefaultValue() {
			return partialDefaultValue;
		}
	}

	private static final class DuplicateOperators implements Predicate<String> {
		private String head;

		public DuplicateOperators(String head) {
			this.head = head;
		}

		@Override
		public boolean apply(String input) {
			if (head != null) {
				if (head.endsWith(" AND ") || head.endsWith(" OR ")) {
					if (input.startsWith("AND ") || input.startsWith("OR ")) {
						return false;
					}
				}
			}
			return true;
		}
	}

	private static final class InvalidOperators implements Predicate<String> {
		private String filterString;

		public InvalidOperators(String filterString) {
			this.filterString = filterString;
		}

		@Override
		public boolean apply(String input) {
			if (input.startsWith("AND ") || input.startsWith("OR ")) {
				if (Strings.isNullOrEmpty(filterString)) {
					return false;
				} else if (filterString.trim().endsWith("(")) {
					return false;
				}
			}
			return true;
		}
	}

	private static final class AppendString implements Function<String, String> {
		private final String colon;

		private AppendString(String colon) {
			this.colon = colon;
		}

		@Override
		public String apply(String input) {
			return input + colon;
		}
	}

	private static final class PrependString implements
			Function<String, String> {
		private final String head;

		private PrependString(String head) {
			this.head = head;
		}

		@Override
		public String apply(String input) {
			return head + input;
		}
	}

	private static final class LowerStartsWith implements Predicate<String> {
		private final String lowerTail;

		private LowerStartsWith(String lowerTail) {
			this.lowerTail = lowerTail;
		}

		@Override
		public boolean apply(String input) {
			return input != null && input.toLowerCase().startsWith(lowerTail);
		}
	}

	final static List<String> keywords = asList("AND ", "OR ", "NOT ");

	private TableColumns fields;
	private int maximumFieldOptions = DEFAULT_MAXIMUM_FIELD_OPTIONS;
	Set<String> opsAndAttrsWithColon;

	private List<String> defaultSelectionFieldNames;

	private Map<String, String> columnNamesMapping;

	public SearchOptionsHandler(TableColumns fields) {
		this.fields = fields;
		columnNamesMapping = fields.getLowerCaseColumnNamesMapping();
		opsAndAttrsWithColon = new TreeSet<String>(keywords);
		opsAndAttrsWithColon.addAll( //
				FluentIterable.from(fields.getSearchableColumnPrefixes())
						.transform(new AppendString(":")).toList());

		Map<String,String> defaultNames = fields
				.getDefaultSearchablePrefixes();
		defaultSelectionFieldNames = Lists
				.newArrayListWithCapacity(defaultNames.size());
		for (Map.Entry<String,String> defaultName : defaultNames.entrySet()) {
			if (fields.isSelection(defaultName.getKey())) {
				defaultSelectionFieldNames.add(defaultName.getValue());
			}
		}
	}

	@Override
	public Set<String> getOptions(String all) {
		Set<String> options = new TreeSet<String>();
		FilterString filterString = new FilterString(all);

		if (filterString.isInRangeBeforeTO()) {
			options.add(all + "TO ");
		} else if (filterString.isInRangeAfterTO()) {
			// empty list
		} else if (filterString.isInFieldName()) {
			String lowerTail = filterString.getPartialLowercaseFieldName();
			options.addAll(FluentIterable.from(opsAndAttrsWithColon)
					//
					.filter(new LowerStartsWith(lowerTail))
					//
					.filter(new DuplicateOperators(filterString.getHead()))
					//
					.filter(new InvalidOperators(all))
					//
					.transform(new PrependString(filterString.getHead()))
					.toList());
		} else if (filterString.isInFieldValue()) {
			// lastColonIndex >= 0
			String lowercaseFieldName = filterString.getLowercaseFieldname();
			String columnName = columnNamesMapping.get(lowercaseFieldName);
			if (columnName != null) {
    			String fieldName = fields.get(columnName).getSearchPrefix();
				String partialFieldvalue = filterString.getPartialFieldValue();
				addFilteredOptionListOptions(options, filterString,
						lowercaseFieldName, partialFieldvalue,
						filterString.getHead() + fieldName + ":");
			}
		}
		if (filterString.isInDefaultValue()) {
			for (String defaultName : defaultSelectionFieldNames) {
				addFilteredOptionListOptions(options, filterString,
						defaultName.toLowerCase(),
						filterString.getPartialDefaultValue(),
						filterString.getHead());
			}
		}
		return options;
	}

	private void addFilteredOptionListOptions(Set<String> options,
			FilterString filterString, String lowercaseFieldName,
			String partialFieldvalue, String prefix) {
		String fieldname = columnNamesMapping.get(lowercaseFieldName);
		if (fieldname != null && fields.isSelection(fieldname)) {
            Map<String, String> fieldOptions = fields.getDropdownSelections(
					fieldname, partialFieldvalue, maximumFieldOptions);
			if (fieldOptions != null) {
				for (String fieldOption : fieldOptions.values()) {
					options.add(prefix + "\"" + fieldOption + "\" ");
				}
			}
		}
	}

	public void setMaximumFieldOptions(int maximumFieldOptions) {
		this.maximumFieldOptions = maximumFieldOptions;
	}
}
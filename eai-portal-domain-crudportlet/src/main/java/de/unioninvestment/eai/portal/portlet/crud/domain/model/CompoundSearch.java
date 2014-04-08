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

package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.vaadin.data.util.converter.Converter.ConversionException;

import de.unioninvestment.eai.portal.portlet.crud.config.CompoundSearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CompoundSearchDetailsConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.IsNull;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Not;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Wildcard;
import de.unioninvestment.eai.portal.portlet.crud.domain.search.AsIsAnalyzer;
import de.unioninvestment.eai.portal.portlet.crud.domain.search.SearchableTablesFinder;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;
import de.unioninvestment.eai.portal.support.vaadin.date.GermanDateFormats;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.support.NumberFormatter;

/**
 * Repräsentation der Compound-Suche. Konvertiert eine Suche in Lucene-Syntax in
 * Vaadin-Containerfilter.
 * 
 * @author cmj
 */
@SuppressWarnings("serial")
public class CompoundSearch extends Panel {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompoundSearch.class);

	SearchableTablesFinder searchableTablesFinder = new SearchableTablesFinder();

	private CompoundSearchConfig config;

	private GermanDateFormats dateFormats = new GermanDateFormats();

	protected EventRouter<CompoundQueryChangedEventHandler, CompoundQueryChangedEvent> eventRouter = new EventRouter<CompoundQueryChangedEventHandler, CompoundQueryChangedEvent>();

	private TableColumns searchableColumns;

	public CompoundSearch(CompoundSearchConfig config) {
		super(notNullDetails(config));
		this.config = config;
	}

	private static CompoundSearchDetailsConfig notNullDetails(
			CompoundSearchConfig config) {
		return config.getDetails() != null ? config.getDetails()
				: new CompoundSearchDetailsConfig();
	}

	public String getId() {
		return config.getId();
	}

	/**
	 * @return searchable Columns sorted by name
	 */
	public TableColumns getSearchableColumns() {
		if (searchableColumns == null) {
			final HashMap<String, TableColumn> unorderedColumns = new HashMap<String, TableColumn>();
			for (Table table : getTables()) {
				if (table.getColumns() != null) {
					for (TableColumn column : table.getColumns()) {
						if (column.getSearchable() == Searchable.DEFAULT
								|| !unorderedColumns.containsKey(column
										.getName())) {
							unorderedColumns.put(column.getName(), column);
						}
					}
				}
			}
			ArrayList<TableColumn> listOfColumns = new ArrayList<TableColumn>(
					unorderedColumns.values());
			sort(listOfColumns, new Comparator<TableColumn>() {
				@Override
				public int compare(TableColumn o1, TableColumn o2) {
					if (!o1.getSearchable().equals(o2.getSearchable())) {
						return o1.getSearchable().equals(Searchable.DEFAULT) ? -1
								: 1;
					} else {
						return o1.getName().compareTo(o2.getName());
					}
				}
			});
			searchableColumns = new TableColumns(listOfColumns);
		}
		return searchableColumns;
	}

	Filter getFiltersForTable(Table table, Query query) {
		if (query instanceof BooleanQuery) {
			return convertBooleanQuery(table, (BooleanQuery) query);
		} else if (query instanceof TermQuery) {
			return convertTermQuery(table, (TermQuery) query);
		} else if (query instanceof PrefixQuery) {
			return convertPrefixQuery(table, (PrefixQuery) query);
		} else if (query instanceof WildcardQuery) {
			return convertWildcardQuery(table, (WildcardQuery) query);
		} else if (query instanceof TermRangeQuery) {
			return convertTermRangeQuery(table, (TermRangeQuery) query);
		}
		throw new BusinessException(
				"portlet.crud.error.compoundsearch.unsupportedQuerySyntax",
				query.toString());
	}

	private Filter convertWildcardQuery(Table table, WildcardQuery query) {
		Term wildcard = query.getTerm();
		String columnName = caseCorrectedFieldName(wildcard.field());
		if (table.getContainer().getType(columnName) == null) {
			return null;
		}
		return new Wildcard(columnName, wildcard.text(), false);
	}

	private Filter convertPrefixQuery(Table table, PrefixQuery query) {
		Term prefix = query.getPrefix();
		String columnName = caseCorrectedFieldName(prefix.field());
		if (table.getContainer().getType(columnName) == null) {
			return null;
		}
		return new StartsWith(columnName, prefix.text(), false);
	}

	private Filter convertTermRangeQuery(Table table,
			TermRangeQuery termRangeQuery) {
		String columnName = caseCorrectedFieldName(termRangeQuery.getField());
		Class<?> columnType = table.getContainer().getType(columnName);
		if (columnType == null) {
			return null;
		}

		String lowerText = termRangeQuery.getLowerTerm().utf8ToString();
		String upperText = termRangeQuery.getUpperTerm().utf8ToString();
		Filter lowerFilter;
		Filter upperFilter;

		if (Number.class.isAssignableFrom(columnType)) {
			Number lowerNumber = convertTextToNumber(table, columnName,
					columnType, lowerText);
			lowerFilter = new Greater(columnName, lowerNumber,
					termRangeQuery.includesLower());

			Number upperNumber = convertTextToNumber(table, columnName,
					columnType, upperText);
			upperFilter = new Less(columnName, upperNumber,
					termRangeQuery.includesUpper());
		} else if (Date.class.isAssignableFrom(columnType)) {
			Date lowerDate = convertTextToDate(columnName, lowerText,
					dateFormats, !termRangeQuery.includesLower());
			lowerFilter = new Greater(columnName, DateUtils.adjustDateType(
					lowerDate, columnType), true);
			Date upperDate = convertTextToDate(columnName, upperText,
					dateFormats, termRangeQuery.includesUpper());
			upperFilter = new Less(columnName, DateUtils.adjustDateType(
					upperDate, columnType), false);

		} else { /* String */
			lowerFilter = new Greater(columnName, lowerText,
					termRangeQuery.includesLower());
			upperFilter = new Less(columnName, upperText,
					termRangeQuery.includesUpper());
		}
		return new All(asList(lowerFilter, upperFilter));
	}

	private Date convertTextToDate(String columnName, String text,
			GermanDateFormats formats, boolean returnEndDate) {
		try {
			String datePattern = formats.find(text);
			Date date = new SimpleDateFormat(datePattern, Locale.GERMANY)
					.parse(text);
			if (returnEndDate) {
				int resolution = DateUtils.getResolution(datePattern);
				date = DateUtils.getEndDate(date, resolution);
			}
			return date;

		} catch (ParseException e) {
			throw new BusinessException(
					"portlet.crud.error.compoundsearch.dateConversionFailed",
					columnName, text, e.getMessage());
		}
	}

	private Filter convertTermQuery(Table table, TermQuery query) {
		Term term = query.getTerm();
		String columnName = caseCorrectedFieldName(term.field());
		Class<?> columnType = table.getContainer().getType(columnName);
		if (columnType == null) {
			return null;
		}
		String text = term.text();
		boolean selection = getSearchableColumns().isSelection(columnName);
		if (selection) {
			text = getFieldOptionKey(columnName, text);
			if (text == null) {
				throw new BusinessException(
						"portlet.crud.error.compoundsearch.invalidSelection",
						columnName, term.text());
			}
		}
		if (Number.class.isAssignableFrom(columnType)) {
			Number numberValue = convertTextToNumber(table, columnName,
					columnType, text);
			return new Equal(columnName, numberValue);
		} else if (Date.class.isAssignableFrom(columnType)) {
			Date lowerDate = convertTextToDate(columnName, text, dateFormats,
					false);
			Date upperDate = convertTextToDate(columnName, text, dateFormats,
					true);
			Filter lowerFilter = new Greater(columnName,
					DateUtils.adjustDateType(lowerDate, columnType), true);
			Filter upperFilter = new Less(columnName, DateUtils.adjustDateType(
					upperDate, columnType), false);
			return new All(asList(lowerFilter, upperFilter));
		} else {
			if (selection) {
				return new Equal(columnName, text);
			}
			if (text.equals("*")) {
				return new Not(asList((Filter) new IsNull(columnName)));
			} else if (hasWildcards(text)) {
				return new Wildcard(columnName, text, false);
			} else {
				return new StartsWith(columnName, text, false);
			}
		}
	}

	private String caseCorrectedFieldName(String fieldName) {
		Map<String, String> mapping = getSearchableColumns()
				.getLowerCaseColumnNamesMapping();
		String realFieldName = mapping.get(fieldName.toLowerCase());
		return realFieldName != null ? realFieldName : fieldName;
	}

	private String getFieldOptionKey(String columnName, String title) {
		return getSearchableColumns().getDropdownSelections(columnName).getKey(
				title, null);
	}

	private Number convertTextToNumber(Table table, String columnName,
			Class<?> columnType, String text) {
		try {
			NumberFormatter numberFormatter = new NumberFormatter(
					(NumberFormat) table.getContainer().getFormat(columnName));
			Locale locale = Context.getLocale();
			@SuppressWarnings("unchecked")
			Number numberValue = numberFormatter.convertToModel(text,
					(Class<? extends Number>) columnType, locale);
			return numberValue;
		} catch (ConversionException e) {
			throw new BusinessException(
					"portlet.crud.error.compoundsearch.numberConversionFailed",
					columnName, text, e.getMessage());
		}
	}

	private Filter convertBooleanQuery(Table table, BooleanQuery query) {
		BooleanQuery booleanQuery = (BooleanQuery) query;
		if (isBooleanMixed(booleanQuery)) {
			throw new BusinessException(
					"portlet.crud.error.compoundsearch.mixedBooleansProhibited",
					booleanQuery);
		} else if (isBooleanAND(booleanQuery)) {
			List<Filter> subList = convertBooleanClauses(table, booleanQuery,
					false);
			if (subList.size() == 0) {
				return null;
			} else if (subList.size() == 1) {
				return subList.get(0);
			} else {
				return new All(subList);
			}
		} else {
			List<Filter> subList = convertBooleanClauses(table, booleanQuery,
					true);
			if (subList.size() == 0) {
				return null;
			} else if (subList.size() == 1) {
				return subList.get(0);
			} else {
				return new Any(subList);
			}
		}
	}

	private boolean isBooleanMixed(BooleanQuery booleanQuery) {
		boolean hasMustClause = false;
		boolean hasShouldClause = false;
		// boolean hasMustNotClause = false;
		for (BooleanClause clause : booleanQuery.getClauses()) {
			if (clause.getOccur() == Occur.MUST) {
				hasMustClause = true;
			} else if (clause.getOccur() == Occur.MUST_NOT) {
				// hasMustNotClause = true;
			} else if (clause.getOccur() == Occur.SHOULD) {
				hasShouldClause = true;
			} else {
				throw new UnsupportedOperationException("Unknown:"
						+ clause.getOccur());
			}
		}
		return hasMustClause && hasShouldClause;
	}

	private boolean isMustClause(BooleanClause clause) {
		boolean isMustClause = (clause.getOccur() == Occur.MUST || clause
				.getOccur() == Occur.MUST_NOT);
		return isMustClause;
	}

	private boolean isBooleanAND(BooleanQuery booleanQuery) {
		for (BooleanClause clause : booleanQuery.getClauses()) {
			if (!isMustClause(clause)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasWildcards(String text) {
		return text.contains("*") || text.contains("?");
	}

	private List<Filter> convertBooleanClauses(Table table,
			BooleanQuery booleanQuery, boolean ignorePartialErrors) {
		List<Filter> subList = new ArrayList<Filter>(
				booleanQuery.getClauses().length);
		List<RuntimeException> errors = new LinkedList<RuntimeException>();
		for (BooleanClause clause : booleanQuery.clauses()) {
			try {
				Filter subFilter = getFiltersForTable(table, clause.getQuery());
				if (subFilter != null) {
					if (clause.isProhibited()) {
						subFilter = new Not(asList(subFilter));
					}
					subList.add(subFilter);
				}
			} catch (RuntimeException e) {
				errors.add(e);
			}
		}
		if (ignorePartialErrors) {
			if (errors.size() < booleanQuery.clauses().size()) {
				for (RuntimeException e : errors) {
					LOGGER.info("Ignoring error in 'OR' clause: "
							+ e.getMessage());
				}
				return subList;
			}
		}
		if (errors.size() > 0) {
			throw errors.get(0);
		}
		return subList;
	}

	/**
	 * @return all tables that have column definitions
	 */
	private List<Table> getTables() {
		List<Table> searchableTables = searchableTablesFinder
				.findSearchableTables(this, config.getTables());
		removeTablesWithoutColumnDefinition(searchableTables);
		return searchableTables;
	}

	private void removeTablesWithoutColumnDefinition(
			List<Table> searchableTables) {
		for (Iterator<Table> it = searchableTables.iterator(); it.hasNext();) {
			if (it.next().getColumns() == null) {
				it.remove();
			}
		}
	}

	/**
	 * Search according to the query string on all matching tables.
	 * 
	 * @param queryString
	 */
	public void search(String queryString) {
		Map<Table, Filter> filtersMap = prepareQuery(queryString);
		applyFiltersToTables(filtersMap);
		fireQueryChangedEvent(queryString);
	}

	private void applyFiltersToTables(Map<Table, Filter> filtersMap) {
		for (Entry<Table, Filter> entry : filtersMap.entrySet()) {
			List<Filter> filters = entry.getValue() != null ? asList(entry
					.getValue()) : Collections.<Filter> emptyList();
			entry.getKey().getContainer().replaceFilters(filters, false, true);
		}
	}

	public void addQueryChangedEventHandler(
			CompoundQueryChangedEventHandler handler) {
		eventRouter.addHandler(handler);
	}

	public void removeQueryChangedEventHandler(
			CompoundQueryChangedEventHandler handler) {
		eventRouter.removeHandler(handler);
	}

	void fireQueryChangedEvent(String queryString) {
		eventRouter.fireEvent(new CompoundQueryChangedEvent(this, queryString));
	}

	public boolean isValidQuery(String queryString) {
		try {
			prepareQuery(queryString);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	private Map<Table, Filter> prepareQuery(String queryString) {
		Query query = parseQuery(queryString);
		return createTableFiltersMap(query);
	}

	private Map<Table, Filter> createTableFiltersMap(Query query) {
		Map<Table, Filter> results = new HashMap<Table, Filter>();
		for (Table table : getTables()) {
			if (query == null) {
				results.put(table, null);
			} else {
				Filter filters = getFiltersForTable(table, query);
				results.put(table, filters);
			}
		}
		return results;
	}

	private Query parseQuery(String queryString) {
		if (Strings.isNullOrEmpty(queryString)) {
			return null;
		}

		Collection<String> defaultFields = getSearchableColumns()
				.getDefaultSearchableColumnNames();
		String[] defaultFieldsArray = defaultFields
				.toArray(new String[defaultFields.size()]);

		QueryParser luceneParser = new MultiFieldQueryParser(Version.LUCENE_46,
				defaultFieldsArray, new AsIsAnalyzer());
		try {
			return luceneParser.parse(queryString);

		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			throw new BusinessException(
					"portlet.crud.error.compoundsearch.invalidQuery",
					queryString);
		}
	}

	public boolean isDefault(String name) {
		TableColumn column = getSearchableColumns().get(name);
		return column != null && column.getSearchable() == Searchable.DEFAULT;
	}

}

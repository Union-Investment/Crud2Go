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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;

import com.google.common.base.Joiner;
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

	SearchableTablesFinder searchableTablesFinder = new SearchableTablesFinder();

	private CompoundSearchConfig config;

	private GermanDateFormats dateFormats = new GermanDateFormats();

	protected EventRouter<CompoundQueryChangedEventHandler, CompoundQueryChangedEvent> eventRouter = new EventRouter<CompoundQueryChangedEventHandler, CompoundQueryChangedEvent>();

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
	public Map<String, TableColumn> getSearchableColumns() {
		TreeMap<String, TableColumn> searchableColumns = new TreeMap<String, TableColumn>();
		for (Table table : getTables()) {
			if (table.getColumns() != null) {
				for (TableColumn column : table.getColumns()) {
					if (column.getSearchable() == Searchable.DEFAULT
							|| !searchableColumns.containsKey(column.getName())) {
						searchableColumns.put(column.getName(), column);
					}
				}
			}
		}
		return searchableColumns;
	}

	/**
	 * @return all field names that can be used inside lucene queries
	 */
	public Collection<String> getSearchableFields() {
		TreeSet<String> searchableFields = new TreeSet<String>();
		for (Table table : getTables()) {
			searchableFields.addAll(table.getColumns()
					.getSearchableColumnNames());
		}
		return searchableFields;
	}

	/**
	 * @return field names that are searched by default if not named explicitly
	 */
	public Collection<String> getDefaultFields() {
		TreeSet<String> defaultFields = new TreeSet<String>();
		for (Table table : getTables()) {
			defaultFields.addAll(table.getColumns()
					.getDefaultSearchableColumnNames());
		}
		return defaultFields;
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
		} else if (query instanceof PhraseQuery) {
			return convertPhraseQuery(table, (PhraseQuery) query);
		} else if (query instanceof TermRangeQuery) {
			return convertTermRangeQuery(table, (TermRangeQuery) query);
		}
		throw new BusinessException(
				"portlet.crud.error.compoundsearch.unsupportedQuerySyntax",
				query.toString());
	}

	private Filter convertWildcardQuery(Table table, WildcardQuery query) {
		Term wildcard = query.getTerm();
		String columnName = wildcard.field();
		if (table.getContainer().getType(columnName) == null) {
			return null;
		}
		return new Wildcard(columnName, wildcard.text(), false);
	}

	private Filter convertPrefixQuery(Table table, PrefixQuery query) {
		Term prefix = query.getPrefix();
		String columnName = prefix.field();
		if (table.getContainer().getType(columnName) == null) {
			return null;
		}
		return new StartsWith(columnName, prefix.text(), false);
	}

	private Filter convertTermRangeQuery(Table table,
			TermRangeQuery termRangeQuery) {
		String columnName = termRangeQuery.getField();
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
		String columnName = term.field();
		Class<?> columnType = table.getContainer().getType(columnName);
		if (columnType == null) {
			return null;
		}
		String text = term.text();
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
			if (text.equals("*")) {
				return new Not(asList((Filter) new IsNull(columnName)));
			} else if (hasWildcards(text)) {
				return new Wildcard(columnName, text, false);
			} else {
				return new StartsWith(columnName, text, false);
			}
		}
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
			List<Filter> subList = convertBooleanClauses(table, booleanQuery);
			if (subList.size() == 0) {
				return null;
			} else if (subList.size() == 1) {
				return subList.get(0);
			} else {
				return new All(subList);
			}
		} else {
			List<Filter> subList = convertBooleanClauses(table, booleanQuery);
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

	private Filter convertPhraseQuery(Table table, PhraseQuery query) {
		PhraseQuery phraseQuery = (PhraseQuery) query;
		Term[] terms = phraseQuery.getTerms();
		String columnName = terms[0].field();
		if (table.getContainer().getType(columnName) == null) {
			return null;
		}
		String[] parts = new String[terms.length];
		int[] positions = phraseQuery.getPositions();
		for (int i = 0; i < terms.length; i++) {
			parts[positions[i]] = terms[i].text();
		}
		String phrase = Joiner.on(' ').join(parts);
		return new StartsWith(columnName, phrase, false);
	}

	private boolean hasWildcards(String text) {
		return text.contains("*") || text.contains("?");
	}

	private List<Filter> convertBooleanClauses(Table table,
			BooleanQuery booleanQuery) {
		List<Filter> subList = new ArrayList<Filter>(
				booleanQuery.getClauses().length);
		for (BooleanClause clause : booleanQuery.clauses()) {
			Filter subFilter = getFiltersForTable(table, clause.getQuery());
			if (subFilter != null) {
				if (clause.isProhibited()) {
					subFilter = new Not(asList(subFilter));
				}
				subList.add(subFilter);
			}
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

		Collection<String> defaultFields = getDefaultFields();
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
}

package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterListConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.IncludeFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;

class SearchFilterFieldsContainedInFormFields extends
		AbstractExplicitFiltersValidator {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchFilterFieldsContainedInFormFields.class);
	
    SearchFilterFieldsContainedInFormFields(ModelBuilder modelBuilder) {
        super(modelBuilder);
    }

    @Override
	protected void doValidate(Form aForm, FormAction searchActionWrapper,
			List<FilterConfig> appliedFilters) {

		final List<String> searchFieldNames = new ArrayList<String>();
		gatherFormFieldNamesInFilter(appliedFilters, searchFieldNames);

		final Collection<String> fieldNames = aForm.getFields().getNames();

		if (!fieldNames.containsAll(searchFieldNames)) {
			List<String> wrongFields = new ArrayList<String>(
					searchFieldNames);
			wrongFields.removeAll(fieldNames);
			throw new IllegalArgumentException("Die Felder ["
					+ StringUtils.join(wrongFields, ", ")
					+ "] sind nicht den durchsuchten Formularen verfügbar");
		}

	}

	static void gatherFormFieldNamesInFilter(List<FilterConfig> filters,
			final List<String> formFieldNames) {
		for (FilterConfig filterConf : filters) {
			if (filterConf instanceof ComparisonFilterConfig) {
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) filterConf;
				String column = comparisonFilterConfig.getField();
				if (column != null) {
					formFieldNames.add(column);
				}
			} else if (filterConf instanceof SQLFilterConfig) {
				SQLFilterConfig sqlFilterConfig = (SQLFilterConfig) filterConf;
				Set<String> fieldNames = extractFieldNames(sqlFilterConfig.getWhere());
				formFieldNames.addAll(fieldNames);
			} else if (filterConf instanceof FilterListConfig) {
				gatherFormFieldNamesInFilter(
						((FilterListConfig) filterConf).getFilters(),
						formFieldNames);
			} else if (filterConf instanceof CustomFilterConfig) {
				// Do Nothing - Groovy Script
			} else if (filterConf instanceof IncludeFilterConfig) {
				// Skip - will be checked separately
			} else {
				LOGGER.warn("CrudValidator does not cover {}", filterConf
						.getClass().getName());
			}
		}
	}

	static void extractFieldNamesFromExpression(String exprString, int startIndex, Set<String> result){
		final String FIELDS_EXPR = "fields.";
		
		int firstExprStartIndex = exprString.indexOf(FIELDS_EXPR, startIndex);
		while(firstExprStartIndex!=-1){
			int fieldStartIndex = firstExprStartIndex;
			int endIndex = exprString.indexOf(".", fieldStartIndex+FIELDS_EXPR.length());
			String fieldName = exprString.substring(fieldStartIndex+FIELDS_EXPR.length(), endIndex);
			result.add(fieldName);
			startIndex = endIndex+1;
			firstExprStartIndex = exprString.indexOf(FIELDS_EXPR, startIndex);
		}
	}
	
	static void doExtractFieldNames(String whereString, int startIndex, Set<String> result){
		final String FIELDS_EXPR = "$fields.";
		
		final int firstExprStartIndex = whereString.indexOf(FIELDS_EXPR, startIndex);
		if(firstExprStartIndex!=-1){
			int fieldStartIndex = firstExprStartIndex;
			int endIndex = whereString.indexOf(".", fieldStartIndex+FIELDS_EXPR.length());
			String fieldName = whereString.substring(fieldStartIndex+FIELDS_EXPR.length(), endIndex);
			result.add(fieldName);
			doExtractFieldNames(whereString, endIndex+1, result);
		}
		final String SECOND_FIELDS_EXPR = "${";
		final int secondExprStartIndex = whereString.indexOf(SECOND_FIELDS_EXPR, startIndex);
		if(secondExprStartIndex!=-1){
			int exprStartIndex = secondExprStartIndex;
			int exprEndIndex = whereString.indexOf("}", exprStartIndex);
			String subexpression = whereString.substring(secondExprStartIndex+SECOND_FIELDS_EXPR.length(), exprEndIndex);
			extractFieldNamesFromExpression(subexpression, 0, result);
			doExtractFieldNames(whereString, exprEndIndex+1, result);		
		}
		
	}
	
	static Set<String> extractFieldNames(String whereString) {
		Set<String> results = new LinkedHashSet<String>();
		doExtractFieldNames(whereString, 0, results);
		return results;
	}

}
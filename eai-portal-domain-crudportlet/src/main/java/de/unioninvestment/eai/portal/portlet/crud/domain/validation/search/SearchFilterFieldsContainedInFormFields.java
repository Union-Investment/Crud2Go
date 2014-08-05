package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
				// Do Nothing - no analysis of where statement
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

}
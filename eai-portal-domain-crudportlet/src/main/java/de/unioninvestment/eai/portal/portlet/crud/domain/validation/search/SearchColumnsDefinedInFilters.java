package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterListConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.IncludeFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

class SearchColumnsDefinedInFilters extends
		AbstractExplicitFiltersValidator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchColumnsDefinedInFilters.class);
	
    List<Table> defaultTables;

    SearchColumnsDefinedInFilters(ModelBuilder modelBuilder) {
        super(modelBuilder);
    }

    @Override
	protected void doValidate(Form aForm, FormAction searchActionWrapper,
			List<FilterConfig> appliedFilters) {

        defaultTables = ((SearchFormAction)searchActionWrapper.getActionHandler()).findSearchableTables(aForm);
        validateFilterList(aForm, searchActionWrapper, appliedFilters);
	}

    private void validateFilterList(Form aForm, FormAction searchActionWrapper, List<FilterConfig> filterList) {
        for (FilterConfig filter : filterList) {
            validateFilter(aForm, searchActionWrapper, filter);
        }
    }

    private void validateFilter(Form aForm, FormAction searchActionWrapper, FilterConfig filter) {
        Optional<String> columnName = getSearchColumnName(filter);
        if (columnName.isPresent()) {
            checkTablesForExistenceOf(columnName.get(), filter.getTable());
        }
        if (filter instanceof FilterListConfig) {
            validateFilterList(aForm, searchActionWrapper, ((FilterListConfig) filter).getFilters());
        }
    }

    private void checkTablesForExistenceOf(String columnName, String explicitTable) {
        for (Table table : defaultTables) {
            if (explicitTable != null && !explicitTable.equals(table.getId())) {
                continue;
            }
            if (table.getContainer().getColumns().contains(columnName)) {
                return;
            }
        }
        throw new IllegalArgumentException(
                "Die Spalte '"
                        + columnName
                        + "' ist nicht in den durchsuchten Tabellen verfügbar");
    }
    
	static Optional<String> getSearchColumnName(FilterConfig filterConf) {
        if (filterConf instanceof ComparisonFilterConfig) {
            ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) filterConf;
            String column = comparisonFilterConfig.getColumn();
            return Optional.of(column);

        } else if (filterConf instanceof SQLFilterConfig) {
            SQLFilterConfig sqlFilterConfig = (SQLFilterConfig) filterConf;
            String column = sqlFilterConfig.getColumn();
            return Optional.fromNullable(column);

        } else if (filterConf instanceof FilterListConfig) {
            // no column name
        } else if (filterConf instanceof CustomFilterConfig) {
            // Do Nothing - Groovy Script
        } else if (filterConf instanceof IncludeFilterConfig) {
            // Skip - will be checked separately
        } else {
            LOGGER.warn("CrudValidator does not cover {}", filterConf
                    .getClass().getName());
        }
        return Optional.absent();
    }

}
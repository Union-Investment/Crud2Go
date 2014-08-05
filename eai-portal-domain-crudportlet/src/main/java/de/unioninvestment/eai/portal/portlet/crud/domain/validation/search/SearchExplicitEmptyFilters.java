package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;

class SearchExplicitEmptyFilters extends AbstractExplicitFiltersValidator {
    SearchExplicitEmptyFilters(ModelBuilder modelBuilder) {
        super(modelBuilder);
    }

    @Override
	protected void doValidate(Form aForm, FormAction searchActionWrapper,
			List<FilterConfig> appliedFilters) {
		if (appliedFilters.isEmpty()) {
			throw new IllegalArgumentException(
					"Die Suche enthält explizit keine Filter");
		}
	}
}
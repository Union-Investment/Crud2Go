package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;

abstract class AbstractExplicitFiltersValidator implements
		SearchActionValidator {

    private ModelBuilder modelBuilder;

    protected AbstractExplicitFiltersValidator(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    @Override
	public void validateSearchAction(Form aForm,
			FormAction searchActionWrapper) {

		FormActionConfig actionConfig = (FormActionConfig) modelBuilder
				.getModelToConfigMapping().get(searchActionWrapper);

		if (actionConfig.getSearch() != null) {
			SearchConfig searchConfig = actionConfig.getSearch();
			if (searchConfig.getApplyFilters() == null) {
				return;
			}
			doValidate(aForm, searchActionWrapper, searchConfig
					.getApplyFilters().getFilters());
		}
	}

	protected abstract void doValidate(Form aForm,
			FormAction searchActionWrapper,
			List<FilterConfig> appliedFilters);

}
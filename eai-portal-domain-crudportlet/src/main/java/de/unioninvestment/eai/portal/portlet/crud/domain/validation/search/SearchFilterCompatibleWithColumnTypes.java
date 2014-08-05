package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormActionValidator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;

class SearchFilterCompatibleWithColumnTypes implements
		SearchActionValidator {
	@Override
	public void validateSearchAction(Form aForm,
			FormAction searchActionWrapper) {
		new SearchFormActionValidator(
				(SearchFormAction) searchActionWrapper.getActionHandler(),
				aForm).validate();
	}
}
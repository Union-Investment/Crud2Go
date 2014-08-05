package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;

interface SearchActionValidator {
	void validateSearchAction(Form Form, FormAction searchActionWrapper);
}
package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.validation.search.SearchFormActionsValidator;

public class ModelValidator {
	public void validateModel(ModelBuilder modelBuilder, 
			Portlet portletDomain, PortletConfig portletConfig){
		new SearchFormActionsValidator(modelBuilder).validate();
	}
}

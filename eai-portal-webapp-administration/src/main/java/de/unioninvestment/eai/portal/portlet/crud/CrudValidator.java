package de.unioninvestment.eai.portal.portlet.crud;

import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormActionValidator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet;

public class CrudValidator {
	
	private ModelBuilder modelBuilder; 
	private Portlet portletDomain;
	private ScriptPortlet scriptPortlet;
	
	public CrudValidator(ModelBuilder modelBuilder, Portlet portletDomain,
			ScriptPortlet scriptPortlet) {
		super();
		this.modelBuilder = modelBuilder;
		this.portletDomain = portletDomain;
		this.scriptPortlet = scriptPortlet;
	}

	public void validate(){
		validateSearchFormFilters();
	}

	void validateSearchFormFilters() {
		List<Form> forms = modelBuilder.getForms();
		
		for(Form aForm:forms){
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if(searchActionWrapper!=null){
				SearchFormAction searchAction = (SearchFormAction)searchActionWrapper.getActionHandler();
				new SearchFormActionValidator(searchAction, aForm).validate();
			}
		}
	}

}

package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import com.google.common.base.Optional;

import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// FIXME: JavaDoc für Klasse und Public Methoden
// FIXME: Umbenennen der Klasse: SearchFormActionsValidator und dafür ggf. kürzen der Methodennamen (um "Search")

public class SearchFormActionsValidator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchFormActionsValidator.class);

	private ModelBuilder modelBuilder;

	public SearchFormActionsValidator(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}

	public void validate() {
		checkExistenceOfExplicitEmptySearchFilters();
		checkExistenceOfTableColumnsReferencedInSearchFilters();
		checkExistenceOfFormFieldsReferencedInSearchFilters();
		checkFilterCompabilityWithColumnTypes();
	}

	private void checkExistenceOfExplicitEmptySearchFilters() {
		forEachSearchActionWrapper(new SearchExplicitEmptyFilters(modelBuilder));
	}

	private void checkExistenceOfFormFieldsReferencedInSearchFilters() {
		forEachSearchActionWrapper(new SearchFilterFieldsContainedInFormFields(modelBuilder));
	}

	private void checkExistenceOfTableColumnsReferencedInSearchFilters() {
		forEachSearchActionWrapper(new SearchColumnsDefinedInFilters(modelBuilder));
	}

	private void checkFilterCompabilityWithColumnTypes() {
		forEachSearchActionWrapper(new SearchFilterCompatibleWithColumnTypes());
	}

	private void forEachSearchActionWrapper(SearchActionValidator validator) {
		List<Form> forms = modelBuilder.getForms();

		for (Form aForm : forms) {
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if (searchActionWrapper != null) {
				validator.validateSearchAction(aForm, searchActionWrapper);
			}
		}
	}

}

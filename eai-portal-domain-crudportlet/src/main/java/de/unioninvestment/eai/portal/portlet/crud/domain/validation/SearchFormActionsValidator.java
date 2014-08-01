package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormActionValidator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

	interface SearchActionValidator {
		void validateSearchAction(Form Form, FormAction searchActionWrapper);
	}

	void forEachSearchActionWrapper(SearchActionValidator validator) {
		List<Form> forms = modelBuilder.getForms();

		for (Form aForm : forms) {
			FormActions actions = aForm.getActions();
			FormAction searchActionWrapper = actions.getSearchAction();
			if (searchActionWrapper != null) {
				validator.validateSearchAction(aForm, searchActionWrapper);
			}
		}
	}

	private void checkExistenceOfExplicitEmptySearchFilters() {
		forEachSearchActionWrapper(new SearchExplicitEmptyFilters());
	}

	private void checkExistenceOfFormFieldsReferencedInSearchFilters() {
		forEachSearchActionWrapper(new SearchFilterFieldsContainedInFormFields());
	}

	private void checkExistenceOfTableColumnsReferencedInSearchFilters() {
		forEachSearchActionWrapper(new SearchColumnsDefinedInQueries());
	}

	private void checkFilterCompabilityWithColumnTypes() {
		forEachSearchActionWrapper(new SearchFilterCompatibleWithColumnTypes());
	}

	static Set<String> gatherFormFieldsNames(Form aForm) {
		final Set<String> fieldNames = new LinkedHashSet<String>();
		for (FormField field : aForm.getFields()) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
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

	abstract class AbstractExplicitFiltersValidator implements
			SearchActionValidator {

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

	class SearchExplicitEmptyFilters extends AbstractExplicitFiltersValidator {
		@Override
		protected void doValidate(Form aForm, FormAction searchActionWrapper,
				List<FilterConfig> appliedFilters) {
			if (appliedFilters.isEmpty()) {
				throw new IllegalArgumentException(
						"Die Suche enthält explizit keine Filter");
			}
		}
	}

	class SearchFilterFieldsContainedInFormFields extends
			AbstractExplicitFiltersValidator {

		@Override
		protected void doValidate(Form aForm, FormAction searchActionWrapper,
				List<FilterConfig> appliedFilters) {

			final List<String> searchFieldNames = new ArrayList<String>();
			gatherFormFieldNamesInFilter(appliedFilters, searchFieldNames);

			final Set<String> fieldNames = gatherFormFieldsNames(aForm);

			if (!fieldNames.containsAll(searchFieldNames)) {
				List<String> wrongFields = new ArrayList<String>(
						searchFieldNames);
				wrongFields.removeAll(fieldNames);
				throw new IllegalArgumentException("Die Felder ["
						+ StringUtils.join(wrongFields, ", ")
						+ "] sind nicht den durchsuchten Formularen verfügbar");
			}

		}

	}

	interface FilterColumnValidator {
		void validate(String columnName, List<Table> tables);
	}
	
	class SearchColumnsDefinedInQueries extends
			AbstractExplicitFiltersValidator {

		
		@Override
		protected void doValidate(Form aForm, FormAction searchActionWrapper,
				List<FilterConfig> appliedFilters) {
			
			final List<String> searchColumnNames = new ArrayList<String>();
			gatherSearchColumnNames(appliedFilters, searchColumnNames);

			final Set<String> columnNames = getTableColumnNames(aForm,
					(SearchFormAction) searchActionWrapper
							.getActionHandler());

			if (searchColumnNames.size() > 0) {
				if (columnNames.size() > 0) {
					for (String name : searchColumnNames) {
						if (!columnNames.contains(name)) {
							throw new IllegalArgumentException(
									"Die Spalte '"
											+ name
											+ "' ist nicht den durchsuchten Tabellen verfügbar");
						}
					}
				} else {
					throw new IllegalArgumentException(
							"Config ist nicht richtig konfiguriert. Da gibt es Such Filtern, aber keine Tabellen für Suche");
				}
			}
		}
	}

	static void gatherSearchColumnNames(List<FilterConfig> filters,
			final List<String> searchColumnNames) {
		for (FilterConfig filterConf : filters) {
			if (filterConf instanceof ComparisonFilterConfig) {
				ComparisonFilterConfig comparisonFilterConfig = (ComparisonFilterConfig) filterConf;
				String column = comparisonFilterConfig.getColumn();
				if (column != null) {
					searchColumnNames.add(column);
				}
			} else if (filterConf instanceof SQLFilterConfig) {
				SQLFilterConfig sqlFilterConfig = (SQLFilterConfig) filterConf;
				String column = sqlFilterConfig.getColumn();
				if (column != null) {
					searchColumnNames.add(column);
				}
			} else if (filterConf instanceof FilterListConfig) {
				gatherSearchColumnNames(
						((FilterListConfig) filterConf).getFilters(),
						searchColumnNames);
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

	static Set<String> getTableColumnNames(Form aForm,
			SearchFormAction searchAction) {
		final Set<String> columnNames = new LinkedHashSet<String>();
		List<Table> searchableTables = searchAction.findSearchableTables(aForm);
		for (Table aTable : searchableTables) {
			columnNames.addAll(aTable.getContainer().getColumns());
		}
		return columnNames;
	}

	private final class SearchFilterCompatibleWithColumnTypes implements
			SearchActionValidator {
		@Override
		public void validateSearchAction(Form aForm,
				FormAction searchActionWrapper) {
			new SearchFormActionValidator(
					(SearchFormAction) searchActionWrapper.getActionHandler(),
					aForm).validate();
		}
	}

}

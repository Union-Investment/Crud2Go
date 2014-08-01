package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createAllFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createAnyFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createCustomFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createEqualsFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createIncludeFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createLessFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createNotFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createSqlWhereFilter;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;

import de.unioninvestment.eai.portal.portlet.crud.config.ApplyFiltersConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EqualsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

// FIXME Testabdeckung
public class SearchFormActionsValidatorTest {

	@Mock
	private ModelBuilder modelBuilderMock;
	
	@Mock
	private Form formMock;

	private SearchFormActionsValidator validator;

	@Mock
	private FormActions searchActionsMock;

	@Mock
	private FormAction searchActionMock;
	
	@Mock
	private SearchFormAction searchActionHandlerMock;

	private FormActionConfig searchFormActionConfig = new FormActionConfig();

	private SearchConfig searchConfig = new SearchConfig();

	private ApplyFiltersConfig applyFiltersConfig = new ApplyFiltersConfig();

	private EqualsFilterConfig equalsFilter = new EqualsFilterConfig();

	@Mock
	private FormFields formFieldsMock;

	@Mock
	private FormField formFieldMock;

	@Mock
	private Table tableMock, otherTableMock;
	
	@Mock
	private DataContainer containerMock, otherContainerMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		searchFormActionConfig.setSearch(searchConfig);
		searchConfig.setApplyFilters(applyFiltersConfig);
		applyFiltersConfig.getFilters().add(equalsFilter);
		equalsFilter.setColumn("COLUMN");
		equalsFilter.setField("FIELD");
		
		when(formMock.getActions()).thenReturn(searchActionsMock);
		when(formMock.getFields()).thenReturn(formFieldsMock);
		when(formFieldsMock.iterator()).thenReturn(asList(formFieldMock).iterator());
		when(formFieldMock.getName()).thenReturn("FIELD");
		
		when(modelBuilderMock.getForms()).thenReturn(asList(formMock));
		when(searchActionsMock.getSearchAction()).thenReturn(searchActionMock);
		
		when(searchActionMock.getActionHandler()).thenReturn(searchActionHandlerMock);
		when(searchActionHandlerMock.findSearchableTables(formMock)).thenReturn(asList(tableMock, otherTableMock));
		
		when(modelBuilderMock.getModelToConfigMapping()).thenReturn(ImmutableMap.<Object,Object>of(searchActionMock, searchFormActionConfig));
		when(tableMock.getContainer()).thenReturn(containerMock);
		when(containerMock.getColumns()).thenReturn(asList("COLUMN"));

		when(otherTableMock.getContainer()).thenReturn(otherContainerMock);
		when(otherContainerMock.getColumns()).thenReturn(asList("OTHER_COLUMN"));

		validator = new SearchFormActionsValidator(modelBuilderMock);
	}
	
	@Test
	public void shouldAcceptValidSearches() {
		validator.validate();
	}

	@Test
	public void shouldAcceptSearchesThatContainNoExplicitFilters() {
		searchConfig.setApplyFilters(null);
		validator.validate();
	}

	@Test
	public void shouldFailIfExplicitFilteringContainsNoFilters() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Die Suche enthält explizit keine Filter");

		applyFiltersConfig.getFilters().clear();
		validator.validate();
	}
	
	@Test
	public void shouldFailIfFormFieldDefinedInFilterDoesNotExist() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Die Felder [NON_EXISTING_FIELD] sind nicht den durchsuchten Formularen verfügbar");

		equalsFilter.setField("NON_EXISTING_FIELD");
		validator.validate();
	}

	@Test
	public void shouldFailIfColumnDefinedInFilterDoesNotExistInTable() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Die Spalte 'NON_EXISTING_COLUMN' ist nicht den durchsuchten Tabellen verfügbar");
		
		equalsFilter.setColumn("NON_EXISTING_COLUMN");
		validator.validate();
	}

	@Test @Ignore
	public void shouldFailIfColumnDefinedExplicitlyInFilterDoesNotExistInSpecificTable() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Die Spalte 'COLUMN' ist nicht den durchsuchten Tabellen verfügbar");
		
		equalsFilter.setTable("OTHER_TABLE");
		validator.validate();
	}

	

	public static void checkGatherSearchNames(FilterConfig filter, String ... expectedColumns){
		List<String> searchColumns = new ArrayList<String>();
		List<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(filter);
		SearchFormActionsValidator.gatherSearchColumnNames(filters, searchColumns);
		Assert.assertEquals(Arrays.asList(expectedColumns), searchColumns);
	}
	
	public static void checkGatherFieldNames(FilterConfig filter, String ... expectedFields){
		List<String> formFieldNames = new ArrayList<String>();
		List<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(filter);
		SearchFormActionsValidator.gatherFormFieldNamesInFilter(filters, formFieldNames);
		Assert.assertEquals(Arrays.asList(expectedFields), formFieldNames);
	}
	
	@Test
	public void gatherColumnNames_ComparisonFilterConfig(){
		checkGatherSearchNames(createEqualsFilter("field", "EQUALS_COLUMN"), "EQUALS_COLUMN");
	}

	@Test
	public void gatherFieldNames_ComparisonFilterConfig(){
		checkGatherFieldNames(createEqualsFilter("field", "EQUALS_COLUMN"), "field");
	}

	
	@Test
	public void gatherColumnNames_SQLFilterConfig(){
		checkGatherSearchNames(createSqlWhereFilter("COLUMN", "some sql query"), "COLUMN");
	}

	@Test
	public void gatherFieldNames_SQLFilterConfig(){
		//No information should be checked hier
		checkGatherFieldNames(createSqlWhereFilter("COLUMN", "some sql query"));
	}

	
	@Test
	public void gatherColumnNames_AnyFilterConfig(){
		checkGatherSearchNames(createAnyFilter(
				createEqualsFilter("field_1", "COLUMN_1"),
				createLessFilter("field_2", "COLUMN_2")), "COLUMN_1", "COLUMN_2");
	}

	@Test
	public void gatherFieldNames_AnyFilterConfig(){
		//No information should be checked hier
		checkGatherFieldNames(createAnyFilter(
				createEqualsFilter("field_1", "COLUMN_1"),
				createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
	}

	
	@Test
	public void gatherColumnNames_AllFilterConfig(){
		checkGatherSearchNames(createAllFilter(
					createEqualsFilter("field_1", "COLUMN_1"),
					createLessFilter("field_2", "COLUMN_2")), 
					"COLUMN_1", "COLUMN_2"
				);
	}

	@Test
	public void gatherFieldNames_AllFilterConfig(){
		//No information should be checked hier
		checkGatherFieldNames(createAllFilter(
				createEqualsFilter("field_1", "COLUMN_1"),
				createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
	}

	
	@Test
	public void gatherColumnNames_NotFilterConfig(){
		checkGatherSearchNames(
				createNotFilter(
					createEqualsFilter("field_1", "COLUMN_1"),
					createLessFilter("field_2", "COLUMN_2")),
					"COLUMN_1", "COLUMN_2");
	}

	@Test
	public void gatherFieldNames_NotFilterConfig(){
		//No information should be checked here
		checkGatherFieldNames(createNotFilter(
				createEqualsFilter("field_1", "COLUMN_1"),
				createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
	}

	
	@Test
	public void gatherColumnNames_CustomFilterConfig(){
		//"Columns from custom filter should not be checked - i.e. that a groovy script"
		checkGatherSearchNames(createCustomFilter());
	}

	@Test
	public void gatherFieldNames_CustomFilterConfig(){
		//No information should be checked here
		checkGatherFieldNames(createCustomFilter());
	}

	
	@Test
	public void gatherColumnNames_IncludeFilterConfig(){
		//"Columns from include filter should not be checked"
		checkGatherSearchNames(createIncludeFilter());
	}

	@Test
	public void gatherFieldNames_IncludeFilterConfig(){
		//No information should be checked here
		checkGatherFieldNames(createIncludeFilter());
	}

}

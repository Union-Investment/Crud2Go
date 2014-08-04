package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import com.google.common.collect.ImmutableMap;
import de.unioninvestment.eai.portal.portlet.crud.config.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

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
		when(formFieldsMock.getNames()).thenReturn(asList("FIELD"));

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
	public void shouldFailIfColumnDefinedInComparisionFilterDoesNotExistInTable() {
        expectColumnExistenceError(createEqualsFilter("field", "NON_EXISTING_COLUMN"));
	}

    @Test
    public void shouldFailIfColumnDefinedInSQLFilterDoesNotExistInTable(){
         expectColumnExistenceError(createSqlWhereFilter("NON_EXISTING_COLUMN", "some sql query"));
    }

    @Test
    public void shouldIgnoreCustomFilterInColumnNameCheck(){
        applyFiltersConfig.getFilters().add(createIncludeFilter());
        validator.validate();
    }

    @Test
    public void shouldIgnoreIncludeFilterInColumnNameCheck(){
        applyFiltersConfig.getFilters().add(createCustomFilter());
        validator.validate();
    }

    private void expectColumnExistenceError(FilterConfig filter) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Die Spalte 'NON_EXISTING_COLUMN' ist nicht in den durchsuchten Tabellen verfügbar");

        applyFiltersConfig.getFilters().add(filter);
        validator.validate();
    }

    @Test
	public void shouldFailIfColumnDefinedExplicitlyInFilterDoesNotExistInSpecificTable() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Die Spalte 'COLUMN' ist nicht in den durchsuchten Tabellen verfügbar");
		
		equalsFilter.setTable("OTHER_TABLE");
		validator.validate();
	}

    @Test
    public void shouldFailIfColumnDefinedExplicitlyInSubFilterDoesNotExist() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Die Spalte 'NON_EXISTING_COLUMN' ist nicht in den durchsuchten Tabellen verfügbar");

        AllFilterConfig allFilterConfig = new AllFilterConfig();
        allFilterConfig.getFilters().add(equalsFilter);
        applyFiltersConfig.getFilters().clear();
        applyFiltersConfig.getFilters().add(allFilterConfig);

        equalsFilter.setColumn("NON_EXISTING_COLUMN");
        validator.validate();
    }



}

package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.*;

// FIXME Testabdeckung
public class CrudValidatorTest {

	public static void checkGatherSearchNames(FilterConfig filter, String ... expectedColumns){
		List<String> searchColumns = new ArrayList<String>();
		List<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(filter);
		CrudValidator.gatherSearchColumnNames(filters, searchColumns);
		Assert.assertEquals(Arrays.asList(expectedColumns), searchColumns);
	}
	
	public static void checkGatherFieldNames(FilterConfig filter, String ... expectedFields){
		List<String> formFieldNames = new ArrayList<String>();
		List<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(filter);
		CrudValidator.gatherFormFieldNamesInFilter(filters, formFieldNames);
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

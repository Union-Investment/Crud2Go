package de.unioninvestment.eai.portal.portlet.crud.domain.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.*;

public class CrudValidatorTest {

	public static void checkGatherSearchNames(FilterConfig filter, String ... expectedColumns){
		List<String> searchColumns = new ArrayList<String>();
		List<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(filter);
		CrudValidator.gatherSearchColumnNames(filters, searchColumns);
		Assert.assertEquals(Arrays.asList(expectedColumns), searchColumns);
	}
	
	@Test
	public void gatherColumnNames_ComparisonFilterConfig(){
		checkGatherSearchNames(createEqualsFilter("field", "EQUALS_COLUMN"), "EQUALS_COLUMN");
	}
	
	@Test
	public void gatherColumnNames_SQLFilterConfig(){
		checkGatherSearchNames(createSqlWhereFilter("COLUMN", "some sql query"), "COLUMN");
	}
	
	@Test
	public void gatherColumnNames_AnyFilterConfig(){
		checkGatherSearchNames(createAnyFilter(
				createEqualsFilter("field_1", "COLUMN_1"),
				createLessFilter("field_2", "COLUMN_2")), "COLUMN_1", "COLUMN_2");
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
	public void gatherColumnNames_NotFilterConfig(){
		checkGatherSearchNames(
				createNotFilter(
					createEqualsFilter("field_1", "COLUMN_1"),
					createLessFilter("field_2", "COLUMN_2")),
					"COLUMN_1", "COLUMN_2");
	}

	@Test
	public void gatherColumnNames_CustomFilterConfig(){
		//"Columns from custom filter should not be checked - i.e. that a groovy script"
		checkGatherSearchNames(createCustomFilter());
	}

	@Test
	public void gatherColumnNames_IncludeFilterConfig(){
		//"Columns from include filter should not be checked"
		checkGatherSearchNames(createIncludeFilter());
	}


}

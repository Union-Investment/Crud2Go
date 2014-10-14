package de.unioninvestment.eai.portal.portlet.crud.domain.validation.search;

import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.*;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createIncludeFilter;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createLessFilter;

/**
 * Created by cmj on 04.08.14.
 */
public class SearchFilterFieldsContainedInFormFieldsTest {

    @Test
    public void gatherFieldNames_ComparisonFilterConfig(){
        checkGatherFieldNames(createEqualsFilter("field", "EQUALS_COLUMN"), "field");
    }


    @Test
    public void gatherFieldNames_SQLFilterConfig(){
        //No information should be checked hier
        checkGatherFieldNames(createSqlWhereFilter("COLUMN", "some sql query"));
    }

    @Test
    public void gatherFieldNames_SQLFilterConfig_CaseField(){
        //No information should be checked hier
        checkGatherFieldNames(createSqlWhereFilter("COLUMN", "LIKE ${fields.fonds_id.value?.toUpperCase()}||'%'"), "fonds_id");
    }

    @Test
    public void gatherFieldNames_SQLFilterConfig_CaseField_2(){
        //No information should be checked hier
        checkGatherFieldNames(createSqlWhereFilter("COLUMN", "in (select x.kofo_fonds_id from ko_fonds x where x.kofo_kofg_id = $fields.fondsgroup.value)"), "fondsgroup");
    }

    @Test
    public void gatherFieldNames_SQLFilterConfig_CaseManyFields_1(){
        //No information should be checked hier
        checkGatherFieldNames(createSqlWhereFilter("COLUMN", "LIKE ${( fields.started.value=='Y' && fields.node.value != null) ? '%'+fields.node.value+'%' : '%'}"), "started", "node");
    }

    @Test
    public void gatherFieldNames_SQLFilterConfig_CaseManyFields_2(){
        //No information should be checked hier
        checkGatherFieldNames(createSqlWhereFilter("FONDS_ID", "in ("
        		+ "select KOFO_FONDS_ID from KO_FONDS where KOFO_KOFG_ID = $fields.fondsgroup.value "
        		+ "UNION "
        		+ "select KOFO_FONDS_ID from KO_FONDS_AWV where KOFO_KOFG_ID = $fields.fondsgroup.value "
        		+ "UNION "
    + "select KOFO_FONDS_ID from KO_FONDS_BVI where KOFO_KOFG_ID = $fields.fondsgroup.value )"
), "fondsgroup");
    }

    

    @Test
    public void gatherFieldNames_AnyFilterConfig(){
        //No information should be checked hier
        checkGatherFieldNames(createAnyFilter(
                createEqualsFilter("field_1", "COLUMN_1"),
                createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
    }

    @Test
    public void gatherFieldNames_AllFilterConfig(){
        //No information should be checked hier
        checkGatherFieldNames(createAllFilter(
                createEqualsFilter("field_1", "COLUMN_1"),
                createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
    }

    @Test
    public void gatherFieldNames_CustomFilterConfig(){
        //No information should be checked here
        checkGatherFieldNames(createCustomFilter());
    }

    @Test
    public void gatherFieldNames_NotFilterConfig(){
        //No information should be checked here
        checkGatherFieldNames(createNotFilter(
                createEqualsFilter("field_1", "COLUMN_1"),
                createLessFilter("field_2", "COLUMN_2")), "field_1", "field_2");
    }

    @Test
    public void gatherFieldNames_IncludeFilterConfig(){
        //No information should be checked here
        checkGatherFieldNames(createIncludeFilter());
    }

    public static void checkGatherFieldNames(FilterConfig filter, String ... expectedFields){
        List<String> formFieldNames = new ArrayList<String>();
        List<FilterConfig> filters = new ArrayList<FilterConfig>();
        filters.add(filter);
        SearchFilterFieldsContainedInFormFields.gatherFormFieldNamesInFilter(filters, formFieldNames);
        Assert.assertEquals(Arrays.asList(expectedFields), formFieldNames);
    }
}

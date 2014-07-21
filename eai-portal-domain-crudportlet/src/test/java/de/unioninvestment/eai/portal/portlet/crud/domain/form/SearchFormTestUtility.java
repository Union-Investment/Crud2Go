package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import java.util.Arrays;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.config.AllFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AnyFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CheckboxConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ContainsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DateConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EndsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EqualsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.LessFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.LessOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.NotFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegExpFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StartsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.StaticOptionList;

import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.*;

public class SearchFormTestUtility {

	static FilterConfig createAnyFilter(FilterConfig... subfilters) {
		AnyFilterConfig config = new AnyFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	static FilterConfig createAllFilter(FilterConfig... subfilters) {
		AllFilterConfig config = new AllFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	static FilterConfig createNotFilter(FilterConfig... subfilters) {
		NotFilterConfig config = new NotFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	static FormFieldConfig createFormFieldConfig(String name, String title,
			String inputPrompt) {
		FormFieldConfig config = new FormFieldConfig();
		config.setName(name);
		config.setTitle(title);
		config.setInputPrompt(inputPrompt);
		return config;
	}

	static FormField createFormField(String name, String title,
			String inputPrompt, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		FormField field = new FormField(config);
		field.setValue(value);
		return field;
	}

	static FormField createDateFormField(String name, String title,
			String inputPrompt, String format, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		config.setDate(new DateConfig());
		config.getDate().setFormat(format);
		DateFormField field = new DateFormField(config);
		field.setValue(value);
		return field;
	}

	static FormField createSelectFormField(String name, String title,
			String inputPrompt, String value, Map<String, String> selections) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		StaticOptionList selection = new StaticOptionList(selections);
		OptionListFormField field = new OptionListFormField(config, selection);
		field.setValue(value);
		return field;
	}

	static FormField createCheckboxFormField(String name, String title,
			String inputPrompt, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		CheckboxConfig cbc = new CheckboxConfig();
		config.setCheckbox(cbc);
	
		CheckBoxFormField field = new CheckBoxFormField(config);
		field.setValue(value);
		return field;
	}

	static FilterConfig createSqlWhereFilter(String columnName,
			String whereCondition) {
		SQLFilterConfig filter = new SQLFilterConfig();
		filter.setColumn(columnName);
		filter.setWhere(whereCondition);
		return filter;
	}

	static ContainsFilterConfig createContainsFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		ContainsFilterConfig filter = new ContainsFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	static RegExpFilterConfig createRegexpFilter(String fieldName,
			String columnName, String modifiers) {
		RegExpFilterConfig filter = new RegExpFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setModifiers(modifiers);
		return filter;
	}

	static EndsWithFilterConfig createEndsWithFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		EndsWithFilterConfig filter = new EndsWithFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	static StartsWithFilterConfig createStartsWithFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		StartsWithFilterConfig filter = new StartsWithFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	static LessOrEqualFilterConfig createLessOrEqualsFilter(String fieldName,
			String columnName) {
		LessOrEqualFilterConfig filter = new LessOrEqualFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	static CustomFilterConfig createCustomFilter() {
		CustomFilterConfig filter = new CustomFilterConfig();
		filter.setFilter(new GroovyScript("abcde"));
		return filter;
	}

	static LessFilterConfig createLessFilter(String fieldName,
			String columnName) {
		LessFilterConfig filter = new LessFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	static GreaterOrEqualFilterConfig createGreaterOrEqualsFilter(
			String fieldName, String columnName) {
		GreaterOrEqualFilterConfig filter = new GreaterOrEqualFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	static GreaterFilterConfig createGreaterFilter(String fieldName,
			String columnName) {
		GreaterFilterConfig filter = new GreaterFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	static EqualsFilterConfig createEqualsFilter(String fieldName,
			String columnName) {
		EqualsFilterConfig filter = new EqualsFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

}

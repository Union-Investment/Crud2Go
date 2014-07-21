package de.unioninvestment.eai.portal.portlet.crud.domain.form;


import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createDateFormField;
import static de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormTestUtility.createFormField;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.config.ComparisonFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ContainsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EndsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegExpFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StartsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.InitializingUI;

public class SearchFormActionValidatorTest {
	@SuppressWarnings("serial")
	static abstract class UIMock extends UI implements InitializingUI {
	}
	
	private SearchFormActionValidator searchFormActionValidator;
	
	@Mock
	private SearchFormAction searchAction;
	

	private FormFields formFields;

	@Mock
	private Form formMock;

	@Mock
	private Table tableMock;
	
	@Mock
	private DataContainer dataContainerMock;
	

	@Mock
	private OptionListFormField selectionFormField;

	@Mock
	private UIMock uiMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(selectionFormField.getName()).thenReturn("selection1");
		when(selectionFormField.getValue()).thenReturn("value1");

		when(tableMock.getId()).thenReturn("table");

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"), selectionFormField);
		when(formMock.getFields()).thenReturn(formFields);

		// register application with thread
		UI.setCurrent(uiMock);

		when(uiMock.getLocale()).thenReturn(Locale.GERMANY);
		
		searchFormActionValidator = new SearchFormActionValidator(searchAction, formMock);
	}

	@After
	public void tearDown() {
		// deregister from thread
		UI.setCurrent(null);
	}

	static FormField emptyField() {
		return createFormField("test-field", "title", "prompt", "");
	}
	
	static FormField emptyDateField() {
		return createDateFormField("test-field", "title",
				"prompt", "dd.MM.yyyy", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_StartsWith_NumericField(){
		checkComparisonFilterColumnType(Number.class, new StartsWithFilterConfig(), emptyField());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_EndsWith_NumericField(){
		checkComparisonFilterColumnType(Number.class, new EndsWithFilterConfig(), emptyField());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_Contains_NumericField(){
		checkComparisonFilterColumnType(Number.class, new ContainsFilterConfig(), emptyField());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_RegExp_NumericField(){
		checkComparisonFilterColumnType(Number.class, new RegExpFilterConfig(), emptyField());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_StartsWith_DateField(){
		checkComparisonFilterColumnType(Date.class, new StartsWithFilterConfig(), emptyDateField());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_EndsWith_DateField(){
		checkComparisonFilterColumnType(Date.class, new EndsWithFilterConfig(), emptyDateField());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_Contains_DateField(){
		checkComparisonFilterColumnType(Date.class, new ContainsFilterConfig(), emptyDateField());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldRaiseError_RegExp_DateField(){
		checkComparisonFilterColumnType(Date.class, new RegExpFilterConfig(), emptyDateField());
	}


	@Test
	public void noError_StartsWith_StringField(){
		checkComparisonFilterColumnType(String.class, new StartsWithFilterConfig(), emptyField());
	}
	
	@Test
	public void noError_EndsWith_StringField(){
		checkComparisonFilterColumnType(String.class, new EndsWithFilterConfig(), emptyField());
	}
	
	@Test
	public void noError_Contains_StringField(){
		checkComparisonFilterColumnType(String.class, new ContainsFilterConfig(), emptyField());
	}

	@Test
	public void noError_RegExp_StringField(){
		checkComparisonFilterColumnType(String.class, new RegExpFilterConfig(), emptyField());
	}

	
	private void checkComparisonFilterColumnType(final Class<?> aClass,
			ComparisonFilterConfig aFilterConfig, FormField createFormField) {
		when(searchAction.findSearchableTables(formMock)).thenReturn(Collections.singletonList(tableMock));
		
		
		when(dataContainerMock.getType(any(String.class))).thenAnswer(
				new Answer<Class<?>>() {
					@Override
					public Class<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return aClass;
					}
				});

		when(tableMock.getContainer()).thenReturn(dataContainerMock);
		
		ComparisonFilterConfig filterConfig = aFilterConfig;
		
		filterConfig.setField("test-field");
		
		
		List<FilterConfig> filterConfigList = Arrays.<FilterConfig>asList(filterConfig);

		when(searchAction.createFilterConfigs(formMock, tableMock)).thenReturn(filterConfigList);
		when(searchAction.filterMatchesTable(tableMock, filterConfig)).thenReturn(true);
		
		FormFields formFields = new FormFields(createFormField);
		
		when(formMock.getFields()).thenReturn(formFields);
		
		searchFormActionValidator.validate();
	}

}

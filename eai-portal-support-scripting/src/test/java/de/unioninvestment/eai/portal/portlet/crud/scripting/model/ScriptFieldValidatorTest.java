package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;

public class ScriptFieldValidatorTest {

	private ScriptFieldValidator validator;

	@Mock
	private ScriptTable tableMock;

	@Mock
	private Closure<Object> closureMock;

	@Mock
	private Field<?> fieldMock;
	
	@Captor
	private ArgumentCaptor<ScriptValidator> scriptValidatorCaptor;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		validator = new ScriptFieldValidator(tableMock, closureMock, "Test");
		validator.apply(fieldMock);
		verify(fieldMock).addValidator(scriptValidatorCaptor.capture());
	}

	@Test
	public void shouldAcceptBooleanTrueFromClosure() {
		when(closureMock.call(tableMock, "Test")).thenReturn(true);
		scriptValidatorCaptor.getValue().validate("Test");
		verify(closureMock).call(tableMock, "Test");
	}

	@Test
	public void shouldRejectBooleanFalseFromClosureUsingConfiguredMessage() {
		thrown.expect(InvalidValueException.class);
		thrown.expectMessage("Test");
		
		when(closureMock.call(tableMock, "Test")).thenReturn(false);
		scriptValidatorCaptor.getValue().validate("Test");
		verify(closureMock).call("Test");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldRejectBooleanFalseFromClosureUsingDefaultMessage() {
		reset(fieldMock);
		validator = new ScriptFieldValidator(tableMock, closureMock, null);
		validator.apply(fieldMock);
		verify(fieldMock).addValidator(scriptValidatorCaptor.capture());

		thrown.expect(InvalidValueException.class);
		thrown.expectMessage("#portlet.crud.error.validation.defaultValidatorMessage");
		
		when(closureMock.call(tableMock, "Test")).thenReturn(false);
		scriptValidatorCaptor.getValue().validate("Test");
		verify(closureMock).call(tableMock, "Test");
	}

	@Test
	public void shouldTreatExceptionFromClosureAsValidationError() {
		thrown.expect(InvalidValueException.class);
		thrown.expectMessage("Bla");
		
		when(closureMock.call(tableMock, "Test")).thenThrow(new IllegalArgumentException("Bla"));
		scriptValidatorCaptor.getValue().validate("Test");
	}
}

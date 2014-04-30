package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import com.vaadin.data.Validator;

import de.unioninvestment.eai.portal.support.vaadin.context.Context;

/**
 * Vaaadin Validator that delegates to a Groovy {@link Closure}.
 * 
 * The validator has multiple options to communicate a failure.
 * 
 * @author cmj
 * 
 */
@SuppressWarnings("serial")
public class ScriptValidator implements Validator {

	private Closure<?> closure;
	private String defaultMessage;
	private ScriptTable table;

	/**
	 * @param closure
	 *            the closure that validates
	 * @param message
	 *            the error message to show if none is given by the closure
	 */
	public ScriptValidator(ScriptTable table, Closure<?> closure, String message) {
		this.table = table;
		this.closure = closure;
		this.defaultMessage = message;
		if (defaultMessage == null) {
			defaultMessage = Context
					.getMessage("portlet.crud.error.validation.defaultValidatorMessage");
		}
	}

	/**
	 * Validates by calling the given closure. If the closure returns true or
	 * nothing, validation is successful. If the closure returns
	 * <code>false</code>, the validation fails with the configured or default
	 * error message. If an exception is thrown, the exception message text is
	 * 
	 * @see com.vaadin.data.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(Object value) throws InvalidValueException {
		boolean failed = false;
		String message = null;
		try {
			Object result = closure.call(table, value);
			if (result instanceof Boolean && result.equals(Boolean.FALSE)) {
				failed = true;
			}

		} catch (Exception e) {
			failed = true;
			message = e.getMessage();
		}
		if (failed) {
			if (message == null) {
				message = defaultMessage;
			}
			throw new InvalidValueException(message);
		}
	}
}
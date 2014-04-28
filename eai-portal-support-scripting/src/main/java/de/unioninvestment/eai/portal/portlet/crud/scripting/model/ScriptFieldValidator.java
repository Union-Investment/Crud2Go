package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import com.vaadin.data.Validator;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

@SuppressWarnings("serial")
public class ScriptFieldValidator implements FieldValidator {

	final class ScriptValidator implements Validator {
		@Override
		public void validate(Object value) throws InvalidValueException {
			try {
				Object result = closure.call(value);

				if (result instanceof Boolean && result.equals(Boolean.FALSE)) {

					if (message == null) {
						message = Context
								.getMessage("portlet.crud.error.validation.defaultValidatorMessage");
					}
					throw new InvalidValueException(message);
				}
			} catch (Exception e) {
				throw new InvalidValueException(e.getMessage());
			}
		}
	}

	private Closure<?> closure;
	private String message;

	public ScriptFieldValidator(Closure<?> closure, String message) {
		this.closure = closure;
		this.message = message;
	}

	@Override
	public void apply(Field<?> field) {
		field.addValidator(new ScriptValidator());
	}
}
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

/**
 * FieldValidator that delegates validation to a {@link Closure} via
 * {@link ScriptValidator}.
 * 
 * @author cmj
 * 
 */
public class ScriptFieldValidator implements FieldValidator {

	private Closure<?> closure;
	private String message;
	private ScriptTable table;

	public ScriptFieldValidator(ScriptTable scriptTable, Closure<?> closure, String message) {
		this.table = scriptTable;
		this.closure = closure;
		this.message = message;
	}

	@Override
	public void apply(Field<?> field) {
		field.addValidator(new ScriptValidator(table, closure, message));
	}
}
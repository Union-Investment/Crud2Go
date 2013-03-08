package de.unioninvestment.eai.portal.support.scripting;

import groovy.lang.Closure;

import org.springframework.util.Assert;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterMatcher;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

/**
 * Matcher that delegates to a {@link Closure}.
 * 
 * @author carsten.mjartan
 */
public class ScriptCustomFilterMatcher implements CustomFilterMatcher {

	private Closure<Object> closure;

	/**
	 * @param closure
	 *            the Closure to delegate to
	 */
	public ScriptCustomFilterMatcher(Closure<Object> closure) {
		Assert.notNull(closure, "Custom Filter Closure is null");
		this.closure = closure;
	}

	@Override
	public boolean matches(ContainerRow row) {
		ScriptRow scriptRow = new ScriptRow(row);
		Object result = closure.call(scriptRow);
		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		} else {
			throw new NullPointerException(
					"CustomFilter Closure has to return true or false, not null");
		}
	}

}

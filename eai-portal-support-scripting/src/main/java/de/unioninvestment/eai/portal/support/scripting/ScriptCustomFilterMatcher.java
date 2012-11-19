package de.unioninvestment.eai.portal.support.scripting;

import groovy.lang.Closure;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterMatcher;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

public class ScriptCustomFilterMatcher implements CustomFilterMatcher {

	private Closure<Boolean> closure;

	public ScriptCustomFilterMatcher(Closure<Boolean> closure) {
		this.closure = closure;
	}

	@Override
	public boolean matches(ContainerRow row) {
		ScriptRow scriptRow = new ScriptRow(row);
		return closure.call(scriptRow);
	}

}

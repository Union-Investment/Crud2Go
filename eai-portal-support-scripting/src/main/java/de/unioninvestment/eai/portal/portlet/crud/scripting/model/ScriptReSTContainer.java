package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;

public class ScriptReSTContainer extends ScriptContainer {

	private ReSTContainer container;

	public ScriptReSTContainer(ReSTContainer container) {
		super(container);
		this.container = container;
	}

	public void setBaseUrl(String newBaseUrl) {
		container.setBaseUrl(newBaseUrl);
	}

	public void setQueryUrl(String newQueryUrl) {
		container.setQueryUrl(newQueryUrl);
	}

}

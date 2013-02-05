package de.unioninvestment.eai.portal.support.vaadin.container;

public class UpdateContext {

	private boolean refreshRequired = false;

	public boolean isRefreshRequired() {
		return refreshRequired;
	}

	public void requireRefresh() {
		refreshRequired = true;
	}

}

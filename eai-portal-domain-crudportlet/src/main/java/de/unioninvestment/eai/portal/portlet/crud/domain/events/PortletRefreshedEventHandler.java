package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventHandler;

public interface PortletRefreshedEventHandler extends EventHandler {
	void onPortletRefresh(PortletRefreshedEvent event);
}

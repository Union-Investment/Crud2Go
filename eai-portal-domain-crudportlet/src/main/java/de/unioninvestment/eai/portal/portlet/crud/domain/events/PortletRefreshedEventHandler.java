package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventHandler;

/**
 * Handler for refreshed portlet
 * 
 * @author carsten.mjartan
 */
public interface PortletRefreshedEventHandler extends EventHandler {
	/**
	 * @param event
	 *            the refresh event
	 */
	void onPortletRefresh(PortletRefreshedEvent event);
}

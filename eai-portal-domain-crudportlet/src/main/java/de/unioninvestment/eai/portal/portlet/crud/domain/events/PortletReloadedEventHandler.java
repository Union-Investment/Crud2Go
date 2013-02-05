package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventHandler;

/**
 * Event Handler for reload events
 * 
 * @author carsten.mjartan
 */
public interface PortletReloadedEventHandler extends EventHandler {
	/**
	 * @param event
	 *            the reloaded event
	 */
	void onPortletReload(PortletReloadedEvent event);
}

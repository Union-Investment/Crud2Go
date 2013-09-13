package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

public abstract class VolatileOptionList implements OptionList,
		PortletRefreshedEventHandler {

	private static final long serialVersionUID = 1L;
	
	private EventRouter<OptionListChangeEventHandler, OptionListChangeEvent> changeEventRouter = new EventRouter<OptionListChangeEventHandler, OptionListChangeEvent>();

	/**
	 * @param eventBus
	 *            zur Registrierung für {@link PortletRefreshedEvent}.
	 */
	public VolatileOptionList(EventBus eventBus) {
		eventBus.addHandler(PortletRefreshedEvent.class, this);
	}

	@Override
	public void addChangeListener(OptionListChangeEventHandler handler) {
		changeEventRouter.addHandler(handler);
	}

	@Override
	public void removeChangeListener(OptionListChangeEventHandler handler) {
		changeEventRouter.removeHandler(handler);
	}

	protected void fireChangeEvent(boolean initialized) {
		changeEventRouter.fireEvent(new OptionListChangeEvent(
				this, initialized));
	}

	/**
	 * Refreshes the List on {@link PortletRefreshedEvent}
	 */
	@Override
	public void onPortletRefresh(PortletRefreshedEvent event) {
		refresh();
	}

	public abstract void refresh();
}
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.Map;
import java.util.Map.Entry;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

public abstract class VolatileOptionList implements OptionList,
		PortletRefreshedEventHandler {

	private static final long serialVersionUID = 1L;
	
	public enum RefreshPolicy {
		FROM_CACHE,
		FROM_SOURCE
	}
	
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
		refresh(RefreshPolicy.FROM_CACHE);
	}

	public void refresh() {
		refresh(RefreshPolicy.FROM_SOURCE);
	}
	
	public abstract void refresh(RefreshPolicy policy);
	
	@Override
	public String getTitle(String key, SelectionContext context) {
		Map<String, String> options = getOptions(context);
		if (options != null) {
			return options.get(key);
		}
		return null;
	}

	@Override
	public String getKey(String title, SelectionContext context) {
		Map<String, String> options = getOptions(context);
		if (options != null) {
			for (Entry<String, String> entry : options.entrySet()) {
				if (entry.getValue().equals(title)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

}
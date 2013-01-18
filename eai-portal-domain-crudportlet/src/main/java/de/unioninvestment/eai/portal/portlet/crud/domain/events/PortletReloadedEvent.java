package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * This is fired if a global refresh of all components of a {@link Portlet} is
 * requested.
 * 
 * @author carsten.mjartan
 */
public class PortletReloadedEvent implements
		SourceEvent<PortletReloadedEventHandler, Portlet> {

	private static final long serialVersionUID = 1L;

	private Portlet source;

	/**
	 * @param source
	 *            the reloaded portlet
	 */
	public PortletReloadedEvent(Portlet source) {
		this.source = source;
	}

	@Override
	public void dispatch(PortletReloadedEventHandler eventHandler) {
		eventHandler.onPortletReload(this);
	}

	@Override
	public Portlet getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PortletReloadedEvent other = (PortletReloadedEvent) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}

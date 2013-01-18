package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * This is fired if a global refresh of all components of a {@link Portlet} is
 * requested.
 * 
 * @author carsten.mjartan
 */
public class PortletRefreshedEvent implements
		SourceEvent<PortletRefreshedEventHandler, Portlet> {

	private Portlet source;

	public PortletRefreshedEvent(Portlet source) {
		this.source = source;
	}

	@Override
	public void dispatch(PortletRefreshedEventHandler eventHandler) {
		eventHandler.onPortletRefresh(this);
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
		PortletRefreshedEvent other = (PortletRefreshedEvent) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}

package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * Container für JMX-Operationen
 * 
 * @author carsten.mjartan
 */
public class JMXContainer extends GenericDataContainer {

	private static final long serialVersionUID = 1L;

	public JMXContainer(EventBus eventBus, Map<String, String> formatPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy) {
		super(eventBus, formatPattern, defaultOrder, filterPolicy);
	}

}

package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * Implementierung des GenericContainer, die ReST-Operationen unterstützt.
 * 
 * @author carsten.mjartan
 */
public class ReSTContainer extends GenericDataContainer {

	private static final long serialVersionUID = 1L;

	public ReSTContainer(EventBus eventBus, Map<String, String> formatPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy) {
		super(eventBus, formatPattern, defaultOrder, filterPolicy);
	}

}

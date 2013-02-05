package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;

public interface PayloadCreator {

	String getMimeType();

	byte[] create(GenericItem item, GroovyScript conversionScript,
			String charset);

}

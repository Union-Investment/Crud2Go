package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;

/**
 * Extension of GenericDelegate for ReST Backend.
 * 
 * @author carsten.mjartan
 */
public interface ReSTDelegate extends GenericDelegate {

	void setBaseUrl(String newBaseUrl);

	void setQueryUrl(String newQueryUrl);

}

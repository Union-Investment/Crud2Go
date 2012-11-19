package de.unioninvestment.eai.portal.portlet.crud.domain.model.filter;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;

/**
 * Domain Interface for matching rows for the {@link CustomFilter}.
 * 
 * @author carsten.mjartan
 */
public interface CustomFilterMatcher {

	/**
	 * @param row
	 *            the current Row
	 * 
	 * @return <code>true</code>, if the row matches the custom filter criteria
	 */
	boolean matches(ContainerRow row);
}

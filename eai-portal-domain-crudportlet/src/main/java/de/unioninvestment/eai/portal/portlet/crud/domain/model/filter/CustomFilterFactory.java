package de.unioninvestment.eai.portal.portlet.crud.domain.model.filter;

import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;

/**
 * Factory for {@link CustomFilter}, that is needed for supporting Groovy
 * Scripting the {@link CustomFilterMatcher}.
 * 
 * @author carsten.mjartan
 */
public interface CustomFilterFactory {

	CustomFilter createCustomFilter(CustomFilterConfig config);
}

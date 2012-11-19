package de.unioninvestment.eai.portal.portlet.crud.domain.model.filter;

/**
 * Filterklasse für eigene Filterkriterien. Dies funktioniert nicht mit den
 * SQL-Backends (siehe {@link SQLFilter}).
 * 
 * @author carsten.mjartan
 */
public class CustomFilter extends Filter {

	private final CustomFilterMatcher matcher;

	public CustomFilter(CustomFilterMatcher matcher, boolean durable) {
		super(durable);
		this.matcher = matcher;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * @return the {@link CustomFilterMatcher}
	 */
	public CustomFilterMatcher getMatcher() {
		return matcher;
	}

}

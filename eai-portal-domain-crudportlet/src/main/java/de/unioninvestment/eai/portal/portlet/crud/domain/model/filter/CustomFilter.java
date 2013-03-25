package de.unioninvestment.eai.portal.portlet.crud.domain.model.filter;

/**
 * Filterklasse für eigene Filterkriterien. Dies funktioniert nicht mit den
 * SQL-Backends (siehe {@link SQLFilter}).
 * 
 * @author carsten.mjartan
 */
public class CustomFilter extends Filter {

	private final CustomFilterMatcher matcher;

	/**
	 * @param matcher
	 *            a custom matcher
	 * 
	 * @param durable
	 *            if the filter is durable
	 */
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

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomFilter other = (CustomFilter) obj;
		if (matcher == null) {
			if (other.matcher != null)
				return false;
		} else if (!matcher.equals(other.matcher))
			return false;
		return true;
	}

}

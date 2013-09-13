package de.unioninvestment.eai.portal.support.vaadin.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * Custom In-Memory Vaadin Filter that delegates to a Matcher Interface.
 * 
 * @author carsten.mjartan
 */
public class CustomFilter implements Filter {

	private static final long serialVersionUID = 1L;

	/**
	 * Matcher Interface.
	 * 
	 * @author carsten.mjartan
	 */
	public interface Matcher {
		boolean matches(Object itemId, Item item);
	}

	private Matcher matcher;

	/**
	 * @param matcher
	 *            the delegate interface
	 */
	public CustomFilter(Matcher matcher) {
		this.matcher = matcher;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		return matcher.matches(itemId, item);
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return true;
	}

}

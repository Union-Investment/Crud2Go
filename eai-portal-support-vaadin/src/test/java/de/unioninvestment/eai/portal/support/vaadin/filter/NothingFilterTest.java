package de.unioninvestment.eai.portal.support.vaadin.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NothingFilterTest {

	@Test
	public void shouldAlwaysRejectARow() {
		assertThat(new NothingFilter().passesFilter(null, null), is(false));
	}

	@Test
	public void shouldApplyToAllProperties() {
		assertThat(new NothingFilter().appliesToProperty("anyProperty"),
				is(true));
	}
}

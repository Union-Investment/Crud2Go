package de.unioninvestment.eai.portal.support.vaadin.container;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UpdateContextTest {

	@Test
	public void shouldReturnRefreshNotRequiredByDefault() {
		UpdateContext context = new UpdateContext();
		assertThat(context.isRefreshRequired(), is(false));
	}

	@Test
	public void shouldAllowRequiringRefresh() {
		UpdateContext context = new UpdateContext();
		context.requireRefresh();
		assertThat(context.isRefreshRequired(), is(true));
	}
}

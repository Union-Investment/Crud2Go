package de.unioninvestment.eai.portal.support.vaadin.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NothingFilterTranslatorTest {

	@Test
	public void shouldMatchToNothingFilter() {
		assertThat(
				new NothingFilterTranslator()
						.translatesFilter(new NothingFilter()),
				is(true));
	}

	@Test
	public void shouldMatch() {
		String whereString = new NothingFilterTranslator()
				.getWhereStringForFilter(new NothingFilter(), null);
		assertThat(whereString, is("1 = 0"));
	}

}

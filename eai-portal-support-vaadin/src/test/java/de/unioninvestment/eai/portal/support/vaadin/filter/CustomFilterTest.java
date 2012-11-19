package de.unioninvestment.eai.portal.support.vaadin.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.support.vaadin.filter.CustomFilter.Matcher;

public class CustomFilterTest {

	@Mock
	private Matcher matcherMock;

	@Mock
	private Object itemIdMock;

	@Mock
	private Item itemMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldDelegateToMatcherForFiltering() {
		CustomFilter filter = new CustomFilter(matcherMock);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(false));
		verify(matcherMock).matches(itemIdMock, itemMock);
	}

	@Test
	public void shouldDelegateToMatcherForFilteringToTrue() {
		CustomFilter filter = new CustomFilter(matcherMock);
		when(matcherMock.matches(itemIdMock, itemMock)).thenReturn(true);
		assertThat(filter.passesFilter(itemIdMock, itemMock), is(true));
	}
}

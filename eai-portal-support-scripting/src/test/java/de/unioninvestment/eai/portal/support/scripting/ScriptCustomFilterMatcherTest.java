package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

public class ScriptCustomFilterMatcherTest {

	@Mock
	private Closure<Boolean> closureMock;

	@Mock
	private ContainerRow rowMock;

	@Captor
	private ArgumentCaptor<ScriptRow> scriptRowCaptor;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnMissingClosure() {
		new ScriptCustomFilterMatcher(null);
	}

	@Test
	public void shouldDelegateMatchingToClosure() {
		ScriptCustomFilterMatcher matcher = new ScriptCustomFilterMatcher(
				closureMock);
		when(closureMock.call(scriptRowCaptor.capture())).thenReturn(true);

		assertThat(matcher.matches(rowMock), is(true));
	}

	@Test
	public void shouldWriteHelpfulMessagOnNPE() {
		ScriptCustomFilterMatcher matcher = new ScriptCustomFilterMatcher(
				closureMock);
		when(closureMock.call(scriptRowCaptor.capture())).thenReturn(null);

		thrown.expect(NullPointerException.class);
		thrown.expectMessage("CustomFilter Closure has to return true or false, not null");

		matcher.matches(rowMock);
	}
}

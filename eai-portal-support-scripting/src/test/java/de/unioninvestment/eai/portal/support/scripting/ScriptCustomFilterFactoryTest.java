package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilter;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormAction;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

public class ScriptCustomFilterFactoryTest {

	@Mock
	private ScriptFormAction formActionMock;

	@Mock
	private ScriptBuilder scriptBuilderMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateCustomFilterFromConfig() {
		ScriptCustomFilterFactory factory = new ScriptCustomFilterFactory(
				scriptBuilderMock, formActionMock);

		CustomFilterConfig config = new CustomFilterConfig();
		config.setFilter(new GroovyScript("abcde"));
		final Closure<Boolean> closure = new Closure<Boolean>(
				ScriptCustomFilterFactoryTest.this) {
			public Boolean doCall(ScriptRow row) {
				return true;
			}
		};
		when(scriptBuilderMock.buildClosure(config.getFilter())).thenAnswer(
				new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return closure;
					}
				});

		CustomFilter customFilter = factory.createCustomFilter(config);

		assertThat(customFilter.getMatcher(),
				instanceOf(ScriptCustomFilterMatcher.class));
	}
}

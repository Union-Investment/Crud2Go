package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
		final Closure<Object> closure = new Closure<Object>(
				ScriptCustomFilterFactoryTest.this) {
			public Object doCall(ScriptRow row) {
				return true;
			}
		};
		when(scriptBuilderMock.buildClosure(config.getFilter())).thenReturn(
				closure);
		CustomFilter customFilter = factory.createCustomFilter(config);

		assertThat(customFilter.getMatcher(),
				instanceOf(ScriptCustomFilterMatcher.class));
	}
}

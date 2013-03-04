package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public class JsonParserTest {

	@Mock
	private ScriptBuilder scriptBuilderMock;
	private JsonParser parser;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		parser = new JsonParser(new ReSTContainerConfig(), scriptBuilderMock);
	}

	@Test
	public void shouldParseJSon() {
		StringReader reader = new StringReader("[1,2]");

		Object data = parser.parseData(reader);

		assertThat(data, is((Object) asList(1, 2)));
	}
}
